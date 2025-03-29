package aphorea.registry;

import necesse.engine.registries.ItemRegistry;

public class AphGlobalIngredients {
    public static void registerCore() {
        ItemRegistry.getItem("magicfoci").addGlobalIngredient("anybasicfoci");
        ItemRegistry.getItem("meleefoci").addGlobalIngredient("anybasicfoci");
        ItemRegistry.getItem("rangefoci").addGlobalIngredient("anybasicfoci");
        ItemRegistry.getItem("summonfoci").addGlobalIngredient("anybasicfoci");
    }
}
