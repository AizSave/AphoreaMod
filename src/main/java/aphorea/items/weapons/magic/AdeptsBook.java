package aphorea.items.weapons.magic;

import aphorea.items.AphAreaToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;

import java.awt.*;

public class AdeptsBook extends AphAreaToolItem implements ItemInteractAction {

    static int range = 250;
    static Color color = AphColors.palettePinkWitch[2];

    static AphAreaList areaList = new AphAreaList(
            new AphArea(range, color).setDamageArea(30).setArmorPen(10)
    ).setDamageType(DamageTypeRegistry.MAGIC);

    public AdeptsBook() {
        super(500, true, false, areaList);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(1000);

        manaCost.setBaseValue(4.0F);

        this.attackXOffset = 2;
        this.attackYOffset = 8;
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
    }
}
