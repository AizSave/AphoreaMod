package aphorea.items.tools.weapons.melee.battleaxe;

import aphorea.utils.AphColors;

public class DemonicBattleaxe extends AphBattleaxeToolItem {

    public DemonicBattleaxe() {
        super(400, getChargeLevel(2000, AphColors.demonic));
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(140)
                .setUpgradedValue(1, 340);
        attackRange.setBaseValue(100);
        knockback.setBaseValue(150);
    }
}
