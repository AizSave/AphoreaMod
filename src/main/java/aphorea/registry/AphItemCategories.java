package aphorea.registry;

import necesse.inventory.item.ItemCategory;

public class AphItemCategories {
    public static void registerCore() {
        ItemCategory.createCategory("A-A-E", "equipment", "tools", "healing");
        ItemCategory.createCategory("A-F-A", "misc", "runes");
        ItemCategory.createCategory("A-F-A", "misc", "runes", "baserunes");
        ItemCategory.createCategory("A-F-B", "misc", "runes", "modifierrunes");

        equipmentCategories();
        craftingCategories();
    }

    public static void equipmentCategories() {
        ItemCategory.equipmentManager.createCategory("C-A-A", "tools");
        ItemCategory.equipmentManager.createCategory("C-B-A", "tools", "healingtools");
    }

    public static void craftingCategories() {
        ItemCategory.craftingManager.createCategory("D-B-F", "equipment", "tools", "healingtools");
        ItemCategory.craftingManager.createCategory("J-A-A", "runes");
        ItemCategory.craftingManager.createCategory("J-A-A", "runes", "runesinjectors");
        ItemCategory.craftingManager.createCategory("J-B-A", "runes", "baserunes");
        ItemCategory.craftingManager.createCategory("J-c-A", "runes", "modifierrunes");
    }
}
