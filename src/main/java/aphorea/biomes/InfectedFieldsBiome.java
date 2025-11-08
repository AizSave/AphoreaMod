package aphorea.biomes;

import aphorea.registry.AphObjects;
import aphorea.registry.AphTiles;
import aphorea.utils.AphColors;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.entity.mobs.PlayerMob;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.regionSystem.Region;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Override
    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return !spot.tile.level.isCave ? surfaceFish : super.getFishingLootTable(spot);
    }

    @Override
    public MobSpawnTable getCritterSpawnTable(Level level) {
        return !level.isCave ? surfaceCritters : caveCritters;
    }

    @Override
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        } else {
            return level.getIslandDimension() == -2 ? deepCaveMobs : caveMobs;
        }
    }

    @Override
    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(MusicRegistry.GrindTheAlarms);
    }

    @Override
    public GameTile getUnderLiquidTile(Level level, int tileX, int tileY) {
        return level.isCave ? TileRegistry.getTile(TileRegistry.dirtID) : TileRegistry.getTile(TileRegistry.sandID);
    }

    @Override
    public int getGenerationWaterTileID() {
        return AphTiles.INFECTED_WATER;
    }

    @Override
    public int getGenerationCaveLavaTileID() {
        return AphTiles.INFECTED_WATER;
    }

    @Override
    public int getGenerationDeepCaveLavaTileID() {
        return AphTiles.INFECTED_WATER;
    }

    @Override
    public int getGenerationTerrainTileID() {
        return AphTiles.INFECTED_GRASS;
    }

    @Override
    public int getGenerationCaveRockObjectID() {
        return AphObjects.GEL_ROCK;
    }

    @Override
    public int getGenerationCaveTileID() {
        return super.getGenerationCaveTileID();
    }

    @Override
    public void initializeGeneratorStack(BiomeGeneratorStack stack) {
        super.initializeGeneratorStack(stack);
        stack.addRandomSimplexVeinsBranch("infectedTrees", 2.0F, 0.2F, 1.0F, 0);
        stack.addRandomVeinsBranch("infectedTungsten", 0.16F, 3, 6, 0.4F, 2, false);
    }

    @Override
    public void generateRegionSurfaceTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionSurfaceTerrain(region, stack, random);
        int grassTile = AphTiles.INFECTED_GRASS;
        stack.startPlaceOnVein(this, region, random, "infectedTrees").onlyOnTile(grassTile).chance(0.07999999821186066).placeObject("infectedtree");
        stack.startPlace(this, region, random).onlyOnTile(grassTile).chance(0.4000000059604645).placeObject("infectedgrass");
        stack.startPlace(this, region, random).chance(0.0024999999441206455).placeObject("surfacegelrock");
        region.updateLiquidManager();
    }

    @Override
    public void generateRegionCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.004999999888241291).placeObject("spinelcluster");
        stack.startPlace(this, region, random).chance(0.009999999776482582).placeObject("spinelclustersmall");
        stack.startPlaceOnVein(this, region, random, "infectedTungsten").onlyOnObject(AphObjects.GEL_ROCK).placeObjectForced("tungstenoregelrock");
    }

    @Override
    public void generateRegionDeepCaveTerrain(Region region, BiomeGeneratorStack stack, GameRandom random) {
        super.generateRegionDeepCaveTerrain(region, stack, random);
        stack.startPlace(this, region, random).chance(0.004999999888241291).placeObject("deepcaverock");
        stack.startPlace(this, region, random).chance(0.009999999776482582).placeObject("deepcaverocksmall");
        stack.startPlace(this, region, random).chance(0.029999999329447746).placeCrates(new String[]{"crate"});
        stack.startPlaceOnVein(this, region, random, "forestWildCaveGlow").onlyOnTile(TileRegistry.deepRockID).chance(0.20000000298023224).placeObject("wildcaveglow");
        stack.startPlaceOnVein(this, region, random, "forestDeepCopper").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("copperoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepIron").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("ironoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepGold").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("goldoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepObsidian").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("obsidianrock");
        stack.startPlaceOnVein(this, region, random, "forestDeepTungsten").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("tungstenoredeeprock");
        stack.startPlaceOnVein(this, region, random, "forestDeepLifeQuartz").onlyOnObject(ObjectRegistry.deepRockID).placeObjectForced("lifequartzdeeprock");
    }

    @Override
    public Color getDebugBiomeColor() {
        return AphColors.spinel;
    }

    @Override
    public CaveRuins getNewCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    @Override
    public CaveRuins getNewDeepCaveRuinsPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    @Override
    public RandomCaveChestRoom getNewCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    @Override
    public RandomCaveChestRoom getNewDeepCaveChestRoomPreset(GameRandom random, AtomicInteger lootRotation) {
        return null;
    }

    static {
        surfaceMobs = new MobSpawnTable().addLimited(100, "infectedtreant", 10, 100 * 32).add(20, "rockygelslime").add(1, "stabbybush");
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
