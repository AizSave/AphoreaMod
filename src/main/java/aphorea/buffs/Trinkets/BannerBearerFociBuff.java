package aphorea.buffs.Trinkets;

import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class BannerBearerFociBuff extends TrinketBuff {
    public BannerBearerFociBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(AphModifiers.BANNER_EFFECT, 0.4F);
        buff.setModifier(AphModifiers.BANNER_ABILITY_SPEED, 0.2F);
        buff.setModifier(BuffModifiers.MELEE_DAMAGE, -0.2F);
        buff.setModifier(BuffModifiers.RANGED_DAMAGE, -0.2F);
        buff.setModifier(BuffModifiers.MAGIC_DAMAGE, -0.2F);
        buff.setModifier(BuffModifiers.SUMMON_DAMAGE, -0.2F);
        buff.setModifier(BuffModifiers.MAX_SUMMONS, -1);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "bannerbearerfoci1"));
        tooltips.add(Localization.translate("itemtooltip", "bannerbearerfoci2"));
        return tooltips;
    }
}
