package aphorea.buffs.Trinkets.Healing;

import aphorea.packets.AphSingleAreaShowPacket;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphFlatArea;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class AncientMedallionBuff extends AphAreaWhenHealTrinketBuff {
    static int range = 300;
    static Color color = AphColors.darker_magic;

    public static AphAreaList areaList = new AphAreaList(
            new AphFlatArea(range, color).setDamageArea(30).setArmorPen(10)
                    .setDebuffArea(5000, "cursed")
    ).setDamageType(DamageTypeRegistry.MAGIC);

    public AncientMedallionBuff() {
        super(30, areaList);
    }

    @Override
    public Packet getPacket(PlayerMob player, float rangeModifier) {
        return new AphSingleAreaShowPacket(player.x, player.y, range * rangeModifier, color);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cursedbuff"));
        return tooltips;
    }
}