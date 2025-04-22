package aphorea.biomes.listeners;

import aphorea.biomes.presets.RuneInventorHousePreset;
import necesse.engine.GameEventListener;
import necesse.engine.events.worldGeneration.GeneratedIslandStructuresEvent;
import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.generationModules.PresetGeneration;

import java.awt.*;

public class GeneratedIslandStructuresListener extends GameEventListener<GeneratedIslandStructuresEvent> {

    @Override
    public void onEvent(GeneratedIslandStructuresEvent event) {
        boolean initialIsland = BiomeRegistry.defaultSpawnIslandFilter.test(new Point(event.level.getIslandX(), event.level.getIslandY()));

        PresetGeneration presets = new PresetGeneration(event.level);

        if (initialIsland) {
            presets.addOccupiedSpace(
                    event.level.width / 2 - 25,
                    event.level.height / 2 - 25,
                    50,
                    50
            );
        }

        generateRuneInventorHousePreset(event, presets, initialIsland);
    }

    public static void generateRuneInventorHousePreset(GeneratedIslandStructuresEvent event, PresetGeneration presets, boolean initialIsland) {
        if (initialIsland || shouldGenerateRuneInventorHousePreset(event)) {
            presets.findRandomValidPositionAndApply(event.islandGeneration.random, 200, new RuneInventorHousePreset(event.islandGeneration.random), 10, true, false, false);
        }
    }

    public static boolean shouldGenerateRuneInventorHousePreset(GeneratedIslandStructuresEvent event) {
        if (!(event.level.biome == BiomeRegistry.FOREST || event.level.biome == BiomeRegistry.PLAINS)) return false;
        return event.islandGeneration.random.getChance(0.05F);
    }
}