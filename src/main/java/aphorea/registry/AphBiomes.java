package aphorea.registry;

import aphorea.biomes.InfectedFieldsBiome;
import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.biomes.Biome;

public class AphBiomes {
    public static Biome INFECTED_FIELDS;

    public static void registerCore() {
        BiomeRegistry.registerBiome("infectedfields", INFECTED_FIELDS = new InfectedFieldsBiome(), 200, null);
    }
}
