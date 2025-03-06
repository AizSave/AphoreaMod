package aphorea.mobs.summon;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class Onyx extends FlyingAttackingFollowingMob {
    public static GameTexture texture;

    public int count;

    public Onyx() {
        super(10);
        this.accelerationMod = 1.0F;
        this.moveAccuracy = 10;
        this.setSpeed(100.0F);
        this.setFriction(1.0F);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 40);
    }

    @Override
    public GameDamage getCollisionDamage(Mob target) {
        float damagePercent = 0.05F;
        if (target.isBoss()) {
            damagePercent /= 50;
        } else if (target.isPlayer || target.isHuman) {
            damagePercent /= 5;
        }

        return new GameDamage(target.getMaxHealth() * damagePercent, 1000000);
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 5;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, (float) knockback, this);
            this.collisionHitCooldowns.startCooldown(target);
            if (target.isHostile) {
                getLevel().entityManager.addLevelEvent(new MobHealthChangeEvent(owner, (int) Math.max(owner.getMaxHealth() * 0.01F, 1)));
            }
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new PlayerFlyingFollowerCollisionChaserAI<>(576, null, 15, 500, 640, 32), new FlyingAIMover());

        count = 0;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        count++;

        if (count >= 100) {
            if (this.isFollowing()) {
                ((ItemAttackerMob) this.getFollowingMob()).serverFollowersManager.removeFollower(this, false, false);
            }
            this.remove();
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), texture, i, 8, 32, this.x, this.y, 20.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32).minLevelCopy(100.0F);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 55;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        float bobbing = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0F;
        drawY = (int) ((float) drawY + bobbing);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        final TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 300), dir);
    }

}
