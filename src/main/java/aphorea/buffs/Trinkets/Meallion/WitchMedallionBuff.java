package aphorea.buffs.Trinkets.Meallion;

import aphorea.buffs.Trinkets.AphAreaWhenHealTrinketBuff;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;

import java.awt.*;

public class WitchMedallionBuff extends AphAreaWhenHealTrinketBuff {
    static int range = 200;
    static Color color = AphColors.dark_magic;

    public static AphAreaList areaList = new AphAreaList(
            new AphArea(range, color).setDamageArea(new GameDamage(DamageTypeRegistry.MAGIC, 15, 5))
    );

    public WitchMedallionBuff() {
        super(30, areaList);
    }
}