package aphorea.items.tools.weapons.magic;

import aphorea.items.AphAreaToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.ItemInteractAction;

import java.awt.*;

public class AdeptsBook extends AphAreaToolItem implements ItemInteractAction {

    static int range = 250;
    static Color color = AphColors.dark_magic;

    static AphAreaList areaList = new AphAreaList(
            new AphArea(range, color).setDamageArea(30, 60).setArmorPen(10)
    ).setDamageType(DamageTypeRegistry.MAGIC);

    public AdeptsBook() {
        super(650, true, false, areaList);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(1000);

        manaCost.setBaseValue(4.0F);

        this.attackXOffset = -4;
        this.attackYOffset = 10;

        attackDamage.setBaseValue(30).setUpgradedValue(1, 60);
    }
}
