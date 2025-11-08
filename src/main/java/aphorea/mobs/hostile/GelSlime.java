package aphorea.mobs.hostile;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class GelSlime extends HostileMob {
    public static GameDamage attack = new GameDamage(25);
    public static int attack_knockback = 50;

    public static GameTexture texture;

    public static LootTable lootTable = new LootTable(
            ChanceLootItem.between(0.8f, "gelball", 1, 2),
            ChanceLootItem.between(0.05f, "unstablecore", 1, 1),
            ChanceLootItem.between(0.02f, "gelring", 1, 1)
    );

    public boolean turnPhosphor;

    public GelSlime() {
        super(60);
        setSpeed(30);
        setFriction(2);

        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-14, -12, 28, 24);
        selectBox = new Rectangle(-14, -7 - 14, 28, 28);
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        if (client == null) {
            return false;
        }

        return !AphData.gelSlimesNulled(client.getLevel().getWorldEntity()) &&
                !client.getLevel().getWorldEntity().isNight() &&
                (new MobSpawnLocation(this, targetX, targetY)).checkMobSpawnLocation().checkNotOnSurfaceInsideOnFloor().checkMaxHostilesAround(4, 8, client).validAndApply();
    }

    @Override
    public void init() {
        super.init();
        ai = new BehaviourTreeAI<>(this, new GelSlimeAI<>(() -> this.getServer().world.worldEntity.isNight(), 4 * 32, attack, attack_knockback, 40000));
        turnPhosphor = GameRandom.globalRandom.getChance(0.03F);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("turnPhosphor", this.turnPhosphor);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.turnPhosphor = save.getBoolean("turnPhosphor", false, false);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
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
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;

        Point sprite = getAnimSprite(x, y, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 64)
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
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void collidedWith(Mob other) {
        super.collidedWith(other);
        if (isServer()) {
            other.addBuff(new ActiveBuff(AphBuffs.STICKY, other, 1000, this), true);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (turnPhosphor && this.getLevel().getWorldEntity().isNight()) {
            Mob phosphor = MobRegistry.getMob("wildphosphorslime", this.getLevel());
            this.getLevel().entityManager.addMob(phosphor, this.x, this.y);
            this.remove();
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        if (attacker != null && attacker.getAttackOwner() != null) {
            if (Arrays.stream(attackers.toArray()).noneMatch((a -> ((Attacker) a).getAttackOwner() != null && ((Attacker) a).getAttackOwner().isPlayer))) {
                this.dropsLoot = false;
            }
        }
        super.onDeath(attacker, attackers);
    }

    public static class GelSlimeAI<T extends Mob> extends SelectorAINode<T> {
        public final EscapeAINode<T> escapeAINode;
        public final CollisionOnlyPlayerChaserAI<T> collisionPlayerChaserAI;
        public final WandererAINode<T> wandererAINode;

        public GelSlimeAI(final Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
            this.addChild(this.escapeAINode = new EscapeAINode<T>() {
                public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                    if (mob.isHostile && !mob.isSummoned && mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
                        return true;
                    } else {
                        return shouldEscape != null && shouldEscape.get();
                    }
                }
            });
            this.addChild(this.collisionPlayerChaserAI = new CollisionOnlyPlayerChaserAI<>(searchDistance, damage, knockback));
            this.addChild(this.wandererAINode = new WandererAINode<>(wanderFrequency));
        }
    }

    public static class CollisionOnlyPlayerChaserAI<T extends Mob> extends CollisionChaserAI<T> {
        public CollisionOnlyPlayerChaserAI(int searchDistance, GameDamage damage, int knockback) {
            super(searchDistance, damage, knockback);
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
            return TargetFinderAINode.streamPlayers(mob, base, distance).map(playerMob -> playerMob);
        }
    }

}
