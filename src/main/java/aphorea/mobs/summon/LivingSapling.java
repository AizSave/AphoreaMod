package aphorea.mobs.summon;

import aphorea.mobs.hostile.InfectedTreant;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LivingSapling extends AttackingFollowingMob {
    public static Map<Integer, Integer> hitCount = new HashMap<>();

    public static int getHitCount(Mob mob) {
        return hitCount.getOrDefault(mob.getUniqueID(), 0);
    }

    public static void setHitCount(Mob mob, int amount) {
        hitCount.put(mob.getUniqueID(), amount);
    }

    public static GameTexture texture;
    public static GameTexture texture_shadow;

    public static String leavesTextureName = "oakleaves";
    public static Supplier<GameTextureSection> leavesTexture;

    public int jump = 0;

    static public float jumpHeight = 10;
    static public int jumpDuration = 4;

    public boolean mirrored;
    public int spriteX;

    public LivingSapling() {
        super(100);
        this.setSpeed(40);
        this.setFriction(5);
        collision = new Rectangle(-8, -4, 16, 12);
        hitBox = new Rectangle(-14, -8, 28, 20);
        selectBox = new Rectangle(-16, -16, 32, 32);

        GameRandom random = new GameRandom();
        this.mirrored = random.getChance(0.5F);
        this.spriteX = random.getIntBetween(0, 5);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new PlayerFollowerCollisionChaserAI<LivingSapling>(10 * 32, this.summonDamage, 30, 1000, 640, 64) {
            @Override
            public boolean attackTarget(LivingSapling mob, Mob target) {
                if (isServer() && target.isHostile) {
                    int attacks = getHitCount(getAttackOwner()) + 1;
                    if (attacks >= 10) {
                        attacks = 0;
                        AphMagicHealing.healMob(getAttackOwner(), getAttackOwner(), 4);
                    }
                    setHitCount(getAttackOwner(), attacks);
                }
                return super.attackTarget(mob, target);
            }
        });
        jump = 0;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (dx == 0 && dy == 0) {
            jump = 0;
        } else {
            jump++;

            if (jump > jumpDuration) {
                jump = 0;
            }
        }
        if (jump == 0) {
            this.setFriction(20);
        } else {
            this.setFriction(0.1F);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (dx == 0 && dy == 0) {
            jump = 0;
        } else {
            jump++;

            if (jump > jumpDuration) {
                jump = 0;
            }
        }
        if (jump == 0) {
            this.setFriction(20);
        } else {
            this.setFriction(0.1F);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32 / 2;
        int drawY = camera.getDrawY(y) - 32 / 2;

        drawY -= (int) (Math.sin((float) jump / jumpDuration * Math.PI) * jumpHeight);
        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(spriteX, 0, 32)
                .light(light)
                .mirror(mirrored, false)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!this.isWaterWalking()) addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = texture_shadow;
        int drawX = camera.getDrawX(x) - 32 / 2;
        int drawY = camera.getDrawY(y) - 32 / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().light(light).mirror(mirrored, false).pos(drawX, drawY);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int leaves = GameRandom.globalRandom.getIntBetween(1, 2);
        this.spawnLeafParticles(getLevel(), (int) x, (int) y, 10, leaves, new Point2D.Double(), 0.0F);
    }

    public void spawnLeafParticles(Level level, int x, int y, int minStartHeight, int amount, Point2D.Double windDir, float windSpeed) {
        if (InfectedTreant.leavesTexture != null) {
            TreeObject.spawnLeafParticles(level, x, y, 16, minStartHeight, 14, amount, windDir, windSpeed, InfectedTreant.leavesTexture);
        }
    }
}
