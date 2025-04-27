package aphorea.registry;

import aphorea.biomes.levels.InfectedFieldsCaveLevel;
import aphorea.biomes.levels.InfectedFieldsSurfaceLevel;
import aphorea.biomes.levels.InfectedTrialRoomLevel;
import necesse.engine.registries.LevelRegistry;

public class AphLevels {
    public static void registerCore() {
        LevelRegistry.registerLevel("infectedfieldssurface", InfectedFieldsSurfaceLevel.class);
        LevelRegistry.registerLevel("infectedfieldscave", InfectedFieldsCaveLevel.class);
        LevelRegistry.registerLevel("infectedtrialroom", InfectedTrialRoomLevel.class);
    }
}
