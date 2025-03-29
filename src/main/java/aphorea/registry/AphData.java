package aphorea.registry;

import aphorea.data.AphSwampLevelData;
import aphorea.data.AphWorldData;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.registries.WorldDataRegistry;

public class AphData {
    public static void registerCore() {
        WorldDataRegistry.registerWorldData("aphoreaworlddata", AphWorldData.class);
        LevelDataRegistry.registerLevelData("aphoreaswampleveldata", AphSwampLevelData.class);
    }
}
