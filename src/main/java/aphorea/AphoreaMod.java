package aphorea;

import aphorea.journal.AphJournalChallenges;
import aphorea.registry.*;
import aphorea.utils.AphColors;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.gfx.GameColor;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.function.Supplier;

@ModEntry
public class AphoreaMod {

    static {
        new AphModifiers();
    }

    public void init() throws Exception {
        System.out.println("AphoreaMod starting...");

        // Data
        AphData.registerCore();

        // LevelEvents
        AphLevelEvents.registerCore();

        // Packets
        AphPackets.registerCore();

        // Controls
        AphControls.registerCore();

        // Containers
        AphContainers.registerCore();

        // Item Category
        AphItemCategories.registerCore();

        // Enchantments
        AphEnchantments.registerCore();

        // Damage Types
        AphDamageType.registerCore();

        // Items
        AphItems.registerCore();

        // Global Ingredients
        AphGlobalIngredients.registerCore();

        // Tiles
        AphTiles.registerCore();

        // Objects
        AphObjects.registerCore();

        // Recipe Tech
        AphTech.registerCore();

        // Mobs
        AphMobs.registerCore();

        // Projectiles
        AphProjectiles.registerCore();

        // Buffs
        AphBuffs.registerCore();

        // Biomes
        AphBiomes.registerCore();

        // World Presets
        AphWorldPresets.registerCore();

        // Levels
        AphLevels.registerCore();

        // Journal Challenges
        AphJournalChallenges.registerCore();

        // Journal
        AphJournal.registerCore();

        System.out.println("AphoreaMod started");
    }

    public void initResources() {
        AphResources.initResources();
    }

    public void postInit() {
        // Recipes
        AphRecipes.initRecipes();

        // Spawn mob tables
        AphSpawnTables.modifySpawnTables();

        // Loot tables
        AphLootTables.modifyLootTables();

        try {
            Supplier<Color> newColor = () -> AphColors.normal_rarity;

            Field description = GameColor.class.getDeclaredField("color");
            description.setAccessible(true);
            description.set(GameColor.ITEM_NORMAL, newColor);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
