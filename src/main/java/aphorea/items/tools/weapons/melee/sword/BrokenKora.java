package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import necesse.inventory.InventoryItem;

public class BrokenKora extends AphSwordToolItem {

    public BrokenKora() {
        super(500);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(80);
        knockback.setBaseValue(50);

        attackRange.setBaseValue(25);

        attackXOffset = 10;
        attackYOffset = 10;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }
}
