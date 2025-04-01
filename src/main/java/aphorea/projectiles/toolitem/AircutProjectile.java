package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
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
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

abstract public class AircutProjectile extends Projectile {

    abstract Color getColor();

    abstract GameTexture getTexture();

    public AircutProjectile() {
    }

    public AircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        trailOffset = -10f;
        setWidth(36, true);
        piercing = 10;
        bouncing = 0;
    }

    @Override
    public Color getParticleColor() {
        return getColor();
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), getColor(), 32, 50, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;

        GameTexture texture = getTexture();

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
    }

    public static class CopperAircutProjectile extends AircutProjectile {

        public static GameTexture texture;

        Color getColor() {
            return AphColors.copper;
        }

        GameTexture getTexture() {
            return texture;
        }

        public CopperAircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        }

        public CopperAircutProjectile() {
        }
    }

    public static class IronAircutProjectile extends AircutProjectile {

        public static GameTexture texture;

        Color getColor() {
            return AphColors.iron;
        }

        GameTexture getTexture() {
            return texture;
        }

        public IronAircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        }

        public IronAircutProjectile() {
        }
    }

    public static class GoldAircutProjectile extends AircutProjectile {

        public static GameTexture texture;

        Color getColor() {
            return AphColors.gold;
        }

        GameTexture getTexture() {
            return texture;
        }

        public GoldAircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        }

        public GoldAircutProjectile() {
        }
    }

    public static class UnstableGelAircutProjectile extends AircutProjectile {

        public static GameTexture texture;

        Color getColor() {
            return AphColors.unstableGel;
        }

        GameTexture getTexture() {
            return texture;
        }

        public UnstableGelAircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        }

        public UnstableGelAircutProjectile() {
        }
    }

    public static class DemonicAircutProjectile extends AircutProjectile {

        public static GameTexture texture;

        Color getColor() {
            return AphColors.demonic;
        }

        GameTexture getTexture() {
            return texture;
        }

        public DemonicAircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        }

        public DemonicAircutProjectile() {
        }
    }

    public static class RedAircutProjectile extends AircutProjectile {

        public static GameTexture texture;

        Color getColor() {
            return new Color(255, 98, 98);
        }

        GameTexture getTexture() {
            return texture;
        }

        public RedAircutProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        }

        public RedAircutProjectile() {
        }

        @Override
        public void init() {
            super.init();
            setWidth(56, true);
            piercing = 20;
            givesLight = true;
        }
    }
}
