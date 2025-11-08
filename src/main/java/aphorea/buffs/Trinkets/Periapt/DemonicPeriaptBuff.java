package aphorea.buffs.Trinkets.Periapt;

import aphorea.AphDependencies;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class DemonicPeriaptBuff extends AphPeriaptActivableBuff {

    public boolean hasRPGMod;

    public DemonicPeriaptBuff() {
        super("demonicperiaptactive");
        hasRPGMod = AphDependencies.checkRPGMod();
    }

    @Override
    public Color getColor() {
        return GameRandom.globalRandom.getOneOf(AphColors.paletteDemonic);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "periaptboost"));
        tooltips.add(Localization.translate("itemtooltip", "demonicperiapt", "amount", hasRPGMod ? "0.2" : "2"));
        if (hasRPGMod) tooltips.add(Localization.translate("itemtooltip", "rpgmodnerf"));
        return tooltips;
    }

}