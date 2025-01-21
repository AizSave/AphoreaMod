package aphorea.journal;

import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.MultiJournalChallenge;
import necesse.engine.journal.PickupItemsJournalChallenge;
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

    public static void registerCore() {
        KILL_GEL_SLIMES_ID = registerChallenge("killgelslimesforest", new KillGelSlimesSurfaceForestJournalChallenge());
        PICKUP_FLORAL_RING_ID = registerChallenge("pickupfloralring", new PickupItemsJournalChallenge(1, true, "floralring"));
        KILL_UNSTABLE_GEL_SLIME_ID = registerChallenge("killunstablegelslime", new KillUnstableGelSlimeJournalChallenge());
        APH_FOREST_SURFACE_CHALLENGES_ID = registerChallenge("aphoreaforestsurface", (new MultiJournalChallenge(KILL_GEL_SLIMES_ID, PICKUP_FLORAL_RING_ID, KILL_UNSTABLE_GEL_SLIME_ID)).setReward(APH_FOREST_SURFACE_REWARD));
    }

    public static int registerChallenge(String stringID, JournalChallenge journalChallenge) {
        return JournalChallengeRegistry.registerChallenge(stringID, journalChallenge);
    }
}
