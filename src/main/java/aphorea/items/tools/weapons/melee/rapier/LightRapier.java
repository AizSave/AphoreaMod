package aphorea.items.tools.weapons.melee.rapier;

public class LightRapier extends AphRapierToolItem {
    public LightRapier() {
        super(1300);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(50);
        knockback.setBaseValue(10);
        attackDamage.setBaseValue(10)
                .setUpgradedValue(1, 20);

        this.dashAnimTime.setBaseValue(400);

        this.attackRange.setBaseValue(85);
    }

}
