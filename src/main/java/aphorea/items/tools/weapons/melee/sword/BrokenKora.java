package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import necesse.inventory.InventoryItem;

public class BrokenKora extends AphSwordToolItem {

    public BrokenKora() {
        super(1000);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(50);
        knockback.setBaseValue(50);

        attackRange.setBaseValue(40);

        attackXOffset = 10;
        attackYOffset = 10;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }
}
