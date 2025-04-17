package aphorea.registry;

import aphorea.biomes.listeners.GeneratedIslandStructuresListener;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GeneratedIslandStructuresEvent;

public class AphListeners {
    public static void addListeners() {
        GameEvents.addListener(GeneratedIslandStructuresEvent.class, new GeneratedIslandStructuresListener());
    }
}
