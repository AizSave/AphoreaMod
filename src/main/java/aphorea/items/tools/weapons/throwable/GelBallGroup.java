package aphorea.items.tools.weapons.throwable;

public class GelBallGroup extends GelBall {
    public GelBallGroup() {
        super();
        infinity = true;
        this.stackSize = 1;
        this.dropsAsMatDeathPenalty = false;
        attackDamage.setBaseValue(8).setUpgradedValue(1, 50);
    }
}
