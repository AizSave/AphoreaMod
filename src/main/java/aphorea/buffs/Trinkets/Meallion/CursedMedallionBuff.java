package aphorea.buffs.Trinkets.Meallion;

import aphorea.buffs.Trinkets.AphAreaWhenHealTrinketBuff;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class CursedMedallionBuff extends AphAreaWhenHealTrinketBuff {
    static int range = 200;
    static Color color = AphColors.black;

    public static AphAreaList areaList = new AphAreaList(
            new AphArea(range, color).setDebuffArea(5000, "cursedbuff")
    );

    public CursedMedallionBuff() {
        super(30, areaList);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cursedbuff"));
        return tooltips;
    }
}