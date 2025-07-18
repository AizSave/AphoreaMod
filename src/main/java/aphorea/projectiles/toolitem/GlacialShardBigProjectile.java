package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
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

public class GlacialShardBigProjectile extends Projectile {
    public int projectilesAmount;
    int seed;

    public GlacialShardBigProjectile() {
    }

    public GlacialShardBigProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int seed) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;

        this.seed = seed;
    }

    @Override
    public void init() {
        super.init();
        givesLight = false;
        height = 14;
        trailOffset = -14f;
        setWidth(14, true);
        piercing = 0;
        bouncing = 0;
        projectilesAmount = 6;
    }

    @Override
    public Color getParticleColor() {
        return null;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.ice, 22, 100, getHeight());
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
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
        if (this.amountHit() >= this.piercing && (this.bounced >= this.getTotalBouncing() || !this.canBounce)) {
        }
    }

    @Override
    public void remove() {
        GameRandom random = new GameRandom(seed);
        float randomAngle = GameRandom.globalRandom.getFloatBetween(0F, (float) (Math.PI * 2));
        for (int i = 0; i < projectilesAmount; i++) {
            Projectile projectile = getProjectile(randomAngle + ((float) Math.PI * 2 * i) / projectilesAmount);
            projectile.resetUniqueID(random);
            Mob owner = getOwner();
            if (owner instanceof ItemAttackerMob) {
                ((ItemAttackerMob) owner).addAndSendAttackerProjectile(projectile);
            }
        }
        super.remove();
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FROSTBURN, mob, 5000, this), true);
        }
    }

    private Projectile getProjectile(float angle) {
        float targetX = this.x + 100 * (float) Math.cos(angle);
        float targetY = this.y + 100 * (float) Math.sin(angle);
        Projectile projectile = new GlacialShardSmallProjectile(
                this.getLevel(), this.getOwner(),
                this.x, this.y,
                targetX, targetY,
                50,
                50,
                this.getDamage().modDamage(0.5F),
                this.knockback
        );

        projectile.resetUniqueID(new GameRandom(seed));
        return projectile;
    }

}
