package aphorea.projectiles.bullet;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class SpamBulletProjectile extends BulletProjectile {
    ToolItem toolItem;
    InventoryItem item;

    private long spawnTime;
    private int type;
    private boolean clockWise;

    public AphAreaList areaList = new AphAreaList(
            new AphArea(100, 0.5F, AphColors.green)
                    .setHealingArea(2)
    );

    public SpamBulletProjectile() {
    }

    public SpamBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, ToolItem toolItem, InventoryItem item, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);

        this.toolItem = toolItem;
        this.item = item;
    }

    public void init() {
        super.init();
        this.setWidth(10.0F);
        this.height = 18.0F;
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0F;

        this.spawnTime = this.getWorldEntity().getTime();

        GameRandom gameRandom = new GameRandom(this.getUniqueID());
        this.type = gameRandom.getIntBetween(0, 4);

        this.clockWise = gameRandom.nextBoolean();

        if (type == 1 || type == 4) {
            this.doesImpactDamage = false;
        }
        if (type == 2) {
            this.bouncing = 10;
            this.piercing = 10;
        } else {
            this.canBounce = false;
        }
    }

    public Trail getTrail() {
        return null;
    }

    protected Color getWallHitColor() {
        return AphColors.spinel;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int textureRes = 32;
            int halfTextureRes = textureRes / 2;
            int drawX = camera.getDrawX(this.x) - halfTextureRes;
            int drawY = camera.getDrawY(this.y) - halfTextureRes;
            final TextureDrawOptions options = texture.initDraw().sprite(this.type, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(this.type, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
            tileList.add((tm) -> shadowOptions.draw());
        }
    }

    public float getAngle() {
        return (float) (this.getWorldEntity().getTime() - this.spawnTime) * (clockWise ? 1 : -1);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (type == 0 && mob != null) {
            mob.buffManager.addBuff(new ActiveBuff(AphBuffs.STICKY, mob, 5F, null), false);
        } else if (type == 1) {
            LevelEvent event = new FirePoolGroundEffectEvent(this.getOwner(), (int) x, (int) y, new GameRandom(GameRandom.getNewUniqueID()));
            this.getLevel().entityManager.events.add(event);
        } else if (type == 3) {
            areaList.execute(getOwner(), x, y, 1, item, toolItem, false);
        } else if (type == 4) {
            ExplosionEvent event = new SpamBulletExplosion(x, y, this.getDamage(), this.getOwner());
            this.getLevel().entityManager.events.add(event);
        }
    }


    static public class SpamBulletExplosion extends ExplosionEvent implements Attacker {
        public SpamBulletExplosion() {
            this(0.0F, 0.0F, new GameDamage(0), null);
        }

        public SpamBulletExplosion(float x, float y, GameDamage damage, Mob owner) {
            super(x, y, 100, damage, false, 0, owner);
        }

        protected void playExplosionEffects() {
            SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(0.8F).pitch(1.5F));
            this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 0.5F, 0.5F, true);
        }

        @Override
        protected boolean canHitMob(Mob target) {
            return super.canHitMob(target) && target.canBeTargeted(ownerMob, ownerMob.isPlayer ? ((PlayerMob) ownerMob).getNetworkClient() : null);
        }
    }

    public static class FirePoolGroundEffectEvent extends GroundEffectEvent {
        protected int tickCounter;
        protected MobHitCooldowns hitCooldowns;
        protected SpamBulletProjectile.FirePoolParticle particle;

        public FirePoolGroundEffectEvent() {
        }

        public FirePoolGroundEffectEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
            super(owner, x, y, uniqueIDRandom);
        }

        public void setupSpawnPacket(PacketWriter writer) {
            super.setupSpawnPacket(writer);
        }

        public void applySpawnPacket(PacketReader reader) {
            super.applySpawnPacket(reader);
        }

        public void init() {
            super.init();
            this.tickCounter = 0;
            this.hitCooldowns = new MobHitCooldowns();
            if (this.isClient()) {
                this.level.entityManager.addParticle(this.particle = new SpamBulletProjectile.FirePoolParticle(this.level, (float) this.x, (float) this.y, 1000L), true, Particle.GType.CRITICAL);
            }

        }

        public Shape getHitBox() {
            int width = 40;
            int height = 30;
            return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
        }

        @Override
        public void clientHit(Mob mob) {

        }

        public void serverHit(Mob target, boolean clientSubmitted) {
            if (clientSubmitted || !target.buffManager.hasBuff(BuffRegistry.Debuffs.ON_FIRE)) {
                target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE, target, 5000, this), true);
            }
            target.isServerHit(new GameDamage(1), x, y, 0, getAttackOwner());
        }

        public void hitObject(LevelObjectHit hit) {
        }

        public void clientTick() {
            ++this.tickCounter;
            if (this.tickCounter > 20) {
                this.over();
            } else {
                super.clientTick();
            }

        }

        public void serverTick() {
            ++this.tickCounter;
            if (this.tickCounter > 20) {
                this.over();
            } else {
                super.serverTick();
            }

        }

        public void over() {
            super.over();
            if (this.particle != null) {
                this.particle.despawnNow();
            }

        }
    }

    public static class FirePoolParticle extends Particle {
        public static GameTexture texture;

        public int gel;

        public FirePoolParticle(Level level, float x, float y, long lifeTime) {
            super(level, x, y, lifeTime);
            this.gel = GameRandom.globalRandom.nextInt(4);
        }

        public void despawnNow() {
            if (this.getRemainingLifeTime() > 500L) {
                this.lifeTime = 500L;
                this.spawnTime = this.getWorldEntity().getLocalTime();
            }

        }

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
