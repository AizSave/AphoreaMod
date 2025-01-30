package aphorea.projectiles.mob;

import aphorea.utils.AphColors;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.laserProjectile.LaserProjectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;

import java.awt.*;

public class RubyGolemBeamProjectile extends LaserProjectile {
    public RubyGolemBeamProjectile() {
    }

    public RubyGolemBeamProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        this.setWidth(10.0F);
        this.givesLight = true;
        this.height = 24.0F;
        this.piercing = 1000;
    }

    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 3;
    }

    public Color getParticleColor() {
        return AphColors.ruby_darker;
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), AphColors.ruby_darker, 15.0F, 500, 18.0F);
    }
}
