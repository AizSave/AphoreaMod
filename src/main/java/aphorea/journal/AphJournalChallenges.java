package aphorea.journal;

import necesse.engine.journal.*;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class AphJournalChallenges {

    public static LootTable APH_FOREST_SURFACE_REWARD = new LootTable((new LootItemList(new LootItem("sapphirebackpack"))).setCustomListName("item", "sapphirebackpack"));
    public static int APH_FOREST_SURFACE_CHALLENGES_ID;
    public static int KILL_GEL_SLIMES_ID;
    public static int PICKUP_FLORAL_RING_ID;
    public static int KILL_UNSTABLE_GEL_SLIME_ID;

    public static LootTable APH_SWAMP_SURFACE_REWARD = new LootTable((new LootItemList(new LootItem("amethystbackpack"))).setCustomListName("item", "amethystbackpack"));
    public static int APH_SWAMP_SURFACE_CHALLENGES_ID;
    public static int KILL_PINK_WITCHES_ID;

    public static LootTable INFECTED_SURFACE_REWARD = new LootTable((new LootItemList(new LootItem("rubybackpack"))).setCustomListName("item", "rubybackpack"));
    public static int INFECTED_SURFACE_CHALLENGES_ID;
    public static int PICKUP_INFECTED_LOGS_ID;

    public static LootTable INFECTED_CAVE_REWARD = new LootTable((new LootItemList(new LootItem("diamondbackpack"))).setCustomListName("item", "diamondbackpack"));
    public static int INFECTED_CAVE_CHALLENGES_ID;
    public static int PICKUP_SPINEL_ID;
    public static int ACTIVATE_BABYLON_TOWER_ID;
    public static int FIND_THE_SPAMMER_ID;

    public static void registerCore() {
        KILL_GEL_SLIMES_ID = registerChallenge("killgelslimesforest", new KillGelSlimesSurfaceForestJournalChallenge());
        PICKUP_FLORAL_RING_ID = registerChallenge("pickupfloralring", new PickupItemsJournalChallenge(1, true, "floralring"));
        KILL_UNSTABLE_GEL_SLIME_ID = registerChallenge("killunstablegelslime", new KillUnstableGelSlimeJournalChallenge());
        APH_FOREST_SURFACE_CHALLENGES_ID = registerChallenge("aphoreaforestsurface", (new MultiJournalChallenge(KILL_GEL_SLIMES_ID, PICKUP_FLORAL_RING_ID, KILL_UNSTABLE_GEL_SLIME_ID)).setReward(APH_FOREST_SURFACE_REWARD));

        KILL_PINK_WITCHES_ID = registerChallenge("killpinkwitchesswamp", new MobsKilledJournalChallenge(5, "pinkwitch"));
        APH_SWAMP_SURFACE_CHALLENGES_ID = registerChallenge("aphoreaswampsurface", (new MultiJournalChallenge(KILL_PINK_WITCHES_ID)).setReward(APH_SWAMP_SURFACE_REWARD));

        PICKUP_INFECTED_LOGS_ID = registerChallenge("pickupinfectedlogs", new PickupItemsJournalChallenge(100, true, "infectedlog"));
        INFECTED_SURFACE_CHALLENGES_ID = registerChallenge("infectedfieldssurface", (new MultiJournalChallenge(PICKUP_INFECTED_LOGS_ID)).setReward(INFECTED_SURFACE_REWARD));

        PICKUP_SPINEL_ID = registerChallenge("pickupspinel", new PickupItemsJournalChallenge(40, true, "spinel"));
        ACTIVATE_BABYLON_TOWER_ID = registerChallenge("activatebabylontower", new ActivateBabylonTowerJournalChallenge());
        FIND_THE_SPAMMER_ID = registerChallenge("findthespammer", new ItemObtainedJournalChallenge("thespammer"));
        INFECTED_CAVE_CHALLENGES_ID = registerChallenge("infectedfieldscave", (new MultiJournalChallenge(PICKUP_SPINEL_ID, ACTIVATE_BABYLON_TOWER_ID, FIND_THE_SPAMMER_ID)).setReward(INFECTED_CAVE_REWARD));
    }

    public static int registerChallenge(String stringID, JournalChallenge journalChallenge) {
        return JournalChallengeRegistry.registerChallenge(stringID, journalChallenge);
    }
}
