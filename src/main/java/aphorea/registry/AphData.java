package aphorea.registry;

import aphorea.data.AphWorldData;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.world.WorldEntity;

public class AphData {
    public static AphWorldData worldData;

    public static void registerCore() {
        WorldDataRegistry.registerWorldData("aphoreaworlddata", AphWorldData.class);

        worldData = new AphWorldData();
    }

    public static AphWorldData getWorldData(WorldEntity world) {
        return worldData.getData(world);
    }

    public static boolean gelSlimesNulled(WorldEntity world) {
        return getWorldData(world).gelSlimesNulled;
    }

    public static boolean spinelCured(WorldEntity world) {
        return getWorldData(world).spinelCured;
    }
}
