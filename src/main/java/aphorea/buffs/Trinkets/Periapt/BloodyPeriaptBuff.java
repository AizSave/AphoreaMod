package aphorea.buffs.Trinkets.Periapt;

import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class BloodyPeriaptBuff extends AphPeriaptActivableBuff {

    public BloodyPeriaptBuff() {
        super("bloodyperiaptactive");
    }

    @Override
    public Color getColor() {
        return AphColors.blood;
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "periaptboost"));
        tooltips.add(Localization.translate("itemtooltip", "bloodyperiapt"));
        return tooltips;
    }

}