package aphorea.items.tools.weapons.melee.greatsword;

import java.awt.*;

public class BabylonGreatsword extends AphGreatswordSecondarySpinToolItem {

    public BabylonGreatsword() {
        super(1550, 300, getThreeChargeLevels(500, 600, 700), new Color(200, 100, 100));
        rarity = Rarity.EPIC;
        attackDamage.setBaseValue(120)
                .setUpgradedValue(1, 150);
        attackRange.setBaseValue(110);
        knockback.setBaseValue(50);

        width = 24.0F;

        this.attackXOffset = 18;
        this.attackYOffset = 20;
    }
}