package aphorea.mobs.summon;

import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class VolatileGelSlime extends AttackingFollowingMob {

    public static GameTexture texture;

    public VolatileGelSlime() {
        super(5);
        this.setSpeed(60);
        this.setFriction(2);
        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-14, -12, 28, 24);
        selectBox = new Rectangle(-14, -7 - 14, 28, 28);
    }

    int explosionTime = 0;
    int maxExplosionTime = GameRandom.globalRandom.getIntBetween(17, 23);

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<Mob>(this, new PlayerFollowerCollisionChaserAI<>(16 * 32, null, 0, 1000, 640, 64));
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if(!this.removed() && this.explosionTime > 0) {
            this.explosionTime++;
            if(this.explosionTime >= maxExplosionTime) {
                doExplosion();
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if(!this.removed() && this.explosionTime > 0) {
            this.explosionTime++;
        }
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if(texture != null) {
            for (int i = 0; i < 4; i++) {
                getLevel().entityManager.addParticle(new FleshParticle(
                        getLevel(), texture,
                        GameRandom.globalRandom.nextInt(5),
                        8,
                        32,
                        x, y, 20f,
                        knockbackX, knockbackY
                ), Particle.GType.IMPORTANT_COSMETIC);
            }
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;

        Point sprite = getAnimSprite(x, y, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 64)
                .color(light.getFloatRed(), light.getFloatGreen() * (1 - (float) this.explosionTime / this.maxExplosionTime), light.getFloatBlue() * (1 - (float) this.explosionTime / this.maxExplosionTime))
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if(!this.isWaterWalking()) addShadowDrawables(tileList, x, y, light, camera);
    }

    public Point getAnimSprite(int x, int y, int dir) {
        int animTime = 200;
        int spriteX = this.inLiquid(x, y) ? 4 + GameUtils.getAnim(this.getWorldEntity().getTime(), 2, animTime) : GameUtils.getAnim(this.getWorldEntity().getTime(), 4, animTime);
        return new Point(spriteX, dir);
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return false;
    }

    @Override
    public void collidedWith(Mob other) {
        super.collidedWith(other);
        if(this.explosionTime <= 0 && !this.removed() && other.canBeTargeted(this, this.getPvPOwner()) && other.canBeHit(this)) {
            ActiveBuff buff = new ActiveBuff(AphBuffs.STICKY, other, 2000, this);
            other.addBuff(buff, true);
            this.explosionTime = 1;
        }
    }

    public void doExplosion() {
        this.spawnDeathParticles(GameRandom.globalRandom.getFloatBetween(-600, 600), GameRandom.globalRandom.getFloatBetween(-600, 600));
        this.remove();

        if (damage != null) {
            ExplosionEvent event = new BombExplosionEvent(x, y, 140, damage, false, 0, this.getFollowingPlayer());
            this.getLevel().entityManager.addLevelEvent(event);
        }

    }

    @Override
    public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
        if(buff.buff != AphBuffs.STICKY) super.addBuff(buff, sendUpdatePacket);
    }
}
