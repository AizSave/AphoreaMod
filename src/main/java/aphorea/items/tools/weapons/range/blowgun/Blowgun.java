package aphorea.items.tools.weapons.range.blowgun;

public class Blowgun extends AphBlowgunToolItem {
    public Blowgun() {
        super(100);

        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(10.0F).setUpgradedValue(1.0F, 60.0F);
        this.attackXOffset = 8;
        this.attackYOffset = 10;
        this.velocity.setBaseValue(100);
        this.attackRange.setBaseValue(500);
        this.resilienceGain.setBaseValue(0F);
    }

}