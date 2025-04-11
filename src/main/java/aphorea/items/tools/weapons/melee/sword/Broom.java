package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;

public class Broom extends AphSwordToolItem {

    public Broom() {
        super(200);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(16)
                .setUpgradedValue(1, 80);
        attackRange.setBaseValue(120);
        attackAnimTime.setBaseValue(400);
        knockback.setBaseValue(200);
        this.keyWords.add("broom");
    }

}
