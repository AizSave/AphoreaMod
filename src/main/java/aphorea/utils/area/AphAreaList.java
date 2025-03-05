package aphorea.utils.area;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.*;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AphAreaList {
    public AphArea[] areas;

    public AphAreaList(AphArea... areas) {
        this.areas = areas;

        for (int i = 0; i < areas.length; i++) {
            areas[i].position = i;
            areas[i].antRange = i == 0 ? 0 : areas[i - 1].range;
            areas[i].currentRange = areas[i].range;
            areas[i].range = areas[i].range + areas[i].antRange;
        }
    }

    public AphAreaList add(AphArea... areas) {
        for (int i = 0; i < areas.length; i++) {
            areas[i].position = i;
            areas[i].antRange = i == 0 ? 0 : areas[i - 1].range;
            areas[i].currentRange = areas[i].range;
            areas[i].range = areas[i].range + areas[i].antRange;
        }
        return this;
    }

    public void showAllAreaParticles(Level level, float x, float y, float rangeModifier, float borderParticleModifier, float innerParticleModifier, int particleTime) {
        Arrays.stream(areas).forEach((AphArea area) -> area.showAreaParticles(level, x, y, this, null, rangeModifier, borderParticleModifier, innerParticleModifier, particleTime));
    }

    public void showAllAreaParticles(Level level, float x, float y, float rangeModifier, float borderParticleModifier, float innerParticleModifier) {
        showAllAreaParticles(level, x, y, rangeModifier, borderParticleModifier, innerParticleModifier, (int) (Math.random() * 200) + 900);
    }

    public void showAllAreaParticles(Level level, float x, float y, float rangeModifier, float particleModifier, int particleTime) {
        showAllAreaParticles(level, x, y, rangeModifier, particleModifier, particleModifier, particleTime);
    }

    public void showAllAreaParticles(Level level, float x, float y, float rangeModifier, float particleModifier) {
        showAllAreaParticles(level, x, y, rangeModifier, particleModifier, particleModifier, (int) (Math.random() * 200) + 900);
    }

    public void showAllAreaParticles(Level level, float x, float y, float rangeModifier, int particleTime) {
        showAllAreaParticles(level, x, y, rangeModifier, 1, 0.2F, particleTime);
    }

    public void showAllAreaParticles(Level level, float x, float y, float rangeModifier) {
        showAllAreaParticles(level, x, y, rangeModifier, 1, 0.2F, (int) (Math.random() * 200) + 900);
    }

    public void showAllAreaParticles(Level level, float x, float y) {
        showAllAreaParticles(level, x, y, 1);
    }

    public void executeAreas(Mob attacker, float x, float y, float rangeModifier, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        if (attacker.isServer()) {
            int range = Math.round(this.areas[this.areas.length - 1].range * rangeModifier);

            attacker.getLevel().entityManager.streamAreaMobsAndPlayers(x, y, range).forEach(
                    (Mob target) -> {
                        for (AphArea area : this.areas) {
                            area.execute(attacker, target, x, y, rangeModifier, item, toolItem);
                        }
                    }
            );

        }
    }

    public void executeAreas(Mob attacker, float x, float y, float rangeModifier) {
        executeAreas(attacker, x, y, rangeModifier, null, null);
    }

    public void executeAreas(Mob attacker, float x, float y) {
        executeAreas(attacker, x, y, 1F);
    }

    public void execute(Mob attacker, float x, float y, float rangeModifier, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        if(attacker.isServer()) {
            executeAreas(attacker, x, y, rangeModifier, item, toolItem);
        }
        if(attacker.isClient()) {
            showAllAreaParticles(attacker.getLevel(), x, y, rangeModifier);
        }
    }

    public void execute(Mob attacker, float x, float y, float rangeModifier) {
        execute(attacker, x, y, rangeModifier, null, null);
    }

    public void execute(Mob attacker, float x, float y) {
        execute(attacker, x, y, 1F);
    }

    public void executeAreas(Mob attacker) {
        executeAreas(attacker, attacker.x, attacker.y);
    }

    public boolean someType(AphAreaType type) {
        return Arrays.stream(areas).anyMatch(a -> a.areaTypes.contains(type));
    }

    public AphAreaList setDamageType(DamageType damageType) {
        Arrays.stream(areas).forEach(area -> area.damageType = damageType);
        return this;
    }

    public void addAreasToolTip(ListGameTooltips tooltips, Attacker attacker, boolean forceLines, InventoryItem item, ToolItem toolItem) {
        boolean lines = areas.length > 1 || forceLines;
        if (lines) {
            tooltips.add(Localization.translate("itemtooltip", "line"));
        }

        for (int i = 0; i < areas.length; i++) {
            AphArea area = areas[i];

            if (lines) {
                tooltips.add(Localization.translate("itemtooltip", "areatip", "number", i + 1));
            }

            if (area.areaTypes.contains(AphAreaType.DAMAGE)) {
                float damage = area.getDamage(item).damage;
                tooltips.add(area.damageType.getDamageTip((int) damage).toTooltip(GameColor.GREEN.color.get(), GameColor.RED.color.get(), GameColor.YELLOW.color.get(), false));
            }

            if (area.areaTypes.contains(AphAreaType.HEALING)) {
                int healing = AphMagicHealing.getMagicHealing((Mob) attacker, null, area.getHealing(item), toolItem, item);
                tooltips.add(Localization.translate("itemtooltip", "magichealingtip", "health", healing));
            }

            tooltips.add(Localization.translate("itemtooltip", "rangetip", "range", area.currentRange));

            if (lines) {
                tooltips.add(Localization.translate("itemtooltip", "line"));
            }
        }
    }

    public void addAreasStatTip(ItemStatTipList list, ToolItem toolItem, InventoryItem currentItem, InventoryItem lastItem, Attacker attacker, boolean forceAdd) {
        boolean multipleAreas = areas.length > 1;
        if (multipleAreas) {
            StringItemStatTip lineTip = new LocalMessageStringItemStatTip("itemtooltip", "line", "none", "none");
            list.add(100, lineTip);
        }

        for (int i = 0; i < areas.length; i++) {
            AphArea area = areas[i];

            if (multipleAreas) {
                StringItemStatTip areasTip = new LocalMessageStringItemStatTip("itemtooltip", "areatip", "number", String.valueOf(i + 1));
                list.add(100, areasTip);
            }

            if (area.areaTypes.contains(AphAreaType.DAMAGE)) {

                float damage = area.getDamage(currentItem).damage;
                float lastDamage = lastItem == null ? -1 : area.getDamage(lastItem).damage;
                if (damage > 0 || lastDamage > 0 || forceAdd) {
                    DoubleItemStatTip tip = area.damageType.getDamageTip((int) damage);

                    if (lastItem != null) {
                        tip.setCompareValue(lastDamage);
                    }

                    list.add(100, tip);
                }
            }

            if (area.areaTypes.contains(AphAreaType.HEALING)) {

                int healing = AphMagicHealing.getMagicHealing((Mob) attacker, null, area.getHealing(currentItem), toolItem, currentItem);
                DoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", healing, 0);

                if (lastItem != null) {
                    int lastHealing = AphMagicHealing.getMagicHealing((Mob) attacker, null, area.getHealing(lastItem), toolItem, lastItem);
                    tip.setCompareValue(lastHealing);
                }

                list.add(100, tip);

            }

            if (area.areaTypes.contains(AphAreaType.BUFF)) {

                Arrays.stream(area.buffs).forEach(
                        buffID -> {
                            Buff buff = BuffRegistry.getBuff(buffID);

                            StringItemStatTip tip = new LocalMessageStringItemStatTip("itemtooltip", "areabuff", "buff", Localization.translate("itemtooltip", "areabuffdisplay", "buff", buff.getLocalization(), "duration", (float) area.buffDuration / 1000));
                            list.add(100, tip);

                        }
                );

            }

            if (area.areaTypes.contains(AphAreaType.DEBUFF)) {

                Arrays.stream(area.debuffs).forEach(
                        debuffID -> {
                            Buff debuff = BuffRegistry.getBuff(debuffID);

                            StringItemStatTip tip = new LocalMessageStringItemStatTip("itemtooltip", "areadebuff", "debuff", Localization.translate("itemtooltip", "areabuffdisplay", "buff", debuff.getLocalization(), "duration", (float) area.buffDuration / 1000));
                            list.add(100, tip);

                        }
                );

            }

            DoubleItemStatTip rangeTip = new LocalMessageDoubleItemStatTip("itemtooltip", "rangetip", "range", area.currentRange, 0);
            list.add(100, rangeTip);

            if (multipleAreas) {
                StringItemStatTip lineTip = new LocalMessageStringItemStatTip("itemtooltip", "line", "none", "none");
                list.add(100, lineTip);
            }
        }
    }

}
