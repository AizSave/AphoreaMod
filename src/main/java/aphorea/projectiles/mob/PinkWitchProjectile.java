package aphorea.projectiles.mob;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;
import java.util.List;

public class PinkWitchProjectile extends FollowingProjectile {

    public PinkWitchProjectile() {
    }

    public PinkWitchProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        turnSpeed = 0.15f;
        givesLight = true;
        height = 2;
        trailOffset = -14f;
        setWidth(2, true);
        piercing = 0;
        bouncing = 0;
    }

    @Override
    public Color getParticleColor() {
        return AphColors.dark_magic;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.dark_magic, 26, 500, getHeight());
    }

    @Override
    public void updateTarget() {
        if (traveledDistance > 20) {
            findTarget(
                    m -> m.isPlayer,
                    200, 450
            );
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.BROKEN_ARMOR, mob, 3000, this.getOwner()), true);
        }
    }
}
