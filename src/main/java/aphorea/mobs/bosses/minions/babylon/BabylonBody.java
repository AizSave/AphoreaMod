package aphorea.mobs.bosses.minions.babylon;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobBody;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BabylonBody extends BossWormMobBody<BabylonHead, BabylonBody> {
    public int spriteY;
    public TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(50);

    public BabylonBody() {
        super(1000);
        this.isSummoned = true;
        this.collision = new Rectangle(-30, -25, 60, 50);
        this.hitBox = new Rectangle(-40, -35, 80, 70);
        this.selectBox = new Rectangle(-40, -60, 80, 80);
    }

    public GameMessage getLocalization() {
        BabylonHead head = this.master.get(this.getLevel());
        return head != null ? head.getLocalization() : new StaticMessage("babylonbody");
    }

    public void init() {
        super.init();
    }

    public GameDamage getCollisionDamage(Mob target) {
        return BabylonHead.bodyCollisionDamage;
    }

    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0F && super.canCollisionHit(target);
    }

    public void clientTick() {
        super.clientTick();
        if (this.isVisible()) {
            this.particleSpawner.gameTick();

            while(this.particleSpawner.shouldTick()) {
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 45.0F, this.y + GameRandom.globalRandom.floatGaussian() * 30.0F + 5.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).sizeFades(5, 10).givesLight().heightMoves(this.height + 10.0F, this.height + GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(1000);
                if (this.spriteY == 7) {
                    this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 10.0F + 5.0F, Particle.GType.COSMETIC).sprite(GameResources.pearlescentShardParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 18, 24)).movesFriction(-this.dx * 20.0F, -this.dy * 20.0F, 0.8F).sizeFades(11, 22).ignoreLight(true).givesLight().heightMoves(this.height + 10.0F, this.height + GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(2000);
                }
            }
        }

    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of((new ModifierValue<>(BuffModifiers.SLOW, 0.0F)).max(0.0F));
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isVisible()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(x) - 112;
            int drawY = camera.getDrawY(y);
            if (this.next != null) {
                Point2D.Float dir = new Point2D.Float(this.next.x - (float)x, this.next.y - this.next.height - ((float)y - this.height));
                float angle = GameMath.fixAngle(GameMath.getAngle(dir));
                final MobDrawable drawOptions = WormMobHead.getAngledDrawable(new GameSprite(BabylonHead.texture, 0, this.spriteY, 224), null, light.minLevelCopy(100.0F), (int)this.height, angle, drawX, drawY, 130);
                MobDrawable drawOptionsShadow = WormMobHead.getAngledDrawable(new GameSprite(BabylonHead.texture_shadow, 0, this.spriteY, 224), null, light, (int)this.height, angle, drawX, drawY + 40, 130);
                topList.add(new MobDrawable() {
                    public void draw(TickManager tickManager) {
                        drawOptions.draw(tickManager);
                    }
                });
                Objects.requireNonNull(drawOptionsShadow);
                tileList.add(drawOptionsShadow);
            }

        }
    }
}
