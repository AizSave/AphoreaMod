package aphorea.mobs.hostile;

import aphorea.projectiles.mob.SpinelGolemBeamProjectile;
import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.RayLinkedList;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.TargetedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.InverterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class SpinelGolem extends HostileMob {
    public static GameTexture texture;

    public static GameDamage damage = new GameDamage(20F, 40F);
    public static int chargeTime = 600;
    public static int stickTime = 100;
    protected long shootTime;
    protected Mob shootTarget;
    protected int shootX;
    protected int shootY;
    public ParticleBeamHandler warningBeam;
    public final TargetedMobAbility startShootingAbility;
    public final CoordinateMobAbility stickShootAbility;
    public final CoordinateMobAbility shootAbility;

    public static LootTable lootTable = new LootTable(
            new OneOfTicketLootItems(
                    7, new LootItem("spinel"),
                    1, new LootItem("lifespinel")
            )
    );

    public SpinelGolem() {
        super(200);
        this.setArmor(20);
        this.setSpeed(20.0F);
        this.setFriction(3.0F);
        this.setKnockbackModifier(0.4F);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 32;
        this.swimMaskOffset = -6;
        this.swimSinkOffset = -4;
        this.startShootingAbility = this.registerAbility(new TargetedMobAbility() {
            protected void run(Mob target) {
                SpinelGolem.this.attackAnimTime = SpinelGolem.chargeTime + SpinelGolem.stickTime + 500;
                SpinelGolem.this.attackCooldown = SpinelGolem.chargeTime + SpinelGolem.stickTime;
                SpinelGolem.this.shootTime = SpinelGolem.this.getWorldEntity().getTime() + (long) SpinelGolem.chargeTime + (long) SpinelGolem.stickTime;
                SpinelGolem.this.shootTarget = target;
                if (SpinelGolem.this.warningBeam != null) {
                    SpinelGolem.this.warningBeam.dispose();
                }

                SpinelGolem.this.warningBeam = null;
                SpinelGolem.this.startAttackCooldown();
                if (target != null) {
                    SpinelGolem.this.showAttack(target.getX(), target.getY(), true);
                } else {
                    SpinelGolem.this.showAttack(SpinelGolem.this.getX() + 100, SpinelGolem.this.getY(), true);
                }

            }
        });
        this.stickShootAbility = this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                SpinelGolem.this.shootX = x;
                SpinelGolem.this.shootY = y;
                SpinelGolem.this.shootTarget = null;
            }
        });
        this.shootAbility = this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                SpinelGolem.this.shootAbilityProjectile(x, y);
            }
        });
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS) ? this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS : this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        } else {
            return null;
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new SpinelGolem.CrystalGolemAI<>(544, 320, this.isSummoned ? 960 : 384));
    }

    @Override
    public float getAttackingMovementModifier() {
        return 0.0F;
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.isServer()) {
            if (this.shootTime != 0L) {
                long timer = this.shootTime - this.getWorldEntity().getTime();
                Point2D.Float target;
                if (this.shootTarget != null) {
                    target = new Point2D.Float(this.shootTarget.x, this.shootTarget.y);
                } else {
                    target = new Point2D.Float((float) this.shootX, (float) this.shootY);
                }

                this.setFacingDir(target.x - this.x, target.y - this.y);
                RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(this.getLevel(), this.x, this.y, target.x - this.x, target.y - this.y, 1000.0, 0, (new CollisionFilter()).projectileCollision());
                if (this.warningBeam == null) {
                    this.warningBeam = (new ParticleBeamHandler(this.getLevel())).particleSize(10, 12).particleThicknessMod(0.2F).endParticleSize(8, 12).distPerParticle(20.0F).thickness(10, 2).speed(50.0F).height(24.0F).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
                }

                int alpha = GameMath.limit(GameMath.lerp((float) timer / (float) (chargeTime + stickTime), 255, 0), 0, 255);
                this.warningBeam.color(AphColors.withAlpha(AphColors.spinel, alpha));
                this.warningBeam.update(rays, delta);
            } else if (this.warningBeam != null) {
                this.warningBeam.dispose();
                this.warningBeam = null;
            }
        }

    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickShooting();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickShooting();
    }

    private void tickShooting() {
        if (this.shootTime != 0L) {
            long timer = this.shootTime - this.getWorldEntity().getTime();
            if (!this.isServer()) {
                if (timer <= 0L) {
                    this.shootTime = 0L;
                    return;
                }

                this.spawnChargeParticles();
            } else if (timer <= (long) stickTime && this.shootTarget != null && this.isSamePlace(this.shootTarget)) {
                this.stickShootAbility.runAndSend(this.shootTarget.getX(), this.shootTarget.getY());
            } else if (timer <= 0L) {
                this.shootAbility.runAndSend(this.shootX, this.shootY);
                this.shootTime = 0L;
            }
        }

    }

    public void shootAbilityProjectile(int x, int y) {
        if (this.isServer()) {
            SpinelGolemBeamProjectile p = new SpinelGolemBeamProjectile(this.getLevel(), this, this.x, this.y, (float) x, (float) y, 1000, damage, 20);
            this.getLevel().entityManager.projectiles.add(p);
        }

        if (this.isClient()) {
            if (this.warningBeam != null) {
                this.warningBeam.dispose();
            }

            this.warningBeam = null;
            SoundManager.playSound(GameResources.firespell1, SoundEffect.effect(this).pitch(1.8F).volume(0.8F));
        }

        this.shootTime = 0L;
    }

    public void spawnChargeParticles() {
        for (int i = 0; i < 2; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir((float) angle);
            float range = GameRandom.globalRandom.getFloatBetween(25.0F, 40.0F);
            float startX = this.x + dir.x * range;
            float startY = this.y + 4.0F;
            float endHeight = 29.0F;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0F / (float) lifeTime;
            Color color = GameRandom.globalRandom.getOneOf(AphColors.spinel_lighter, AphColors.spinel_light, AphColors.spinel, AphColors.spinel_dark);
            float hueMod = (float) this.getLevel().getWorldEntity().getLocalTime() / 10.0F % 240.0F;
            float glowHue = hueMod < 120.0F ? hueMod + 200.0F : 440.0F - hueMod;
            this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0F).color(color).givesLight(glowHue, 1.0F).ignoreLight(true).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }

    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crystalGolem, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7 - 6;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        if (this.isAttacking) {
            sprite.x = 0;
        }

        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final DrawOptions drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light.minLevelCopy(100.0F)).pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                swimMask.use();
                drawOptions.draw();
                swimMask.stop();
            }
        });
        this.addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.warningBeam != null) {
            this.warningBeam.dispose();
        }

        this.warningBeam = null;
    }

    public static class CrystalGolemAI<T extends SpinelGolem> extends SequenceAINode<T> {
        public final EscapeAINode<T> escapeAINode;
        public final CooldownAttackTargetAINode<T> shootAtTargetNode;
        public final TargetFinderAINode<T> targetFinderNode;
        public final ChaserAINode<T> chaserNode;

        public CrystalGolemAI(int shootDistance, int meleeDistance, int searchDistance) {
            this.addChild(new InverterAINode<>(this.escapeAINode = new EscapeAINode<T>() {
                public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                    return mob.isHostile && !mob.isSummoned && mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING);
                }
            }));
            if (shootDistance > 0) {
                this.addChild(this.shootAtTargetNode = new CooldownAttackTargetAINode<T>(CooldownAttackTargetAINode.CooldownTimer.TICK, SpinelGolem.chargeTime + SpinelGolem.stickTime + 500, shootDistance) {
                    public boolean attackTarget(T mob, Mob target) {
                        if (mob.canAttack() && mob.shootTime == 0L) {
                            mob.startShootingAbility.runAndSend(target);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                this.shootAtTargetNode.attackTimer = this.shootAtTargetNode.attackCooldown;
            } else {
                this.shootAtTargetNode = null;
            }

            TargetFinderDistance<T> targetFinder = new TargetFinderDistance<>(searchDistance);
            targetFinder.targetLostAddedDistance = searchDistance * 2;
            this.addChild(this.targetFinderNode = new TargetFinderAINode<T>(targetFinder) {
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
                }
            });
            this.addChild(this.chaserNode = new ChaserAINode<T>(meleeDistance, false, true) {
                public boolean attackTarget(T mob, Mob target) {
                    return false;
                }
            });
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }
}
