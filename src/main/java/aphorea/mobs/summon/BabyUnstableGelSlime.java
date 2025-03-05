package aphorea.mobs.summon;

import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry.Textures;
import necesse.engine.util.GameRandom;
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
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class BabyUnstableGelSlime extends AttackingFollowingMob {

    public static GameTexture texture;

    public BabyUnstableGelSlime() {
        super(5);
        this.setSpeed(40);
        this.setFriction(2);
        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-12, -14, 24, 24);
        selectBox = new Rectangle(-13, -14, 26, 24);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<Mob>(this, new PlayerFollowerCollisionChaserAI<>(10 * 32, this.summonDamage, 30, 1000, 640, 64));
    }

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

        if (!this.isWaterWalking()) addShadowDrawables(tileList, x, y, light, camera);
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY);
    }

    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
        if (buff.buff != AphBuffs.STICKY) super.addBuff(buff, sendUpdatePacket);
    }
}
