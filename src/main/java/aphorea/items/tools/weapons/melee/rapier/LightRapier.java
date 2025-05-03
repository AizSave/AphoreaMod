package aphorea.items.tools.weapons.melee.rapier;

public class LightRapier extends AphRapierToolItem {
    public LightRapier() {
        super(1300);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(50);
        attackDamage.setBaseValue(10)
                .setUpgradedValue(1, 20);

        this.dashAnimTime.setBaseValue(500);

        this.attackRange.setBaseValue(85);
    }

}
