package aphorea.registry;

import aphorea.levels.InfectedTrialRoomLevel;
import necesse.engine.registries.LevelRegistry;

public class AphLevels {
    public static void registerCore() {
        LevelRegistry.registerLevel("infectedtrialroom", InfectedTrialRoomLevel.class);
    }
}
