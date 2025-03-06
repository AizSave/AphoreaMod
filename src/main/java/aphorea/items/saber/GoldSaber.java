package aphorea.items.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class GoldSaber extends AphSaberToolItem {

    public GoldSaber() {
        super(500);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(16)
                .setUpgradedValue(1, 85);
        knockback.setBaseValue(150);
    }

    @Override
    public Projectile getProjectile(Level level, ItemAttackerMob attackerMob, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback) {
        return new AircutProjectile.GoldAircutProjectile(level, attackerMob, x, y, targetX, targetY, finalVelocity, distance, damage, knockback);
    }

}
