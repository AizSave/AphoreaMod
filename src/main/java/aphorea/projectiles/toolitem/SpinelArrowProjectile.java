package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
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
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;

public class SpinelArrowProjectile extends Projectile {

    public SpinelArrowProjectile() {
    }

    public SpinelArrowProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        setWidth(10, true);
        piercing = 2;
        bouncing = 0;
    }

    public static Color[] colors = new Color[]{
            AphColors.spinel_light, AphColors.spinel, AphColors.spinel_dark
    };

    public static int[] thickness = new int[]{
            14, 16, 18
    };

    @Override
    public Color getParticleColor() {
        return this.amountHit == 0 ? null : (this.amountHit > 2 ? colors[2] : colors[this.amountHit]);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), this.amountHit > 2 ? colors[2] : colors[this.amountHit], this.amountHit > 2 ? thickness[2] : thickness[this.amountHit], 500, getHeight() - 2);
    }

    @Override
    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
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
    public float tickMovement(float delta) {
        if (this.removed()) {
            return 0.0F;
        } else {
            float speed = this.speed * (0.5F + 0.5F * this.amountHit);
            if (this.isBoomerang) {
                Mob owner = this.getOwner();
                if (owner == null) {
                    this.remove();
                    return 0.0F;
                }

                if (this.returningToOwner) {
                    this.setTarget(owner.x, owner.y);
                }
            }

            float moveX = this.getMoveDist(this.dx * speed, delta);
            float moveY = this.getMoveDist(this.dy * speed, delta);
            double totalDist = Math.sqrt(moveX * moveX + moveY * moveY);
            if (Double.isNaN(totalDist) || Double.isInfinite(totalDist)) {
                totalDist = 0.0;
            }

            this.moveDist(totalDist);
            if (this.removeIfOutOfBounds && (this.getX() < -100 || this.getY() < -100 || this.getX() > this.getLevel().width * 32 + 100 || this.getY() > this.getLevel().height * 32 + 100)) {
                this.remove();
            }

            return (float) totalDist;
        }
    }

    @Override
    public void applyDamage(Mob mob, float x, float y) {
        float damageMod = 1 + 0.25F * this.amountHit;
        mob.isServerHit(this.getDamage().modDamage(damageMod), mob.x - x * -this.dx * 50.0F, mob.y - y * -this.dy * 50.0F, (float) this.knockback, this);
    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
        this.replaceTrail();
    }
}
