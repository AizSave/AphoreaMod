package aphorea.biomes;

import aphorea.biomes.levels.InfectedFieldsFieldsCaveLevel;
import aphorea.biomes.levels.InfectedFieldsSurfaceLevel;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;

public class InfectedFieldsBiome extends Biome {
    public static FishingLootTable surfaceFish;
    public static MobSpawnTable surfaceMobs;
    public static MobSpawnTable caveMobs;
    public static MobSpawnTable deepCaveMobs;
    public static MobSpawnTable surfaceCritters;
    public static MobSpawnTable caveCritters;

    public InfectedFieldsBiome() {
    }

    @Override
    public boolean canRain(Level level) {
        return false;
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new InfectedFieldsSurfaceLevel(islandX, islandY, islandSize, worldEntity, this);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new InfectedFieldsFieldsCaveLevel(islandX, islandY, dimension, worldEntity, this);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new ForestDeepCaveLevel(islandX, islandY, dimension, worldEntity, this);
    }

    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return !spot.tile.level.isCave ? surfaceFish : super.getFishingLootTable(spot);
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        return !level.isCave ? surfaceCritters : caveCritters;
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        } else {
            return level.getIslandDimension() == -2 ? deepCaveMobs : caveMobs;
        }
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? new MusicList(MusicRegistry.PastBehindGlass) : new MusicList(MusicRegistry.GatorsLullaby);
        } else {
            return new MusicList(MusicRegistry.GrindTheAlarms);
        }
    }

    static {
        surfaceMobs = (new MobSpawnTable().add(100, "infectedtreant").add(20, "rockygelslime").add(1, "stabbybush"));
        caveMobs = (new MobSpawnTable());
        deepCaveMobs = (new MobSpawnTable());

        surfaceFish = (new FishingLootTable());
        surfaceCritters = (new MobSpawnTable());
        caveCritters = (new MobSpawnTable());
    }
}
