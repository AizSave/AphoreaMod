package aphorea.mobs.hostile;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VoidAdept extends HostileMob {
    public final CoordinateMobAbility teleportAbility;
    public final CoordinateMobAbility teleportParticle;
    public static AphAreaList showAttackRange = new AphAreaList(
            new AphArea(250, new Color(255, 255, 255))
    ).setDamageType(DamageTypeRegistry.MAGIC);
    public static AphAreaList attackArea = new AphAreaList(
            new AphArea(250, AphColors.palettePinkWitch[2]).setDamageArea(40).setArmorPen(10)
    ).setDamageType(DamageTypeRegistry.MAGIC);


    public static LootTable lootTable = new LootTable(
            LootItem.between("voidshard", 0, 2),
            new ChanceLootItem(0.05f, "adeptsbook")
    );

    public static HumanTexture texture;

    public int attackCount = 0;

    public VoidAdept() {
        super(75);
        this.attackCooldown = 2000;
        this.attackAnimTime = 1500;
        this.setSpeed(40.0F);
        this.setFriction(3.0F);
        this.setArmor(10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;

        this.teleportAbility = this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                if (VoidAdept.this.isClient()) {
                    VoidAdept.this.getLevel().entityManager.addParticle(new SmokePuffParticle(VoidAdept.this.getLevel(), VoidAdept.this.x, VoidAdept.this.y, AphColors.palettePinkWitch[2]), Particle.GType.CRITICAL);
                    VoidAdept.this.getLevel().entityManager.addParticle(new SmokePuffParticle(VoidAdept.this.getLevel(), (float) x, (float) y, AphColors.palettePinkWitch[2]), Particle.GType.CRITICAL);
                }

                VoidAdept.this.setPos((float) x, (float) y, true);
            }
        });

        this.teleportParticle = this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                if (VoidAdept.this.isClient()) {
                    VoidAdept.this.getLevel().entityManager.addParticle(new SmokePuffParticle(VoidAdept.this.getLevel(), (float) x, (float) y, AphColors.palettePinkWitch[2]), Particle.GType.CRITICAL);
                }
            }
        });
    }

    public void init() {
        super.init();
        PlayerChaserWandererAI<VoidAdept> playerChaserAI = new PlayerChaserWandererAI<VoidAdept>(null, 640, 200, 40000, false, false) {
            public boolean attackTarget(VoidAdept mob, Mob target) {
                if (mob.canAttack() && !mob.isAccelerating() && !mob.hasCurrentMovement()) {
                    mob.attack(target.getX(), target.getY(), false);
                    mob.addBuff(new ActiveBuff(AphBuffs.STUN, mob, 1500, mob), true);

                    return true;
                } else {
                    return false;
                }
            }
        };
        playerChaserAI.addChildFirst(new FailerAINode<>(new TeleportOnProjectileHitAINode<VoidAdept>(2000, 3) {
            public boolean teleport(VoidAdept mob, int x, int y) {
                if (mob.isServer()) {
                    if (mob.isAttacking) {
                        mob.teleportParticle.runAndSend(x, y);
                    } else {
                        mob.teleportAbility.runAndSend((int) mob.x, (int) mob.y);
                    }
                    this.getBlackboard().mover.stopMoving(mob);
                }

                return true;
            }
        }));
        this.ai = new BehaviourTreeAI<>(this, playerChaserAI);
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), texture.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    public float getAttackAnimProgress() {
        float progress = (float) (this.getWorldEntity().getTime() - this.attackTime) / (float) this.attackAnimTime;
        if (progress >= 1.0F) {
            this.isAttacking = false;

            if (attackCount == 2) {
                this.attackCount = 0;

                if (this.isServer()) {
                    attackArea.executeAreas(this);

                    int tileX = this.getX() / 32;
                    int tileY = this.getY() / 32;
                    Point moveOffset = this.getPathMoveOffset();
                    ArrayList<Point> possiblePoints = new ArrayList<>();

                    int index;
                    for (index = tileX - 14; index <= tileX + 14; ++index) {
                        for (int y = tileY - 14; y <= tileY + 14; ++y) {
                            int mobX = index * 32 + moveOffset.x;
                            int mobY = y * 32 + moveOffset.y;
                            if (!this.collidesWith(this.getLevel(), mobX, mobY)) {
                                possiblePoints.add(new Point(mobX, mobY));
                            }
                        }
                    }

                    if (!possiblePoints.isEmpty()) {
                        index = GameRandom.globalRandom.nextInt(possiblePoints.size());
                        Point point = possiblePoints.get(index);

                        this.teleportAbility.runAndSend(point.x, point.y);
                    }
                } else if (this.isClient()) {
                    attackArea.showAllAreaParticles(getLevel(), this.x, this.y);
                }
            }
        } else if ((progress >= 0.5F) && attackCount == 1) {
            this.attackCount = 2;

            if (this.isClient()) {
                showAttackRange.showAllAreaParticles(getLevel(), this.x, this.y);
            }
        } else if (attackCount == 0) {
            this.attackCount = 1;

            if (this.isClient()) {
                showAttackRange.showAllAreaParticles(getLevel(), this.x, this.y);
            }
        }

        return Math.min(1.0F, progress);
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = (new HumanDrawOptions(level, texture)).sprite(sprite).dir(dir).mask(swimMask).light(light);
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = getItemAttackDrawOptions(dir, light);
            humanDrawOptions.attackAnim(attackOptions, animProgress);
        }

        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, x, y, light, camera);
    }

    private static ItemAttackDrawOptions getItemAttackDrawOptions(int dir, GameLight light) {
        return ItemAttackDrawOptions.start(dir).itemSprite(texture.body, 0, 9, 32).itemRotatePoint(3, 3).itemEnd().armSprite(texture.body, 0, 8, 32).light(light);
    }

    public int getRockSpeed() {
        return 20;
    }

    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidapp", 3);
    }
}