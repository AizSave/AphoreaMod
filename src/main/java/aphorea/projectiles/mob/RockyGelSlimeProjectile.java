package aphorea.projectiles.mob;

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

public class RockyGelSlimeProjectile extends Projectile {
    private long spawnTime;
    private int sprite;

    public RockyGelSlimeProjectile() {
    }

    public RockyGelSlimeProjectile(Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        this.setWidth(10.0F);
        this.height = 18.0F;
        this.spawnTime = this.getWorldEntity().getTime();
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0F;
        if (this.texture != null) {
            this.sprite = (new GameRandom(this.getUniqueID())).nextInt(this.texture.getWidth() / 32);
        }

    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(50, 50, 50), 16.0F, 150, 18.0F);
    }

    public Color getParticleColor() {
        return new Color(50, 50, 50);
    }

    public float getParticleChance() {
        return super.getParticleChance() * 0.5F;
    }

    protected int getExtraSpinningParticles() {
        return 0;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int textureRes = 32;
            int halfTextureRes = textureRes / 2;
            int drawX = camera.getDrawX(this.x) - halfTextureRes;
            int drawY = camera.getDrawY(this.y) - halfTextureRes;
            final TextureDrawOptions options = this.texture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
            tileList.add((tm) -> shadowOptions.draw());
        }
    }

    public float getAngle() {
        return (float) (this.getWorldEntity().getTime() - this.spawnTime);
    }

    protected void playHitSound(float x, float y) {
    }
}
