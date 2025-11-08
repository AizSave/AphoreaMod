package aphorea.buffs.Trinkets.Periapt;

import aphorea.AphDependencies;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class BloodyPeriaptBuff extends AphPeriaptActivableBuff {

    public boolean hasRPGMod;

    public BloodyPeriaptBuff() {
        super("bloodyperiaptactive");
        hasRPGMod = AphDependencies.checkRPGMod();
    }

    @Override
    public Color getColor() {
        return AphColors.blood;
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "periaptboost"));
        tooltips.add(Localization.translate("itemtooltip", "bloodyperiapt", "amount", hasRPGMod ? "0.2" : "2"));
        if (hasRPGMod) tooltips.add(Localization.translate("itemtooltip", "rpgmodnerf"));
        return tooltips;
    }

}