package aphorea.items.weapons.melee;

import aphorea.other.vanillaitemtypes.weapons.AphSwordToolItem;

public class Broom extends AphSwordToolItem {

    public Broom() {
        super(500);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(16)
                .setUpgradedValue(1, 80);
        attackRange.setBaseValue(120);
        attackAnimTime.setBaseValue(400);
        knockback.setBaseValue(200);
        this.keyWords.add("broom");
        this.keyWords.remove("sword");
    }

}
