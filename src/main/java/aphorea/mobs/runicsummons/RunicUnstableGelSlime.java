package aphorea.mobs.runicsummons;

import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry.Textures;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class RunicUnstableGelSlime extends RunicAttackingFollowingMob {
    public static GameTexture texture = BabyUnstableGelSlime.texture;

    public int count;

    public RunicUnstableGelSlime() {
        super(5);
        this.setSpeed(40);
        this.setFriction(2);
        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-12, -14, 24, 24);
        selectBox = new Rectangle(-13, -14, 26, 24);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new PlayerFollowerCollisionChaserAI<Mob>(Integer.MAX_VALUE, new GameDamage(0), 30, 1000, Integer.MAX_VALUE, 64) {
            @Override
            public boolean attackTarget(Mob mob, Mob target) {
                float damagePercent = 0.05F;
                if (target.isBoss()) {
                    damagePercent /= 50;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5;
                }
                return CollisionChaserAINode.simpleAttack(mob, target, new GameDamage(target.getMaxHealth() * damagePercent, 1000000), this.knockback);
            }
        });

        count = 0;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        count++;

        if (count >= 20 * effectNumber) {
            if (this.isFollowing()) {
                ((ItemAttackerMob) this.getFollowingMob()).serverFollowersManager.removeFollower(this, false, false);
            }
            this.remove();
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
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

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32 / 2;
        int drawY = camera.getDrawY(y) - 51 / 2;

        Point sprite = getAnimSprite(x, y, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 64 / 2)
                .light(light)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!this.isWaterWalking()) addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }
}
