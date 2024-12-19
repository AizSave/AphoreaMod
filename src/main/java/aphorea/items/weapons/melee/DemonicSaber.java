package aphorea.items.weapons.melee;

import aphorea.other.itemtype.weapons.AphSaberToolItem;
import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class DemonicSaber extends AphSaberToolItem {

    public DemonicSaber() {
        super(500, getChargeLevels());
        rarity = Rarity.UNCOMMON;
        attackDamage.setBaseValue(20)
                .setUpgradedValue(1, 120);
        knockback.setBaseValue(150);
    }

    @Override
    public Projectile getProjectile(Level level, PlayerMob player, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback) {
        return new AircutProjectile.DemonicAircutProjectile(level, player, x, y, targetX, targetY, finalVelocity, distance, damage, knockback);
    }
}
