package aphorea.items.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class DemonicSaber extends AphSaberToolItem {

    public DemonicSaber() {
        super(800);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(20)
                .setUpgradedValue(1, 120);
        knockback.setBaseValue(150);

        this.attackRange.setBaseValue(60);
    }

    @Override
    public Projectile getProjectile(Level level, ItemAttackerMob attackerMob, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback) {
        return new AircutProjectile.DemonicAircutProjectile(level, attackerMob, x, y, targetX, targetY, finalVelocity, distance, damage, knockback);
    }
}
