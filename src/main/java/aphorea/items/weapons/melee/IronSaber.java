package aphorea.items.weapons.melee;

import aphorea.other.itemtype.weapons.AphSaberToolItem;
import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class IronSaber extends AphSaberToolItem {

    public IronSaber() {
        super(500, getChargeLevels());
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(14)
                .setUpgradedValue(1, 80);
        knockback.setBaseValue(150);
    }

    @Override
    public Projectile getProjectile(Level level, PlayerMob player, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback) {
        return new AircutProjectile.IronAircutProjectile(level, player, x, y, targetX, targetY, finalVelocity, distance, damage, knockback);
    }

}
