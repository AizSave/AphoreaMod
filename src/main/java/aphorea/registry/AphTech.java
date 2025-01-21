package aphorea.registry;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Tech;

public class AphTech {
    public static Tech RUNES;

    public static void registerCore() {
        RUNES = RecipeTechRegistry.registerTech("runes", "runestable");
    }
}
