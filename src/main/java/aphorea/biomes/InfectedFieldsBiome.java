package aphorea.biomes;

import aphorea.biomes.levels.InfectedFieldsCaveLevel;
import aphorea.biomes.levels.InfectedFieldsSurfaceLevel;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.ItemRegistry;
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
        return new InfectedFieldsCaveLevel(islandX, islandY, dimension, worldEntity, this);
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
        return new MusicList(MusicRegistry.GrindTheAlarms);
    }

    static {
        surfaceMobs = new MobSpawnTable().add(100, "infectedtreant").add(20, "rockygelslime").add(1, "stabbybush");
        caveMobs = new MobSpawnTable();
        deepCaveMobs = new MobSpawnTable();

        final int woodTrashTickets = 200 / 10;

        surfaceFish = new FishingLootTable()
                .startCustom(300).onlyTile("infectedwatertile").end("rockfish")
                .startCustom(100).onlyTile("infectedwatertile").end("fossilrapier")

                // Trash
                .startCustom(400).onlyTile("infectedwatertile").end(
                        (spot, random) -> ItemRegistry.getItem("infectedlog").getDefaultLootItem(random, random.getIntBetween(1, 3))
                )

                // Wood trash
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodaxe")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodpickaxe")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodshovel")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodfishingrod")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodsword")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodspear")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodboomerang")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodbow")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodstaff")
                .startCustom(woodTrashTickets).onlyTile("infectedwatertile").end("woodshield");
        surfaceCritters = new MobSpawnTable();
        caveCritters = new MobSpawnTable();
    }
}
