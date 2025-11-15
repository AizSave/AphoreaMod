package aphorea.tiles;

import aphorea.registry.AphData;
import aphorea.utils.AphColors;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SimulatePriorityList;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InfectedGrassTile extends TerrainSplatterTile {
    public static double growChance = GameMath.getAverageSuccessRuns(7000.0);
    public static double spreadChance = GameMath.getAverageSuccessRuns(850.0);
    private final GameRandom drawRandom;

    private static final Map<Integer, Long> lastHit = new HashMap<>();
    private static final Map<Integer, Integer> consecutiveHits = new HashMap<>();

    public static Attacker INFECED_GRASS_ATTACKER = new Attacker() {
        public GameMessage getAttackerName() {
            return new StaticMessage("Infected Grass at day");
        }

        public DeathMessageTable getDeathMessages() {
            return new DeathMessageTable().add(new LocalMessage("deaths", "default"));
        }

        public Mob getFirstAttackOwner() {
            return null;
        }
    };

    public InfectedGrassTile() {
        super(false, "infectedgrass");
        this.mapColor = AphColors.infected;
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    @Override
    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new ChanceLootItem(0.04F, "infectedgrassseed"));
    }

    @Override
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        addSimulateGrow(level, x, y, growChance, ticks, "infectedgrass", list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, SimulatePriorityList list, boolean sendChanges) {
        addSimulateGrow(level, tileX, tileY, growChance, ticks, growObjectID, (object, l, x, y, r) -> object.canPlace(l, x, y, r, false) == null, list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, GrassTile.CanPlacePredicate canPlace, SimulatePriorityList list, boolean sendChanges) {
        if (level.getObjectID(tileX, tileY) == 0) {
            double runs = Math.max(1.0, GameMath.getRunsForSuccess(growChance, GameRandom.globalRandom.nextDouble()));
            long remainingTicks = (long) ((double) ticks - runs);
            if (remainingTicks > 0L) {
                GameObject obj = ObjectRegistry.getObject(ObjectRegistry.getObjectID(growObjectID));
                if (canPlace.check(obj, level, tileX, tileY, 0)) {
                    list.add(tileX, tileY, remainingTicks, () -> {
                        if (canPlace.check(obj, level, tileX, tileY, 0)) {
                            obj.placeObject(level, tileX, tileY, 0, false);
                            level.objectLayer.setIsPlayerPlaced(tileX, tileY, false);
                            if (sendChanges) {
                                level.sendObjectUpdatePacket(tileX, tileY);
                            }
                        }

                    });
                }
            }
        }

    }

    @Override
    public double spreadToDirtChance() {
        return spreadChance;
    }

    @Override
    public void tick(Level level, int x, int y) {
        if (level.isServer()) {
            if (level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(growChance)) {
                GameObject grass = ObjectRegistry.getObject(ObjectRegistry.getObjectID("infectedgrass"));
                if (grass.canPlace(level, x, y, 0, false) == null) {
                    grass.placeObject(level, x, y, 0, false);
                    level.objectLayer.setIsPlayerPlaced(x, y, false);
                    level.sendObjectUpdatePacket(x, y);
                }
            }

        }
    }

    public static Map<Integer, Long> playersMessageTime = new HashMap<>();

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        if (!AphData.spinelCured(level.getWorldEntity()) && !level.getWorldEntity().isNight() && !level.isCave) {
            if (level.isServer() && !mob.isHostile) {
                if(mob.isPlayer) {
                    PlayerMob player = (PlayerMob) mob;
                    long messageTime = playersMessageTime.getOrDefault(player.getUniqueID(), 0L);
                    long now = player.getTime();
                    if (messageTime + 5000 < now) {
                        playersMessageTime.put(player.getUniqueID(), now);
                        player.getServerClient().sendChatMessage(new LocalMessage("message", "infectedfieldsday"));
                    }
                }

                float damageMultiplier = 0F;
                long lastHitTime = lastHit.getOrDefault(mob.getID(), 0L);
                long currentTime = level.getTime();
                if (lastHitTime + 200 < currentTime) {
                    int consecutiveHitsCount = consecutiveHits.getOrDefault(mob.getID(), 0);
                    if (lastHitTime + 300 > currentTime) {
                        consecutiveHitsCount++;
                    } else {
                        consecutiveHitsCount = 0;
                    }
                    damageMultiplier = consecutiveHitsCount;
                    lastHit.put(mob.getID(), currentTime);
                    consecutiveHits.put(mob.getID(), consecutiveHitsCount);
                }

                if (damageMultiplier != 0) {
                    float damage = 2F * damageMultiplier;
                    mob.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage), 0.0F, 0.0F, 0.0F, INFECED_GRASS_ATTACKER);
                }
            }
        }

    }

    @Override
    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        if (GameRandom.globalRandom.getChance(0.05F) && !level.getWorldEntity().isNight() && !level.isCave && !level.getObject(x, y).drawsFullTile() && level.getLightLevel(x, y).getLevel() > 0F) {
            int posX = x * 32 + GameRandom.globalRandom.nextInt(32);
            int posY = y * 32 + GameRandom.globalRandom.nextInt(32);
            boolean mirror = GameRandom.globalRandom.nextBoolean();
            level.entityManager.addParticle((float) posX, (float) (posY + 30), Particle.GType.COSMETIC).sprite(GameResources.fogParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 32, 16)).fadesAlpha(0.4F, 0.4F).color(AphColors.spinel).alpha(0.4F).size((options, lifeTime, timeAlive, lifePercent) -> {
            }).height(30.0F).dontRotate().movesConstant(GameRandom.globalRandom.getFloatBetween(2.0F, 5.0F) * GameRandom.globalRandom.getOneOf(1.0F, -1.0F), 0.0F).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirror, false)).lifeTime(3000);
        }

    }

    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        synchronized (this.drawRandom) {
            tile = this.drawRandom.seeded(getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }

        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 100;
    }
}
