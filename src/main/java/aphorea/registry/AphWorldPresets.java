package aphorea.registry;

import aphorea.presets.worldpresets.*;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.WorldPresetRegistry;
import necesse.engine.util.LevelIdentifier;

public class AphWorldPresets {

    public static void registerCore() {
        WorldPresetRegistry.registerPreset("runeworkshopforest", new RuneInventorWorldPreset(0.001F, BiomeRegistry.FOREST));
        WorldPresetRegistry.registerPreset("runeworkshopplains", new RuneInventorWorldPreset(0.001F, BiomeRegistry.PLAINS));
        WorldPresetRegistry.registerPreset("runeworkshopsnow", new RuneInventorWorldPreset(0.001F, BiomeRegistry.SNOW));
        WorldPresetRegistry.registerPreset("runeworkshopinfectedfields", new RuneInventorWorldPreset(0.001F, AphBiomes.INFECTED_FIELDS));

        // Infected Fields
        WorldPresetRegistry.registerPreset("spinelcaves", new SpinelCavesWorldPreset());
        WorldPresetRegistry.registerPreset("lootlake", new InfectedLootLakeWorldPreset(0.004F, AphBiomes.INFECTED_FIELDS));
        WorldPresetRegistry.registerPreset("spinelfakechest", new SpinelFakeChestWorldPreset(AphBiomes.INFECTED_FIELDS, LevelIdentifier.CAVE_IDENTIFIER, 0.02F));
        WorldPresetRegistry.registerPreset("infectedforestcave", new InfectedForestCaveWorldPreset(AphBiomes.INFECTED_FIELDS, LevelIdentifier.CAVE_IDENTIFIER, 0.008F));
    }

}
