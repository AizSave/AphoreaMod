package aphorea.buffs.Trinkets.Healing;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FloralRingBuff extends TrinketBuff {
    int count;

    public FloralRingBuff() {
    }

    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
        count = 100;
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (count == 0) {
            AphMagicHealing.healMob(buff.owner, buff.owner, 3, null, null);
            count = 100;
        }
        count--;
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "floralring", "healing", AphMagicHealing.getMagicHealing(perspective, perspective, 3)));
        return tooltips;
    }
}
