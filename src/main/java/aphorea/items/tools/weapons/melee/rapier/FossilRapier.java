package aphorea.items.tools.weapons.melee.rapier;

public class FossilRapier extends AphRapierToolItem {
    public FossilRapier() {
        super(500);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(6)
                .setUpgradedValue(1, 20);

        this.attackRange.setBaseValue(70);

        this.width = 10.0F;
        this.attackXOffset = 12;
        this.attackYOffset = 2;
    }

}
