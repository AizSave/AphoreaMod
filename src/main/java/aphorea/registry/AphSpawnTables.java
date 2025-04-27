package aphorea.registry;

import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.dungeon.DungeonBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;

public class AphSpawnTables {
    public static void modifySpawnTables() {
        Biome.defaultSurfaceMobs
                .addLimited(60, "gelslime", 2, 32 * 32)
                .addLimited(4, "wildphosphorslime", 1, 16 * 32, mob -> mob.isHostile);

        Biome.forestCaveMobs
                .add(10, "rockygelslime");

        SwampBiome.surfaceMobs
                .addLimited(1, "pinkwitch", 1, 2048 * 32);

        DungeonBiome.defaultDungeonMobs
                .add(5, "voidadept");
    }
}