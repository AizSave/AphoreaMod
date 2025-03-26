package aphorea.items.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class IronSaber extends AphSaberToolItem {

    public IronSaber() {
        super(500);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(14)
                .setUpgradedValue(1, 80);
        knockback.setBaseValue(150);
    }

    @Override
    public Projectile getProjectile(Level level, ItemAttackerMob attackerMob, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback) {
        return new AircutProjectile.IronAircutProjectile(level, attackerMob, x, y, targetX, targetY, finalVelocity, distance, damage, knockback);
    }

}
