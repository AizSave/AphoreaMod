package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class BlueBerryProjectile extends Projectile {
    private int sprite = 0;
    private long spawnTime;
    private boolean clockWise;

    public BlueBerryProjectile() {
    }

    public BlueBerryProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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

    @Override
    public void init() {
        super.init();
        givesLight = false;
        height = 18;
        trailOffset = -14f;
        setWidth(12, true);
        piercing = 0;
        bouncing = 0;

        this.spawnTime = this.getWorldEntity().getTime();

        GameRandom gameRandom = new GameRandom(this.getUniqueID());
        this.sprite = gameRandom.getIntBetween(0, 1);
        this.clockWise = gameRandom.nextBoolean();
    }

    @Override
    public Color getParticleColor() {
        return null;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.blueberry, 20, 200, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - texture.getWidth() / 4;
        int drawY = camera.getDrawY(y);

        TextureDrawOptions options = texture.initDraw()
                .sprite(sprite, 0, 14, 14)
                .light(light)
                .rotate(getAngle(), texture.getWidth() / 4, texture.getHeight() / 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });

        addShadowDrawables(tileList, drawX, drawY, light, getAngle(), texture.getWidth() / 4, 2);
    }

    public float getAngle() {
        return (float) (this.getWorldEntity().getTime() - this.spawnTime) * (clockWise ? 1 : -1);
    }

}
