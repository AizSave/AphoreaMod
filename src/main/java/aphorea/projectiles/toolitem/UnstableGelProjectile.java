package aphorea.projectiles.toolitem;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
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

public class UnstableGelProjectile extends Projectile {

    int generation;
    int seed;

    public UnstableGelProjectile() {
    }

    public UnstableGelProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int generation, int seed) {
        this.generation = generation;
        this.seed = seed;

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
        height = 20;
        trailOffset = -14f;
        setWidth(20, true);
        piercing = 10;
        bouncing = 3;
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
    public void addHit(Mob target) {
        super.addHit(target);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 2000, this), true);
    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
        if (mob == null && object != null && this.bounced < this.getTotalBouncing() && this.canBounce && generation < 2) {
            newProjectile();
        }
    }

    public void newProjectile() {
        Projectile projectile = getProjectile();

        this.getLevel().entityManager.projectiles.addHidden(projectile);

        if (this.getLevel().isServer() && this.getOwner().isPlayer) {
            this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, ((PlayerMob) this.getOwner()).getServerClient());
        }

    }

    private Projectile getProjectile() {
        generation++;

        Projectile projectile = new UnstableGelProjectile(
                this.getLevel(), this.getOwner(),
                this.x, this.y,
                this.getOwner().x, this.getOwner().y,
                this.speed,
                this.distance - (int) this.traveledDistance,
                this.getDamage(),
                this.knockback, generation, seed
        );

        GameRandom random = new GameRandom(seed);

        projectile.resetUniqueID(random);

        projectile.moveDist(40);
        return projectile;
    }
}
