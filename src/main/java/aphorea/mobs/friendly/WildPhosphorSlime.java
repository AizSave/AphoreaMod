package aphorea.mobs.friendly;

import aphorea.mobs.ai.AphRunFromMobsAI;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.*;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class WildPhosphorSlime extends FriendlyMob {
    public static GameTexture texture;
    public static GameTexture texture_scared;

    public static LootTable lootTable = new LootTable(new LootItem("cuberry"));

    int dayCount = 0;
    int time;
    int sprite;

    int lightTime;
    static int lightCycle = 80;

    public WildPhosphorSlime() {
        super(1);
        this.setSpeed(30.0F);
        this.setFriction(0.5F);
        this.collision = new Rectangle(-7, -5, 14, 10);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28 - getFlyingHeight(), 32, 34 + getFlyingHeight());
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new WildPhosphorSlimeAI());
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        time++;
        if (time >= (isScared(this.getLevel()) ? 2 : 3)) {
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
                    getServer().network.sendToClientsAtEntireLevel(new PhosphorSlimeParticlesPacket(x, y), getLevel());
                    this.remove();
                }
            }
        }
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        return this.getLevel() != null ? this.getLevel().regionManager.CANNOT_PASS_DOORS_OPTIONS : null;
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return true;
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

        addShadowDrawables(tileList, x, y, light, camera);
    }

    public boolean isScared(Level level) {
        return dayInSurface(level) || level.entityManager.streamAreaMobsAndPlayers(this.x, this.y, 500).anyMatch(m -> m.isHostile && m.getDistance(this) <= 500);
    }

    public static boolean dayInSurface(Level level) {
        return level.getIslandDimension() == 0 && !level.getWorldEntity().isNight();
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        tooltips.add(Localization.translate("mobtooltip", "usenet"));
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    protected void playDeathSound() {
    }

    @Override
    protected void playHitSound() {
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    public static class WildPhosphorSlimeAI extends SelectorAINode<WildPhosphorSlime> {
        public AphRunFromMobsAI<WildPhosphorSlime> aphRunFromMobsAI;

        public WildPhosphorSlimeAI() {
            this.addChild(new EscapeAINode<WildPhosphorSlime>() {
                @Override
                public boolean shouldEscape(WildPhosphorSlime t, Blackboard<WildPhosphorSlime> blackboard) {
                    return dayInSurface(t.getLevel());
                }
            });

            this.addChild(aphRunFromMobsAI = new AphRunFromMobsAI<>(500, (m) -> m.isHostile));

            this.addChild(new WandererAINode<>(10000));
        }

    }

    public static class PhosphorSlimeParticlesPacket extends Packet {
        public final float x;
        public final float y;

        public PhosphorSlimeParticlesPacket(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            this.x = reader.getNextFloat();
            this.y = reader.getNextFloat();
        }

        public PhosphorSlimeParticlesPacket(float x, float y) {
            this.x = x;
            this.y = y;
            PacketWriter writer = new PacketWriter(this);
            writer.putNextFloat(x);
            writer.putNextFloat(y);
        }

        @Override
        public void processClient(NetworkPacket packet, Client client) {
            if (client.getLevel() != null) {
                apply(client.getLevel(), this.x, this.y);
            }
        }

        public static void apply(Level level, float x, float y) {

            if (level != null && level.isClient()) {
                for (int i = 0; i < 2; i++) {
                    level.entityManager.addParticle(new FleshParticle(
                            level, texture,
                            GameRandom.globalRandom.nextInt(3),
                            8,
                            32,
                            x, y, 20f,
                            0, 0
                    ), Particle.GType.IMPORTANT_COSMETIC);
                }
            }

        }
    }

}