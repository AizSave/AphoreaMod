package aphorea.items.weapons.melee.battleaxe;

import aphorea.utils.AphColors;

import java.awt.*;

public class DemonicBattleaxe extends AphBattleaxeToolItem {

    public DemonicBattleaxe() {
        super(500, getChargeLevel(2000, AphColors.demonic), getChargeLevel(1400, new Color(40, 10, 60)));
        rarity = Rarity.UNCOMMON;
        attackDamage.setBaseValue(140)
                .setUpgradedValue(1, 340);
        attackRange.setBaseValue(100);
        knockback.setBaseValue(150);
    }
}
