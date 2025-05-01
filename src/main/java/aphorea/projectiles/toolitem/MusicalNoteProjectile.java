package aphorea.projectiles.toolitem;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class MusicalNoteProjectile extends Projectile {
    private int type;

    public MusicalNoteProjectile() {
    }

    public MusicalNoteProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        this.setWidth(8.0F);
        this.height = 18.0F;
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0F;

        GameRandom gameRandom = new GameRandom(this.getUniqueID());
        this.type = gameRandom.nextInt(texture.getWidth() / 32);

        this.bouncing = 2;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected Color getWallHitColor() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y);

        TextureDrawOptions options = texture.initDraw()
                .sprite(type, 0, 32)
                .light(light)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });

        addShadowDrawables(tileList, drawX, drawY, light, getAngle(), texture.getWidth() / 6, 2);
    }

    @Override
    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, float angle, int centerX, int centerY) {
        this.addShadowDrawables(list, drawX, drawY, light, (o) -> o);
    }

    @Override
    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, float angle, int centerY) {
        this.addShadowDrawables(list, drawX, drawY, light, (o) -> o);
    }

    @Override
    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, Function<TextureDrawOptionsEnd, TextureDrawOptionsEnd> modifier) {
        this.addShadowDrawables(list, this.shadowTexture.initDraw().sprite(type, 0, 32), drawX, drawY, light, modifier);
    }

    @Override
    public float getHeight() {
        float frequency = 0.05F;
        float amplitude = 10F;

        float wave = (float) Math.cos(this.traveledDistance * frequency);
        return this.height + wave * amplitude;
    }
}
