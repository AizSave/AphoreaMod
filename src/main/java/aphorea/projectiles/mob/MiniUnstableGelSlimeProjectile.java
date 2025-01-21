package aphorea.projectiles.mob;

import aphorea.mobs.bosses.MiniUnstableGelSlime;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class MiniUnstableGelSlimeProjectile extends Projectile {

    public MiniUnstableGelSlimeProjectile() {
    }

    public MiniUnstableGelSlimeProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        givesLight = false;
        height = 18;
        trailOffset = -14f;
        setWidth(28, true);
        piercing = 0;
        bouncing = 0;
    }

    @Override
    public Color getParticleColor() {
        return AphColors.unstableGel;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.unstableGel, 26, 500, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(y);
        TextureDrawOptions options = texture.initDraw()
                .light(light)
                .rotate(getAngle(), texture.getWidth() / 2, 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });

        addShadowDrawables(tileList, drawX, drawY, light, getAngle(), texture.getWidth() / 2, 2);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            MiniUnstableGelSlime spawnMob = (MiniUnstableGelSlime) MobRegistry.getMob("miniunstablegelslime", this.getLevel());
            ActiveBuff buff = new ActiveBuff(AphBuffs.STUN, spawnMob, 500, spawnMob);
            spawnMob.addBuff(buff, true);
            this.getLevel().entityManager.addMob(spawnMob, x, y);
        }
    }

    @Override
    public void addHit(Mob target) {
        super.addHit(target);
        ActiveBuff buff = new ActiveBuff(AphBuffs.STICKY, target, 500, this);
        target.addBuff(buff, true);

    }
}