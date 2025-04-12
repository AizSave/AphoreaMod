package aphorea.registry;

import aphorea.journal.AphJournalChallenges;
import necesse.engine.journal.JournalEntry;
import necesse.engine.registries.JournalRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

import java.util.Objects;

public class AphJournal {
    public static void registerCore() {
        // Vanilla
        vanillaSurface();
        vanillaCaves();
        vanillaOther();

        // Vanilla Bulk
        JournalRegistry.getJournalEntries().forEach(journalEntry -> {
            vanillaBulkAll(journalEntry);
            if (journalEntry.levelType == JournalRegistry.LevelType.SURFACE) {
                vanillaBulkSurface(journalEntry);
            }
        });

        // New Levels
        infectedFields();

    }

    public static void vanillaSurface() {
        JournalEntry forestSurfaceJournal = JournalRegistry.getJournalEntry("forestsurface");
        forestSurfaceJournal.addEntryChallenges(AphJournalChallenges.APH_FOREST_SURFACE_CHALLENGES_ID);
        forestSurfaceJournal.addMobEntries("unstablegelslime");

        JournalEntry swampSurfaceJournal = JournalRegistry.getJournalEntry("swampsurface");
        swampSurfaceJournal.addMobEntries("pinkwitch");
    }

    public static void vanillaCaves() {
        JournalEntry forestCaveJournal = JournalRegistry.getJournalEntry("forestcave");
        forestCaveJournal.addMobEntries("rockygelslime");
        forestCaveJournal.addTreasureEntry(new LootTable(new LootItem("blowgun"), new LootItem("sling")));

        JournalEntry snowCaveJournal = JournalRegistry.getJournalEntry("snowcave");
        snowCaveJournal.addTreasureEntry(new LootTable(new LootItem("frozenperiapt")));
    }

    public static void vanillaOther() {
        JournalEntry dungeonJournal = JournalRegistry.getJournalEntry("dungeon");
        dungeonJournal.addMobEntries("voidadept");
        dungeonJournal.addTreasureEntry(new LootTable(new LootItem("heartring")));
    }

    public static void vanillaBulkAll(JournalEntry journalEntry) {
        if (journalEntry.mobsData.stream().anyMatch(m -> Objects.equals(m.mob.getStringID(), "goblin"))) {
            journalEntry.addMobEntries("copperdaggergoblin", "irondaggergoblin", "golddaggergoblin");
        }
    }

    public static void vanillaBulkSurface(JournalEntry journalEntry) {
        journalEntry.addMobEntries("gelslime", "wildphosphorslime");
        journalEntry.addTreasureEntry("blowgun", "sling");
    }

    public static void infectedFields() {
        // Surface
        JournalEntry infectedFieldsSurface = new JournalEntry(AphBiomes.INFECTED_FIELDS, JournalRegistry.LevelType.SURFACE);
        infectedFieldsSurface.addMobEntries("rockygelslime", "infectedtreant");
        infectedFieldsSurface.addTreasureEntry(AphLootTables.infectedFieldsSurface);
        infectedFieldsSurface.addEntryChallenges(AphJournalChallenges.INFECTED_SURFACE_CHALLENGES_ID);
        JournalRegistry.registerJournalEntry("infectedfieldssurface", infectedFieldsSurface);

        // Cave
        JournalEntry infectedFieldsCave = new JournalEntry(AphBiomes.INFECTED_FIELDS, JournalRegistry.LevelType.CAVE);
        infectedFieldsCave.addMobEntries("rockygelslime", "spinelcaveling", "spinelgolem", "spinelmimic");
        infectedFieldsCave.addTreasureEntry(AphLootTables.infectedLootLake, AphLootTables.infectedCaveForest);
        infectedFieldsCave.addEntryChallenges(AphJournalChallenges.INFECTED_CAVE_CHALLENGES_ID);
        JournalRegistry.registerJournalEntry("infectedfieldscave", infectedFieldsCave);
    }
}
