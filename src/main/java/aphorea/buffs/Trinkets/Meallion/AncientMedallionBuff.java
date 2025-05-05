package aphorea.buffs.Trinkets.Meallion;

import aphorea.buffs.Trinkets.AphAreaWhenHealTrinketBuff;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class AncientMedallionBuff extends AphAreaWhenHealTrinketBuff {
    static int range = 300;
    static Color color = AphColors.darker_magic;

    public static AphAreaList areaList = new AphAreaList(
            new AphArea(range, color).setDamageArea(new GameDamage(DamageTypeRegistry.MAGIC, 30, 10))
                    .setDebuffArea(5000, "cursedbuff")
    );

    public AncientMedallionBuff() {
        super(30, areaList);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cursedbuff"));
        return tooltips;
    }
}