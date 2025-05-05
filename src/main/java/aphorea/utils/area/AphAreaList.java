package aphorea.utils.area;

import aphorea.packets.AphAreaShowPacket;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.*;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AphAreaList {
    public final List<AphArea> areas = new ArrayList<>();

    public AphAreaList(@NotNull AphArea... areas) {
        for (AphArea area : areas) {
            addArea(area);
        }
    }

    public AphAreaList addAreas(@NotNull AphArea... areas) {
        for (AphArea area : areas) {
            addArea(area);
        }
        return this;
    }

    public AphAreaList addArea(@NotNull AphArea area) {
        if (areas.isEmpty()) {
            area.position = 0;
            area.antRange = 0;
            area.currentRange = area.range;
        } else {
            AphArea antArea = areas.get(areas.size() - 1);
            area.position = antArea.position + 1;
            area.antRange = antArea.range;
            area.currentRange = area.range;
            area.range = area.range + area.antRange;
        }
        areas.add(area);
        return this;
    }

    public void executeClient(Level level, float x, float y, float rangeModifier, float borderParticleModifier, float innerParticleModifier, int particleTime) {
        areas.forEach((AphArea area) -> {
            area.showParticles(level, x, y, null, rangeModifier, borderParticleModifier, innerParticleModifier, particleTime);
        });
    }

    public void executeClient(Level level, float x, float y, float rangeModifier, float borderParticleModifier, float innerParticleModifier) {
        executeClient(level, x, y, rangeModifier, borderParticleModifier, innerParticleModifier, (int) (Math.random() * 200) + 900);
    }

    public void executeClient(Level level, float x, float y, float rangeModifier) {
        executeClient(level, x, y, rangeModifier, 1, 0.2F, (int) (Math.random() * 200) + 900);
    }

    public void executeClient(Level level, float x, float y) {
        executeClient(level, x, y, 1);
    }

    public void executeServer(@NotNull Mob attacker, float x, float y, float rangeModifier, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        if (attacker.isServer()) {
            int range = Math.round(this.areas.get(this.areas.size() - 1).range * rangeModifier);

            attacker.getLevel().entityManager.streamAreaMobsAndPlayers(x, y, range).forEach(
                    (Mob target) -> {
                        for (AphArea area : this.areas) {
                            area.executeServer(attacker, target, x, y, rangeModifier, item, toolItem);
                        }
                    }
            );
        }
    }

    public void executeServer(Mob attacker, float x, float y, float rangeModifier) {
        executeServer(attacker, x, y, rangeModifier, null, null);
    }

    public void executeServer(Mob attacker, float x, float y) {
        executeServer(attacker, x, y, 1F);
    }

    public void executeServer(Mob attacker) {
        executeServer(attacker, attacker.x, attacker.y);
    }

    public void sendExecutePacket(@NotNull Level level, float x, float y, float rangeModifier) {
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtEntireLevel(new AphAreaShowPacket(x, y, this, rangeModifier), level);
        }
    }

    public void sendExecutePacket(@NotNull Level level, float x, float y) {
        this.sendExecutePacket(level, x, y, 1F);
    }

    public void execute(@NotNull Mob attacker, float x, float y, float rangeModifier, @Nullable InventoryItem item, @Nullable ToolItem toolItem, boolean sendPacket) {
        if (attacker.isServer()) {
            executeServer(attacker, x, y, rangeModifier, item, toolItem);
            if (sendPacket) {
                sendExecutePacket(attacker.getLevel(), x, y, rangeModifier);
            }
        }
        if (!sendPacket && attacker.isClient()) {
            executeClient(attacker.getLevel(), x, y, rangeModifier);
        }
    }

    public void execute(Mob attacker, float x, float y, float rangeModifier, boolean sendPacket) {
        execute(attacker, x, y, rangeModifier, null, null, sendPacket);
    }

    public void execute(Mob attacker, float x, float y, boolean sendPacket) {
        execute(attacker, x, y, 1F, sendPacket);
    }

    public void execute(Mob attacker, boolean sendPacket) {
        execute(attacker, attacker.x, attacker.y, sendPacket);
    }

    public boolean someType(AphAreaType type) {
        return areas.stream().anyMatch(a -> a.areaTypes.contains(type));
    }

    public void addAreasToolTip(ListGameTooltips tooltips, Attacker attacker, boolean forceLines, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        boolean lines = areas.size() > 1 || forceLines;
        if (lines) {
            tooltips.add(Localization.translate("itemtooltip", "line"));
        }

        for (int i = 0; i < areas.size(); i++) {
            AphArea area = areas.get(i);

            if (lines && areas.size() > 1) {
                tooltips.add(Localization.translate("itemtooltip", "areatip", "number", i + 1));
            }

            if (area.areaTypes.contains(AphAreaType.DAMAGE)) {
                tooltips.add(area.getDamage().type.getDamageTip((int) area.getDamage().damage).toTooltip(GameColor.GREEN.color.get(), GameColor.RED.color.get(), GameColor.YELLOW.color.get(), false));
            }

            if (area.areaTypes.contains(AphAreaType.HEALING)) {
                int healing = AphMagicHealing.getMagicHealing((Mob) attacker, null, area.getHealing(), toolItem, item);
                tooltips.add(Localization.translate("itemtooltip", "magichealingtip", "health", healing));
            }

            if (area.areaTypes.contains(AphAreaType.BUFF)) {
                Arrays.stream(area.buffs).forEach(
                        buffID -> {
                            Buff buff = BuffRegistry.getBuff(buffID);
                            tooltips.add(Localization.translate("itemtooltip", "areabuff", "buff", Localization.translate("itemtooltip", "areabuffdisplay", "buff", buff.getLocalization(), "duration", (float) area.buffDuration / 1000)));
                        }
                );
            }

            if (area.areaTypes.contains(AphAreaType.DEBUFF)) {
                Arrays.stream(area.debuffs).forEach(
                        buffID -> {
                            Buff buff = BuffRegistry.getBuff(buffID);
                            tooltips.add(Localization.translate("itemtooltip", "areadebuff", "buff", Localization.translate("itemtooltip", "areabuffdisplay", "buff", buff.getLocalization(), "duration", (float) area.debuffDuration / 1000)));
                        }
                );
            }


            tooltips.add(Localization.translate("itemtooltip", "rangetip", "range", area.currentRange));

            if (lines) {
                tooltips.add(Localization.translate("itemtooltip", "line"));
            }
        }
    }

    public static void addAreasStatTip(ItemStatTipList list, AphAreaList currentAreas, AphAreaList lastAreas, Attacker attacker, boolean forceAdd, @Nullable InventoryItem lastItem, @Nullable InventoryItem currentItem, @Nullable ToolItem toolItem) {
        addAreasStatTip(list, currentAreas, lastAreas, attacker, forceAdd, lastItem, currentItem, toolItem, 2000);
    }

    public static void addAreasStatTip(ItemStatTipList list, AphAreaList currentAreas, AphAreaList lastAreas, Attacker attacker, boolean forceAdd, @Nullable InventoryItem lastItem, @Nullable InventoryItem currentItem, @Nullable ToolItem toolItem, int priority) {
        boolean multipleAreas = currentAreas.areas.size() > 1;
        StringItemStatTip lineTip = new LocalMessageStringItemStatTip("itemtooltip", "line", "none", "none");
        list.add(priority, lineTip);

        for (int i = 0; i < currentAreas.areas.size(); i++) {
            AphArea currentArea = currentAreas.areas.get(i);
            AphArea lastArea = lastAreas == null ? null : lastAreas.areas.get(i);

            if (multipleAreas) {
                StringItemStatTip areasTip = new LocalMessageStringItemStatTip("itemtooltip", "areatip", "number", String.valueOf(i + 1));
                list.add(priority, areasTip);
            }

            if (currentArea.areaTypes.contains(AphAreaType.DAMAGE)) {

                float damage = currentArea.getDamage().damage;
                float lastDamage = lastArea == null ? -1 : lastArea.getDamage().damage;
                if (damage > 0 || lastDamage > 0 || forceAdd) {
                    DoubleItemStatTip tip = currentArea.getDamage().type.getDamageTip((int) damage);

                    if (lastArea != null) {
                        tip.setCompareValue(lastDamage);
                    }

                    list.add(priority, tip);
                }
            }

            if (currentArea.areaTypes.contains(AphAreaType.HEALING)) {

                int healing = AphMagicHealing.getMagicHealing((Mob) attacker, null, currentArea.getHealing(), toolItem, currentItem);
                DoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", healing, 0);

                if (lastArea != null) {
                    int lastHealing = AphMagicHealing.getMagicHealing((Mob) attacker, null, lastArea.getHealing(), toolItem, lastItem);
                    tip.setCompareValue(lastHealing);
                }

                list.add(priority, tip);

            }

            if (currentArea.areaTypes.contains(AphAreaType.BUFF)) {

                Arrays.stream(currentArea.buffs).forEach(
                        buffID -> {
                            Buff buff = BuffRegistry.getBuff(buffID);

                            StringItemStatTip tip = new LocalMessageStringItemStatTip("itemtooltip", "areabuff", "buff", Localization.translate("itemtooltip", "areabuffdisplay", "buff", buff.getLocalization(), "duration", (float) currentArea.buffDuration / 1000));
                            list.add(priority, tip);

                        }
                );

            }

            if (currentArea.areaTypes.contains(AphAreaType.DEBUFF)) {

                Arrays.stream(currentArea.debuffs).forEach(
                        debuffID -> {
                            Buff debuff = BuffRegistry.getBuff(debuffID);

                            StringItemStatTip tip = new LocalMessageStringItemStatTip("itemtooltip", "areadebuff", "buff", Localization.translate("itemtooltip", "areabuffdisplay", "buff", debuff.getLocalization(), "duration", (float) currentArea.buffDuration / 1000));
                            list.add(priority, tip);

                        }
                );

            }

            DoubleItemStatTip rangeTip = new LocalMessageDoubleItemStatTip("itemtooltip", "rangetip", "range", currentArea.currentRange, 0);
            list.add(priority, rangeTip);

            lineTip = new LocalMessageStringItemStatTip("itemtooltip", "line", "none", "none");
            list.add(priority, lineTip);

        }
    }


}
