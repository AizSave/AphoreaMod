package aphorea.registry;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.mobs.hostile.*;
import necesse.entity.mobs.hostile.bosses.*;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CaveChestLootTable;
import necesse.inventory.lootTable.presets.DeepCaveChestLootTable;
import necesse.inventory.lootTable.presets.DeepCaveRuinsLootTable;

public class AphLootTables {
    public static LootTable runeInventorHouse = new LootTable();
    public static LootTable infectedFieldsSurface = new LootTable();
    public static LootTable infectedLootLake = new LootTable();
    public static LootTable infectedCaveForest = new LootTable();
    public static LootTable infectedCaveVariousTreasures = new LootTable();
    public static LootTable infectedCaveForestVariousTreasures = new LootTable();

    public static final RotationLootItem basicChestAllMainItems = RotationLootItem.presetRotation(
            new LootItem("zephyrcharm"), new LootItem("shinebelt"), new LootItem("heavyhammer"), new LootItem("noblehorseshoe"),
            new LootItem("ancientfeather"), new LootItem("miningcharm"), new LootItem("cactusshield"), new LootItem("airvessel"), new LootItem("prophecyslab"),
            new LootItem("swamptome"), new LootItem("slimecanister"), new LootItem("stinkflask"), new LootItem("vambrace"), new LootItem("overgrownfishingrod"),
            new LootItem("frozenwave"), new LootItem("calmingrose"), new LootItem("frozenheart"), new LootItem("sparegemstones"), new LootItem("magicbranch"),
            new LootItem("blowgun"), new LootItem("sling")
    );

    static {
        runeInventorHouse.items.addAll(
                new LootItemList(
                        new LootItem("runestutorialbook", 1),
                        new LootItem("rusticrunesinjector", 1),
                        new LootItem("ironbar", 2)
                )
        );

        infectedFieldsSurface.items.addAll(
                new LootItemList(
                        basicChestAllMainItems,
                        CaveChestLootTable.potions,
                        CaveChestLootTable.bars,
                        CaveChestLootTable.extraItems
                )
        );

        infectedLootLake.items.addAll(
                new LootItemList(
                        new LootItem("thespammer"),
                        LootItem.between("spambullet", 90, 110),
                        LootItem.between("spinel", 4, 6),
                        LootItem.between("lifespinel", 1, 2),
                        basicChestAllMainItems,
                        CaveChestLootTable.potions,
                        CaveChestLootTable.extraItems
                )
        );

        infectedCaveForest.items.addAll(
                new LootItemList(
                        LootItem.between("infectedalloy", 3, 4),
                        LootItem.between("spinel", 2, 3),
                        LootItem.between("lifespinel", 0, 1),
                        CaveChestLootTable.potions,
                        CaveChestLootTable.extraItems
                )
        );

        infectedCaveVariousTreasures.items.addAll(
                new LootItemList(
                        RotationLootItem.globalLootRotation(
                                new LootItem("lightsaber"),
                                new LootItem("shotgunsaber"),
                                new LootItem("ninjascarf"),
                                new LootItem("adrenalinecharm"),
                                new LootItem("cursedmedallion")
                        )
                )
        );

        infectedCaveForestVariousTreasures.items.addAll(
                new LootItemList(
                        RotationLootItem.globalLootRotation(
                                new LootItem("brokenkora")
                        )
                )
        );
    }

