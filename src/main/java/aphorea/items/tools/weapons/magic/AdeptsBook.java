package aphorea.items.tools.weapons.magic;

import aphorea.items.AphAreaToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;

public class AdeptsBook extends AphAreaToolItem implements ItemInteractAction {

    public AdeptsBook() {
        super(650, true, false);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(1000);

        manaCost.setBaseValue(4.0F);

        this.attackXOffset = -4;
        this.attackYOffset = 10;

        attackDamage.setBaseValue(30).setUpgradedValue(1, 60);
        damageType = DamageTypeRegistry.MAGIC;
    }

    @Override
    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(
                new AphArea(250, AphColors.dark_magic).setDamageArea(getAttackDamage(item))
        );
    }
}
