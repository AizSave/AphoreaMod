package aphorea.buffs.Trinkets.Periapt;

import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class RockyPeriaptBuff extends AphPeriaptActivableBuff {

    public RockyPeriaptBuff() {
        super("rockyperiaptactive");
    }

    @Override
    public Color getColor() {
        return AphColors.stone;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SPEED, -0.05F);
        buff.addModifier(BuffModifiers.ARMOR_FLAT, 5);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rockyperiapt"));
        tooltips.add(Localization.translate("itemtooltip", "rockyperiapt2"));
        return tooltips;
    }
}