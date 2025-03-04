package aphorea.biomes.levels;

import aphorea.biomes.presets.FarmersRefugeNoHumansNoFloraPreset;
import aphorea.biomes.presets.FishingHutNoHumanPreset;
import aphorea.biomes.presets.HunterCabinNoHumanPreset;
import aphorea.biomes.presets.WitchCabinMadsNoHumanPreset;
import aphorea.registry.AphLootTables;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.WaterTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.*;
import necesse.level.maps.presets.set.*;

import java.util.stream.Stream;

public class InfectedSurfaceLevel extends Level {
    public InfectedSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public InfectedSurfaceLevel(int islandX, int islandY, float islandSize, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.biome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int) (islandSize * 90.0F) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("infectedwatertile");
        int rockTile = TileRegistry.rockID;
        int grassTile = TileRegistry.getTileID("infectedgrasstile");
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), (e) -> {
            if (ig.random.getChance(0.05F)) {
                ig.generateSimpleIsland(this.width / 2, this.height / 2, waterTile, grassTile, rockTile);
            } else {
                ig.generateShapedIsland(waterTile, grassTile, rockTile);
            }

            int rivers = ig.random.getIntBetween(1, 5);

            for (int i = 0; i < rivers && (i <= 0 || !ig.random.getChance(0.8F)); ++i) {
                ig.generateRiver(waterTile, grassTile, rockTile);
            }

            ig.generateLakes(0.1F, waterTile, grassTile, rockTile);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateHeights();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));

        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), (e) -> {
            int infectedTree = ObjectRegistry.getObjectID("infectedtree");
            int grassObject = ObjectRegistry.getObjectID("infectedgrass");
            ig.generateCellMapObjects(0.4F, infectedTree, grassTile, 0.06F);
            ig.generateObjects(grassObject, grassTile, 0.6F);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacegelrock"), -1, 0.002F, false);
        });

        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));

        Preset ruinPreset = new RandomRuinsPreset(ig.random)
                .setChests("barrel", "storagebox", "oakchest", "sprucechest", "pinechest", "palmchest")
                .setTiles("woodfloor", "stonefloor", "snowstonefloor", "swampstonefloor", "sandstonefloor")
                .setWalls("woodwall", "stonewall", "snowstonefloor", "swampstonefloor", "sandstonewall");

        ruinPreset.addInventory(AphLootTables.infectedFieldsSurface, ig.random, 4, 4);

        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), (e) -> {
            PresetGeneration presets = new PresetGeneration(this);

            presets.findRandomValidPositionAndApply(ig.random, 40, ruinPreset, 40, false, false);

            TicketSystemList<Runnable> miniBiomesTicketList = new TicketSystemList<>();

            miniBiomesTicketList.addObject(100, () -> {
                presets.findRandomValidPositionAndApply(ig.random, 200, new BrokenHusbandryFencePreset(ig.random, getRamndomFenceSet(ig)), 10, true, true);
            });
            miniBiomesTicketList.addObject(100, () -> {
                presets.findRandomValidPositionAndApply(ig.random, 200, new HunterCabinNoHumanPreset(ig.random, getRandomFurnitureSet(ig), getRandomWallSet(ig)), 10, true, true);
            });
            miniBiomesTicketList.addObject(100, () -> {
                presets.findRandomValidPositionAndApply(ig.random, 200, new FishingHutNoHumanPreset(ig.random, getRandomWallSet(ig), getRandomTile(ig)), 10, true, true);
            });
            miniBiomesTicketList.addObject(100, () -> {
                presets.findRandomValidPositionAndApply(ig.random, 200, new FarmersRefugeNoHumansNoFloraPreset(ig.random, getRandomFurnitureSet(ig), getRandomWallSet(ig)), 10, true, true);
            });
            miniBiomesTicketList.addObject(100, () -> {
                presets.findRandomValidPositionAndApply(ig.random, 200, new ChristmasHousePreset(ig.random), 10, false, false);
            });
            miniBiomesTicketList.addObject(100, () -> {
                presets.findRandomValidPositionAndApply(ig.random, 200, new WitchCabinMadsNoHumanPreset(ig.random), 10, true, true);
            });

            float chanceToAddMiniBiome = 0.9F;
            while(!miniBiomesTicketList.isEmpty() && ig.random.getChance(chanceToAddMiniBiome)) {
                Runnable miniBiome = miniBiomesTicketList.getAndRemoveRandomObject(ig.random);
                miniBiome.run();
                chanceToAddMiniBiome -= 0.15F;
            }

        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, islandSize, ig));

        GenerationTools.checkValid(this);
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
    }

    public GameTile getUnderLiquidTile(int tileX, int tileY) {
        return TileRegistry.getTile(TileRegistry.rockID);
    }

    public static FenceSet getRamndomFenceSet(IslandGeneration ig) {
        return ig.random.getOneOf(FenceSet.wood, FenceSet.stone, FenceSet.iron);
    }

    public static FurnitureSet getRandomFurnitureSet(IslandGeneration ig) {
        return ig.random.getOneOf(FurnitureSet.dungeon, FurnitureSet.spruce, FurnitureSet.pine, FurnitureSet.oak, FurnitureSet.maple);
    }

    public static WallSet getRandomWallSet(IslandGeneration ig) {
        return ig.random.getOneOf(WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone, WallSet.swampStone);
    }

    public static int getRandomTile(IslandGeneration ig) {
        return ig.random.getOneOf(TileRegistry.woodPathID, TileRegistry.stonePathID, TileRegistry.snowStonePathID);
    }

    @Override
    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        Stream<ModifierValue<?>> modifiers = Stream.concat(
                super.getMobModifiers(mob),
                Stream.of(new ModifierValue<>(BuffModifiers.BLINDNESS, 0.4F))
        );
        return modifiers;
    }
}
