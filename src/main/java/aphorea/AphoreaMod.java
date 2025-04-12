package aphorea;

import aphorea.journal.AphJournalChallenges;
import aphorea.registry.*;
import aphorea.utils.AphColors;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.util.GameUtils;
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

        // Version Migration
        VersionMigration.oldItemStringIDs = GameUtils.concat(VersionMigration.oldItemStringIDs, new String[][]{
                {"inspirationfoci", "bannerbearerfoci"},
                {"venomextract", "lowdspotion"}
        });


        // Enchantments
        AphEnchantments.registerCore();

        // Journal Challenges
        AphJournalChallenges.registerCore();

        // Damage Types
        AphDamageType.registerCore();

        // Containers
        AphContainers.registerCore();

        // Item Category
        AphItemCategories.registerCore();

        // Data
        AphData.registerCore();

        // Tiles
        AphTiles.registerCore();

        // Objects
        AphObjects.registerCore();

        // Recipe Tech
        AphTech.registerCore();

        // Items
        AphItems.registerCore();

        // Global Ingredients
        AphGlobalIngredients.registerCore();

        // Mobs
        AphMobs.registerCore();

        // Projectiles
        AphProjectiles.registerCore();

        // Buffs
        AphBuffs.registerCore();

        // LevelEvents
        AphLevelEvents.registerCore();

        // Packets
        AphPackets.registerCore();

        // Controls
        AphControls.registerCore();

        // Biomes
        AphBiomes.registerCore();

        // Levels
        AphLevels.registerCore();

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

        // Spawn tables
        AphSpawnTables.modifySpawnTables();

        // LootTables
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
