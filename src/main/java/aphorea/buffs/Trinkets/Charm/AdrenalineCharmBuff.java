package aphorea.buffs.Trinkets.Charm;

import aphorea.buffs.AdrenalineBuff;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class AdrenalineCharmBuff extends TrinketBuff {
    public AdrenalineCharmBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "adrenalinecharm"));
        tooltips.add(Localization.translate("itemtooltip", "adrenaline"));
        return tooltips;
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        AdrenalineBuff.giveAdrenaline(buff.owner, 20000, true);
    }

}
