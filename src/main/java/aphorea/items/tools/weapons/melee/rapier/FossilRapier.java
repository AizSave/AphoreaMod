package aphorea.items.tools.weapons.melee.rapier;

public class FossilRapier extends AphRapierToolItem {
    public FossilRapier() {
        super(500);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(10)
                .setUpgradedValue(1, 30);

        this.attackRange.setBaseValue(60);
        this.width = 12.0F;
    }

}
