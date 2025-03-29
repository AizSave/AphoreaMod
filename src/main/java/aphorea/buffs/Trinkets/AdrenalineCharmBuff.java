package aphorea.buffs.Trinkets;

import aphorea.buffs.AphShownBuff;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
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
        tooltips.add(Localization.translate("itemtooltip", "adrenalinecharm", "damage", "5", "armor", "-5"));
        tooltips.add(Localization.translate("itemtooltip", "adrenalinecharm2"));
        return tooltips;
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        buff.owner.addBuff(new ActiveBuff("adrenalinecharmcharge", buff.owner, 20000, null), false);
    }

    public static class AdrenalineCharmChargeBuff extends AphShownBuff {
        @Override
        public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
            buff.addModifier(BuffModifiers.ALL_DAMAGE, 0.05F);
            buff.addModifier(BuffModifiers.ARMOR, -0.05F);
        }

        @Override
        public int getStackSize(ActiveBuff buff) {
            return 5;
        }
    }
}