    public static void modifyLootTables() {
        LootTablePresets.startChest.items.addAll(
                new LootItemList(
                        new LootItem("sling", 1),
                        new LootItem("basicbackpack", 1)
                )
        );

        LootTablePresets.caveCryptCoffin.items.add(
                new LootItemList(
                        new ChanceLootItem(0.1f, "bloodyperiapt"),
                        new ChanceLootItem(0.05f, "onyxrune")
                )
        );

        LootTablePresets.snowCaveChest.items.add(
                new ChanceLootItem(0.05f, "frozenperiapt")
        );

        LootTablePresets.surfaceRuinsChest.items.addAll(
                new LootItemList(
                        new ChanceLootItem(0.05f, "blowgun"),
                        new ChanceLootItem(0.05f, "sling")
                )
        );

        LootTablePresets.basicCaveChest.items.addAll(
                new LootItemList(
                        new ChanceLootItem(0.05f, "blowgun"),
                        new ChanceLootItem(0.05f, "sling")
                )
        );

        LootTablePresets.hunterChest.items.addAll(
                new LootItemList(
                        new ChanceLootItem(0.05f, "blowgun"),
                        new ChanceLootItem(0.05f, "sling")
                )
        );

        LootTablePresets.dungeonChest.items.addAll(
                new LootItemList(
                        new ChanceLootItem(0.1f, "runeofthunder"),
                        new ChanceLootItem(0.05f, "heartring")
                )
        );

        LootTablePresets.fishianBarrel.items.add(
                new ChanceLootItem(0.25f, "tidalrune")
        );

        DeepCaveChestLootTable.extraItems.items.add(
                new ChanceLootItem(0.02f, "abyssalrune")
        );

        DeepCaveRuinsLootTable.extraItems.items.add(
                new ChanceLootItem(0.005f, "abyssalrune")
        );

        // Mobs loot

        DeepCaveSpiritMob.lootTable.items.add(
                new ChanceLootItem(0.05F, "runeofshadows")
        );

        FishianHookWarriorMob.lootTable.items.add(
                new ChanceLootItem(0.01F, "tidalrune")
        );

        FishianHealerMob.lootTable.items.add(
                new ChanceLootItem(0.01F, "tidalrune")
        );

        FishianShamanMob.lootTable.items.add(
                new ChanceLootItem(0.01F, "tidalrune")
        );

        TrenchcoatGoblinHelmetMob.lootTable = new LootTable(
                GoblinMob.lootTable,
                new ChanceLootItem(0.4F, "frenzyrune")
        );

        VampireMob.lootTable.items.add(
                new ChanceLootItem(0.01f, "onyxrune")
        );

        // Bosses loot

        EvilsProtectorMob.privateLootTable.items.add(
                new LootItem("runeofevilsprotector")
        );

        QueenSpiderMob.privateLootTable.items.add(
                new LootItem("runeofqueenspider")
        );

        VoidWizard.privateLootTable.items.add(
                new LootItem("runeofvoidwizard")
        );

        SwampGuardianHead.privateLootTable.items.add(
                new LootItem("runeofswampguardian")
        );

        AncientVultureMob.privateLootTable.items.add(
                new LootItem("runeofancientvulture")
        );

        PirateCaptainMob.privateLootTable.items.add(
                new LootItem("runeofpiratecaptain")
        );

        ReaperMob.privateLootTable.items.add(
                new LootItem("runeofreaper")
        );

        CryoQueenMob.privateLootTable.items.add(
                new LootItem("runeofcryoqueen")
        );

        PestWardenHead.privateLootTable.items.add(
                new LootItem("runeofpestwarden")
        );

        FlyingSpiritsHead.privateLootTable.items.add(
                new LootItem("runeofsageandgrit")
        );

        FallenWizardMob.privateLootTable.items.add(
                new LootItem("runeoffallenwizard")
        );

        MotherSlimeMob.privateLootTable.items.add(
                new LootItem("runeofmotherslime")
        );

        NightSwarmLevelEvent.privateLootTable.items.add(
                new LootItem("runeofnightswarm")
        );

        SpiderEmpressMob.privateLootTable.items.add(
                new LootItem("runeofspiderempress")
        );

        SunlightChampionMob.privateLootTable.items.add(
                new LootItem("runeofsunlightchampion")
        );

        MoonlightDancerMob.privateLootTable.items.add(
                new LootItem("runeofmoonlightdancer")
        );

        CrystalDragonHead.lootTable.items.add(
                new LootItem("runeofcrystaldragon")
        );
    }
}