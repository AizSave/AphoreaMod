package aphorea.mobs.bosses.minions.babylon;

import aphorea.mobs.bosses.BabylonTowerMob;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.mobMovement.*;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BabylonHead extends BossWormMobHead<BabylonBody, BabylonHead> {
    private SoundPlayer sound;
    public static float lengthPerBodyPart;
    public static float waveLength;
    public static GameDamage headCollisionDamage;
    public static GameDamage bodyCollisionDamage;

    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static GameTexture icon;

    public BabylonHead() {
        super(100, waveLength, 100.0F, 7, 0.0F, -5.0F);
        this.moveAccuracy = 100;
        this.movementUpdateCooldown = 2000;
        this.movePosTolerance = 100.0F;
        this.setSpeed(250.0F);
        this.accelerationMod = 1.0F;
        this.decelerationMod = 1.0F;
        this.collision = new Rectangle(-30, -25, 60, 50);
        this.hitBox = new Rectangle(-40, -35, 80, 70);
        this.selectBox = new Rectangle(-40, -60, 80, 80);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
    }

    protected void onAppearAbility() {
        super.onAppearAbility();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
        }

    }

    protected float getDistToBodyPart(BabylonBody bodyPart, int index, float lastDistance) {
        return index >= 1 ? lengthPerBodyPart + 10.0F : lengthPerBodyPart;
    }

    protected BabylonBody createNewBodyPart(int index) {
        BabylonBody bodyPart = new BabylonBody();
        bodyPart.spriteY = index + 1;
        bodyPart.sharesHitCooldownWithNext = true;
        bodyPart.relaysBuffsToNext = true;
        return bodyPart;
    }

    protected void playMoveSound() {
    }

    public GameDamage getCollisionDamage(Mob target) {
        return headCollisionDamage;
    }

    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0F && super.canCollisionHit(target);
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new BabylonHead.BabylonHeadAI<>(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
        }

    }

    public float getWaveHeight(float length) {
        return super.getWaveHeight(length);
    }

    public void clientTick() {
        super.clientTick();
        if (this.sound == null || this.sound.isDone()) {
            this.sound = SoundManager.playSound(GameResources.wind, SoundEffect.effect(this).falloffDistance(1400).volume(0.8F));
        }

        if (this.sound != null) {
            this.sound.refreshLooping(1.0F);
        }

        SoundManager.setMusic(MusicRegistry.DragonsHoard, SoundManager.MusicPriority.EVENT, 1.5F);
        float mod = Math.abs((float) Math.pow(getBabylonTowerHealthPerc(), 0.5) - 1.0F);
        this.setSpeed(120.0F + mod * 90.0F);
    }

    public void serverTick() {
        super.serverTick();
        float mod = Math.abs((float) Math.pow(getBabylonTowerHealthPerc(), 0.5) - 1.0F);
        this.setSpeed(120.0F + mod * 90.0F);
    }

    public float getBabylonTowerHealthPerc() {
        BabylonTowerMob babylonTowerMob = (BabylonTowerMob) getLevel().entityManager.mobs.stream()
                .filter(m -> Objects.equals(m.getStringID(), "babylontower"))
                .min(Comparator.comparingDouble(m -> m.getDistance(this)))
                .orElse(null);

        if(babylonTowerMob == null) {
            return 1;
        }

        return babylonTowerMob.getHealth() / (float) babylonTowerMob.getMaxHealth();
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level
            level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isVisible()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 112;
            int drawY = camera.getDrawY(this.y);
            float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
            final MobDrawable headDrawable = WormMobHead.getAngledDrawable(new GameSprite(texture, 0, 0, 224), null, light.minLevelCopy(100.0F), (int) this.height, headAngle, drawX, drawY, 130);
            MobDrawable headDrawableShadow = WormMobHead.getAngledDrawable(new GameSprite(texture_shadow, 0, 0, 224), null, light, (int) this.height, headAngle, drawX, drawY + 40, 130);
            new ComputedObjectValue<>(null, () -> 0.0);
            ComputedObjectValue<GameLinkedList<WormMoveLine>.Element, Double> shoulderLine = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 70.0);
            final MobDrawable shoulderDrawable;
            MobDrawable shoulderDrawableShadow;
            if (shoulderLine.object != null) {
                Point2D.Double shoulderPos = WormMobHead.linePos(shoulderLine);
                GameLight shoulderLight = level.getLightLevel((int) (shoulderPos.x / 32.0), (int) (shoulderPos.y / 32.0));
                int shoulderDrawX = camera.getDrawX((float) shoulderPos.x) - 112;
                int shoulderDrawY = camera.getDrawY((float) shoulderPos.y);
                float shoulderHeight = this.getWaveHeight(shoulderLine.object.object.movedDist + shoulderLine.get().floatValue());
                float shoulderAngle = GameMath.fixAngle((float) GameMath.getAngle(new Point2D.Double((double) this.x - shoulderPos.x, (double) (this.y - this.height) - (shoulderPos.y - (double) shoulderHeight))));
                shoulderDrawable = WormMobHead.getAngledDrawable(new GameSprite(texture, 0, 1, 224), null, shoulderLight.minLevelCopy(100.0F), (int) shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY, 130);
                shoulderDrawableShadow = WormMobHead.getAngledDrawable(new GameSprite(texture_shadow, 0, 1, 224), null, shoulderLight, (int) shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY + 40, 130);
            } else {
                shoulderDrawable = null;
                shoulderDrawableShadow = null;
            }

            topList.add(new MobDrawable() {
                public void draw(necesse.engine.gameLoop.tickManager.TickManager tickManager) {
                    if (shoulderDrawable != null) {
                        shoulderDrawable.draw(tickManager);
                    }

                    headDrawable.draw(tickManager);
                }
            });
            tileList.add((tickManager1) -> {
                if (shoulderDrawableShadow != null) {
                    shoulderDrawableShadow.draw(tickManager);
                }

                headDrawableShadow.draw(tickManager);
            });
        }
    }

    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    public void drawOnMap(necesse.engine.gameLoop.tickManager.TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 24;
        int drawY = y - 24;
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        icon.initDraw().sprite(0, 0, 152).rotate(headAngle - 90.0F, 24, 24).size(48, 48).draw(drawX, drawY);
    }

    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-15, -15, 30, 30);
    }

    public GameTooltips getMapTooltips() {
        return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of((new ModifierValue<>(BuffModifiers.SLOW, 0.0F)).max(0.0F));
    }

    static {
        lengthPerBodyPart = 60.0F;
        waveLength = 800.0F;
        headCollisionDamage = new GameDamage(60.0F);
        bodyCollisionDamage = new GameDamage(40.0F);
    }

    public static class BabylonHeadAI<T extends BabylonHead> extends SequenceAINode<T> {
        public BabylonHeadAI() {
            this.addChild(new RemoveOnNoBabylonTowerNode<>());
            this.addChild(new TargetFinderAINode<T>(3200) {
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode<T> attackStages = new AttackStageManagerNode<>();
            this.addChild(new IsolateRunningAINode<>(attackStages));
            for (int i = 0; i < 6; i++) {
                attackStages.addChild(new BabylonHead.CirclingStage<>(600, 5000));
                attackStages.addChild(new BabylonHead.ChargeTargetStage<>());
            }
            for (int i = 0; i < 4; i++) {
                attackStages.addChild(new BabylonHead.CirclingStage<>(600, 500));
                attackStages.addChild(new BabylonHead.ChargeTargetStage<>());
            }

        }
    }

    public static class RemoveOnNoBabylonTowerNode<T extends Mob> extends AINode<T> {

        public RemoveOnNoBabylonTowerNode() {
        }

        @Override
        protected void onRootSet(AINode<T> aiNode, T t, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if(mob.getLevel().entityManager.mobs.stream().noneMatch(m -> Objects.equals(m.getStringID(), "babylontower"))) {
                mob.remove();
            }

            return AINodeResult.SUCCESS;
        }
    }

    public static class ChargeTargetStage<T extends BabylonHead> extends AINode<T> implements AttackStageInterface<T> {
        public int startMoveAccuracy;
        public Mob chargingTarget;

        public ChargeTargetStage() {
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.startMoveAccuracy = mob.moveAccuracy;
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                mob.moveAccuracy = 5;
                this.chargingTarget = target;
                this.getBlackboard().mover.setCustomMovement(this, new MobMovementRelative(target, 0.0F, 0.0F));
            }

            mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, mob, 30.0F, null), true);
        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
            mob.moveAccuracy = this.startMoveAccuracy;
            mob.buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
            this.chargingTarget = null;
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (!mob.buffManager.hasBuff(BuffRegistry.SPIDER_CHARGE)) {
                return AINodeResult.SUCCESS;
            } else if (this.chargingTarget != null && !this.chargingTarget.removed()) {
                float distance = mob.getDistance(this.chargingTarget);
                if (distance < 100.0F) {
                    float currentAngle = GameMath.getAngle(new Point2D.Float(mob.dx, mob.dy));
                    float targetAngle = GameMath.getAngle(new Point2D.Float(this.chargingTarget.x - mob.x, this.chargingTarget.y - mob.y));
                    float diff = GameMath.getAngleDifference(currentAngle, targetAngle);
                    float maxAngle = 30.0F;
                    if (Math.abs(diff) >= maxAngle && distance > 75.0F || mob.dx == 0.0F && mob.dy == 0.0F) {
                        mob.moveAccuracy = this.startMoveAccuracy;
                        return AINodeResult.SUCCESS;
                    }
                }

                return AINodeResult.RUNNING;
            } else {
                return AINodeResult.SUCCESS;
            }
        }
    }

    public static class CirclingStage<T extends BabylonHead> extends AINode<T> implements AttackStageInterface<T> {
        public long statTime;
        public int circlingRange;
        public int circlingTime;

        public CirclingStage(int circlingRange, int circlingTime) {
            this.circlingRange = circlingRange;
            this.circlingTime = circlingTime;
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.statTime = mob.getTime();
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            float circlingSpeed = MobMovementCircle.convertToRotSpeed(this.circlingRange, this.mob().getSpeed()) * 1.1F;
            MobMovement movement;
            if (target != null) {
                movement = new MobMovementCircleRelative(this.mob(), target, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean());
            } else {
                movement = new MobMovementCircleLevelPos(this.mob(), this.mob().x, this.mob().y, this.circlingRange, circlingSpeed, GameRandom.globalRandom.nextBoolean());
            }

            this.getBlackboard().mover.setCustomMovement(this, movement);
        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            long endTime = this.statTime + (long) this.circlingTime;
            return mob.getTime() < endTime ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }
    }
}
