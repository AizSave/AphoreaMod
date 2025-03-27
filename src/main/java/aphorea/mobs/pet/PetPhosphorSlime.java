package aphorea.mobs.pet;

import aphorea.mobs.friendly.WildPhosphorSlime;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class PetPhosphorSlime extends PetFollowingMob {
    public static GameTexture texture;
    public static GameTexture texture_scared;

    int time;
    int sprite;

    int lightTime;
    static int lightCycle = 80;
    int dayCount = 0;

    public PetPhosphorSlime() {
        super(1);
        this.setSpeed(30.0F);
        this.setFriction(0.5F);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-7, -5, 14, 10);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28 - getFlyingHeight(), 32, 34 + getFlyingHeight());
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new PlayerFollowerAINode<>(480, 32));
    }

    @Override
    public void clientTick() {
        super.clientTick();
        time++;
        if (time >= 3) {
            time = 0;
            sprite++;
        }
        if (lightTime >= lightCycle) {
            lightTime = 0;
        }
        float lightVariation = (float) Math.sin(Math.toRadians((float) lightTime * 360 / lightCycle));
        int lightColorVariation = 64 - (int) (64 * lightVariation);
        int lightLevelVariation = (int) (10 * lightVariation);
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, new Color(255 - lightColorVariation, 208, lightColorVariation), 1F, 120 + lightLevelVariation);
        lightTime++;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (isScared(getLevel())) {
            if (!buffManager.hasBuff("movespeedburst")) {
                buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, this, 3000, this), true);
            }
            if (dayInSurface(getLevel())) {
                dayCount++;
                if (dayCount > 400) {
                    getServer().network.sendToClientsAtEntireLevel(new WildPhosphorSlime.PhosphorSlimeParticlesPacket(x, y), getLevel());
                    this.remove();
                }
            }
        }
    }

    @Override
    protected void playDeathSound() {
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51 - getFlyingHeight();

        Point sprite = new Point(this.sprite % 5, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = (isScared(level) ? texture_scared : texture).initDraw()
                .sprite(sprite.x, sprite.y, 64)
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

    public boolean isScared(Level level) {
        return dayInSurface(level) || level.entityManager.streamAreaMobsAndPlayers(this.x, this.y, 500).anyMatch(m -> m.isHostile && m.getDistance(this) <= 500) || level.entityManager.streamAreaMobsAndPlayers(this.x, this.y, 500).noneMatch(m -> m.getDistance(this) <= 500);
    }

    public static boolean dayInSurface(Level level) {
        return level.getIslandDimension() == 0 && !level.getWorldEntity().isNight();
    }
}
