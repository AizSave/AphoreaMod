package aphorea.mobs.runicsummons;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry.Textures;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
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

public class RunicVultureHatchling extends RunicFlyingAttackingFollowingMob {
    public int count;

    public RunicVultureHatchling() {
        super(10);
        this.accelerationMod = 1.0F;
        this.moveAccuracy = 10;
        this.setSpeed(60.0F);
        this.setFriction(1.0F);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle(-20, -34, 40, 36);
    }

    public GameDamage getCollisionDamage(Mob target) {
        float damagePercent = effectNumber / 100;
        if (target.isBoss()) {
            damagePercent /= 50;
        } else if (target.isPlayer || target.isHuman) {
            damagePercent /= 5;
        }

        return new GameDamage(DamageTypeRegistry.TRUE, target.getMaxHealth() * damagePercent);
    }

    public int getCollisionKnockback(Mob target) {
        return 15;
    }

    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null && target != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, (float) knockback, this);
            this.collisionHitCooldowns.startCooldown(target);
        }

    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new PlayerFlyingFollowerCollisionChaserAI<>(576, null, 15, 500, 640, 64), new FlyingAIMover());

        count = 0;
    }

    public void serverTick() {
        super.serverTick();
        count++;

        if (count >= 200) {
            if (this.isFollowing()) {
                ServerClient c = this.getFollowingServerClient();
                if (c != null) {
                    c.removeFollower(this, false, false);
                }
            }
            this.remove();
        }
    }

    public void setFacingDir(float deltaX, float deltaY) {
        if (deltaX < 0.0F) {
            this.setDir(0);
        } else if (deltaX > 0.0F) {
            this.setDir(1);
        }

    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), Textures.vultureHatchling, GameRandom.globalRandom.nextInt(4), 2, 32, this.x, this.y, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 32 - 16;
        int dir = this.getDir();
        long time = level.getWorldEntity().getTime() % 350L;
        byte sprite;
        if (time < 100L) {
            sprite = 0;
        } else if (time < 200L) {
            sprite = 1;
        } else if (time < 300L) {
            sprite = 2;
        } else {
            sprite = 3;
        }

        float rotate = this.dx / 10.0F;
        DrawOptions options = Textures.vultureHatchling.initDraw().sprite(sprite, 0, 64).light(light).mirror(dir == 0, false).rotate(rotate, 32, 32).pos(drawX, drawY);
        topList.add((tm) -> {
            options.draw();
        });
        this.addShadowDrawables(tileList, x, y, light, camera);
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = Textures.vultureHatchling_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 13;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

}