package aphorea.projectiles.mob;

import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.laserProjectile.LaserProjectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class SpinelGolemBeamProjectile extends LaserProjectile {
    public SpinelGolemBeamProjectile() {
    }

    public SpinelGolemBeamProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(10.0F);
        this.givesLight = true;
        this.height = 24.0F;
        this.piercing = 1000;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 3;
    }

    @Override
    public Color getParticleColor() {
        return AphColors.spinel_darker;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), AphColors.spinel_darker, 15.0F, 500, 18.0F);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.BROKEN_ARMOR, mob, 10.0F, this.getOwner());
                mob.addBuff(ab, true);
            }

        }
    }
}
