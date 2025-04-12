package aphorea.projectiles.toolitem;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class GelProjectile extends Projectile {

    public GelProjectile() {
    }

    public GelProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        setWidth(16, true);
        piercing = 0;
        bouncing = 0;
    }

    @Override
    public Color getParticleColor() {
        return AphColors.gel;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.gel, 26, 500, getHeight());
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
            if (this.traveledDistance >= (float) this.distance || (this.amountHit() >= this.piercing && (this.bounced >= this.getTotalBouncing() || !this.canBounce))) {
                Mob owner = this.getOwner();
                if (owner != null && !owner.removed()) {
                    GelProjectileGroundEffectEvent event = new GelProjectileGroundEffectEvent(owner, (int) x, (int) y, GameRandom.globalRandom);
                    this.getLevel().entityManager.addLevelEvent(event);
                }
            }
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff(AphBuffs.STICKY, mob, 1.0F, this.getOwner());
                mob.addBuff(ab, true);
            }
        }

    }

    public static class GelProjectileGroundEffectEvent extends GroundEffectEvent {
        protected int tickCounter;
        protected MobHitCooldowns hitCooldowns;
        protected GelProjectileParticle particle;

        public GelProjectileGroundEffectEvent() {
        }

        public GelProjectileGroundEffectEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
            super(owner, x, y, uniqueIDRandom);
        }

        @Override
        public void setupSpawnPacket(PacketWriter writer) {
            super.setupSpawnPacket(writer);
        }

        @Override
        public void applySpawnPacket(PacketReader reader) {
            super.applySpawnPacket(reader);
        }

        @Override
        public void init() {
            super.init();
            this.tickCounter = 0;
            this.hitCooldowns = new MobHitCooldowns();
            if (this.isClient()) {
                this.level.entityManager.addParticle(this.particle = new GelProjectileParticle(this.level, (float) this.x, (float) this.y, 5000L), true, Particle.GType.CRITICAL);
            }

        }

        @Override
        public Shape getHitBox() {
            int width = 40;
            int height = 30;
            return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
        }

        @Override
        public void clientHit(Mob mob) {

        }

        @Override
        public void serverHit(Mob target, boolean clientSubmitted) {
            if (clientSubmitted || !target.buffManager.hasBuff(AphBuffs.STICKY)) {
                target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, this), true);
            }

        }

        @Override
        public void hitObject(LevelObjectHit hit) {
        }

        @Override
        public void clientTick() {
            ++this.tickCounter;
            if (this.tickCounter > 100) {
                this.over();
            } else {
                super.clientTick();
            }

        }

        @Override
        public void serverTick() {
            ++this.tickCounter;
            if (this.tickCounter > 100) {
                this.over();
            } else {
                super.serverTick();
            }

        }

        @Override
        public void over() {
            super.over();
            if (this.particle != null) {
                this.particle.despawnNow();
            }

        }
    }

    public static class GelProjectileParticle extends Particle {
        public static GameTexture texture;

        public int gel;

        public GelProjectileParticle(Level level, float x, float y, long lifeTime) {
            super(level, x, y, lifeTime);
            this.gel = GameRandom.globalRandom.nextInt(4);
        }

        public void despawnNow() {
            if (this.getRemainingLifeTime() > 500L) {
                this.lifeTime = 500L;
                this.spawnTime = this.getWorldEntity().getLocalTime();
            }

        }

        @Override
        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
            int drawX = camera.getDrawX(this.getX()) - 48;
            int drawY = camera.getDrawY(this.getY()) - 48;
            long remainingLifeTime = this.getRemainingLifeTime();
            float alpha = 1.0F;
            if (remainingLifeTime < 500L) {
                alpha = Math.max(0.0F, (float) remainingLifeTime / 500.0F);
            }

            DrawOptions options = texture.initDraw().sprite(gel, 0, 96).light(light).alpha(alpha).pos(drawX, drawY);
            tileList.add((tm) -> options.draw());
        }
    }


}
