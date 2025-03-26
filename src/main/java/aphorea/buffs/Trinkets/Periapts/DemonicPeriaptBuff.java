package aphorea.buffs.Trinkets.Periapts;

import aphorea.buffs.Trinkets.AphPeriaptActivableBuff;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class DemonicPeriaptBuff extends AphPeriaptActivableBuff {

    public DemonicPeriaptBuff() {
        super("demonicperiaptactive");
    }

    @Override
    public Color getColor() {
        return GameRandom.globalRandom.getOneOf(AphColors.paletteDemonic);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "demonicperiapt"));
        tooltips.add(Localization.translate("itemtooltip", "demonicperiapt2"));
        return tooltips;
    }

}