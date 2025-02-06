package aphorea.buffs.Trinkets.Healing;

import aphorea.buffs.Trinkets.AphDamageWhenHealTrinketBuff;
import aphorea.packets.AphSingleAreaShowPacket;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class WitchMedallionBuff extends AphDamageWhenHealTrinketBuff {
    static int range = 200;
    static Color color = AphColors.palettePinkWitch[2];

    public static AphAreaList areaList = new AphAreaList(
            new AphArea(range, color).setDamageArea(15).setArmorPen(5)
    ).setDamageType(DamageTypeRegistry.MAGIC);

    public WitchMedallionBuff() {
        super(30, areaList);
    }

    @Override
    public Packet getPacket(PlayerMob player, float rangeModifier) {
        return new AphSingleAreaShowPacket(player.x, player.y, range * rangeModifier, color);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "witchmedallion"));
        return tooltips;
    }

}