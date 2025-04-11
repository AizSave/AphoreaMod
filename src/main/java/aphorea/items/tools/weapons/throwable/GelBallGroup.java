package aphorea.items.tools.weapons.throwable;

public class GelBallGroup extends GelBall {
    public GelBallGroup() {
        super();
        this.enchantCost.setBaseValue(200).setUpgradedValue(1.0F, 2000);
        attackDamage.setBaseValue(15).setUpgradedValue(1, 80);

        infinity = true;
        this.stackSize = 1;
        this.dropsAsMatDeathPenalty = false;
        attackDamage.setBaseValue(8).setUpgradedValue(1, 50);
    }
}
