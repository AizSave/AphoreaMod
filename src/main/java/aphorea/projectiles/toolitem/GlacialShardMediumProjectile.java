package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;

public class GlacialShardMediumProjectile extends GlacialShardBigProjectile {

    public GlacialShardMediumProjectile() {
    }

    public GlacialShardMediumProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int seed) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, seed);
    }

    @Override
    public void init() {
        super.init();
        setWidth(10, true);
        projectilesAmount = 3;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.ice, 16, 60, getHeight());
    }
}
