package aphorea.buffs.Trinkets;

import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealingFunctions;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

abstract public class AphAreaWhenHealTrinketBuff extends TrinketBuff implements AphMagicHealingFunctions {
    public int healingToArea;
    public Map<String, Integer> healingDone = new HashMap<>();

    public AphAreaList areaList;

    public AphAreaWhenHealTrinketBuff(int healingToArea, AphAreaList areaList) {
        this.healingToArea = healingToArea;
        this.areaList = areaList;
    }

    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    public void onMagicalHealing(Mob healer, Mob target, int healing, int realHealing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        if (healer.isServer()) {
            String playerName = ((PlayerMob) healer).playerName;

            int thisHealingDone = healingDone.getOrDefault(playerName, 0) + realHealing;

            if (thisHealingDone >= healingToArea) {
                thisHealingDone = 0;

                this.areaList.execute(healer, true);
            }

            healingDone.put(playerName, thisHealingDone);
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        areaList.addAreasToolTip(tooltips, perspective, true, null, null);
        tooltips.add(Localization.translate("itemtooltip", "areawhenheal", "magichealing", healingToArea));
        return tooltips;
    }
}
