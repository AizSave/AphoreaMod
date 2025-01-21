package aphorea.items.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class CopperSaber extends AphSaberToolItem {

    public CopperSaber() {
        super(500, getChargeLevels());
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(12)
                .setUpgradedValue(1, 75);
        knockback.setBaseValue(150);
    }

    @Override
    public Projectile getProjectile(Level level, PlayerMob player, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback) {
        return new AircutProjectile.CopperAircutProjectile(level, player, x, y, targetX, targetY, finalVelocity, distance, damage, knockback);
    }
}
