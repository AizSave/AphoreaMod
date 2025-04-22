package aphorea.buffs.Trinkets.Meallion;

import aphorea.buffs.Trinkets.AphAreaWhenHealTrinketBuff;
import aphorea.packets.AphSingleAreaShowPacket;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphFlatArea;
import necesse.engine.network.Packet;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.PlayerMob;

import java.awt.*;

public class WitchMedallionBuff extends AphAreaWhenHealTrinketBuff {
    static int range = 200;
    static Color color = AphColors.dark_magic;

    public static AphAreaList areaList = new AphAreaList(
            new AphFlatArea(range, color).setDamageArea(15).setArmorPen(5)
    ).setDamageType(DamageTypeRegistry.MAGIC);

    public WitchMedallionBuff() {
        super(30, areaList);
    }

    @Override
    public Packet getPacket(PlayerMob player, float rangeModifier) {
        return new AphSingleAreaShowPacket(player.x, player.y, range * rangeModifier, color);
    }
}