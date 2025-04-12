package aphorea.registry;

import aphorea.biomes.levels.InfectedFieldsCaveLevel;
import aphorea.biomes.levels.InfectedFieldsSurfaceLevel;
import necesse.engine.registries.LevelRegistry;

public class AphLevels {
    public static void registerCore() {
        LevelRegistry.registerLevel("infectedfieldssurface", InfectedFieldsSurfaceLevel.class);
        LevelRegistry.registerLevel("infectedfieldscave", InfectedFieldsCaveLevel.class);
    }
}
