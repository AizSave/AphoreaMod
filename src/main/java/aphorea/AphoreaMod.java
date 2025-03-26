package aphorea;

import aphorea.biomes.InfectedFieldsBiome;
import aphorea.biomes.levels.InfectedFieldsFieldsCaveLevel;
import aphorea.biomes.levels.InfectedFieldsSurfaceLevel;
import aphorea.buffs.LowdsPoisonBuff;
import aphorea.data.AphSwampLevelData;
import aphorea.data.AphWorldData;
import aphorea.items.weapons.melee.greatsword.logic.GreatswordDashLevelEvent;
import aphorea.items.weapons.melee.saber.logic.SaberDashLevelEvent;
import aphorea.items.weapons.melee.saber.logic.SaberJumpLevelEvent;
import aphorea.journal.AphJournalChallenges;
import aphorea.levelevents.*;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.objects.*;
import aphorea.packets.AphCustomPushPacket;
import aphorea.packets.AphRuneOfUnstableGelSlimePacket;
import aphorea.packets.AphRunesInjectorAbilityPacket;
import aphorea.packets.AphSingleAreaShowPacket;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import aphorea.registry.*;
import aphorea.tiles.GelTile;
import aphorea.tiles.InfectedGrassTile;
import aphorea.tiles.InfectedWaterTile;
import aphorea.utils.AphColors;
import necesse.engine.journal.JournalEntry;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.entity.mobs.hostile.*;
import necesse.entity.mobs.hostile.bosses.*;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.gfx.GameColor;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.presets.DeepCaveChestLootTable;
import necesse.inventory.lootTable.presets.DeepCaveRuinsLootTable;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.gameObject.furniture.StorageBoxInventoryObject;
import necesse.level.gameTile.CrystalGravelTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.dungeon.DungeonBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;

@ModEntry
public class AphoreaMod {

    // Load modifiers
    AphModifiers aphModifiers = new AphModifiers();

    public static Biome INFECTED_FIELDS;

    public void init() throws Exception {
        System.out.println("AphoreaMod starting...");

        // Enchantments
        AphEnchantments.registerCore();

        // Journal Challenges
        AphJournalChallenges.registerCore();

        // Damage Types
        AphDamageType.registerCore();

        // Containers
        AphContainers.registerCore();

        // Item Category
        ItemCategory.createCategory("A-A-E", "equipment", "tools", "healing");
        ItemCategory.createCategory("A-F-A", "misc", "runes");
        ItemCategory.createCategory("A-F-A", "misc", "runes", "baserunes");
        ItemCategory.createCategory("A-F-B", "misc", "runes", "modifierrunes");

        ItemCategory.equipmentManager.createCategory("C-A-A", "tools");
        ItemCategory.equipmentManager.createCategory("C-B-A", "tools", "healingtools");

        ItemCategory.craftingManager.createCategory("D-B-F", "equipment", "tools", "healingtools");
        ItemCategory.craftingManager.createCategory("J-A-A", "runes");
        ItemCategory.craftingManager.createCategory("J-A-A", "runes", "runesinjectors");
        ItemCategory.craftingManager.createCategory("J-B-A", "runes", "baserunes");
        ItemCategory.craftingManager.createCategory("J-c-A", "runes", "modifierrunes");

        // Data
        WorldDataRegistry.registerWorldData("aphoreaworlddata", AphWorldData.class);
        LevelDataRegistry.registerLevelData("aphoreaswampleveldata", AphSwampLevelData.class);

        // Tiles
        TileRegistry.registerTile("geltile", new GelTile("geltile", AphColors.gel), -1.0F, true);
        TileRegistry.registerTile("infectedwatertile", new InfectedWaterTile(), 20.0F, true);
        TileRegistry.registerTile("infectedgrasstile", new InfectedGrassTile(), 0.0F, false);
        TileRegistry.registerTile("spinelgravel", new CrystalGravelTile("spinelgravel", AphColors.spinel_dark), 10.0F, true);

        // Objects
        ObjectRegistry.registerObject("witchstatue", new WitchStatue(), -1.0F, true);
        ObjectRegistry.registerObject("runestable", new RunesTable(), -1.0F, true);
        ObjectRegistry.registerObject("infectedgrass", new InfectedGrassObject(), -1.0F, true);
        ObjectRegistry.registerObject("infectedsapling", new AphSaplingObject("infectedsapling", "infectedtree", 900, 1800, true, 50, 340F, 0.6F, "infectedgrasstile"), 10.0F, true);
        ObjectRegistry.registerObject("infectedtree", new AphTreeObject("infectedtree", "infectedlog", "infectedsapling", AphColors.infected_dark, 45, 60, 110, "infectedleaves", 100, 340F, 0.6F), 0.0F, false);
        SpinelClusterObject.registerCrystalCluster("spinelcluster", "spinelcluster", AphColors.spinel, 337.0F, -1.0F, true);
        SpinelClusterObject.registerCrystalCluster("spinelclusterpure", "spinelclusterpure", AphColors.spinel, 337.0F, -1.0F, true);
        ObjectRegistry.registerObject("spinelclustersmall", new SpinelClusterSmallObject("spinelcluster_small", AphColors.spinel, 337.0F), -1.0F, true);
        ObjectRegistry.registerObject("fakespinelchest", new FakeSpinelChest(), -1.0F, true);
        ObjectRegistry.registerObject("spinelchest", new StorageBoxInventoryObject("spinelchest", 40, AphColors.spinel), 20.0F, true);

        RockObject gelRock;
        ObjectRegistry.registerObject("gelrock", gelRock = new RockyWallObject("gelrock", AphColors.rock, "rockygel", 0, 1, 1), -1.0F, true);
        gelRock.toolTier = 1.5F;
        SingleRockObject.registerSurfaceRock(gelRock, "surfacegelrock", AphColors.rock_light, 1, 2, 1, -1.0F, true);

        // Recipe Tech
        AphTech.registerCore();

        // Items
        AphItems.registerCore();

        // Global Ingredients
        ItemRegistry.getItem("magicfoci").addGlobalIngredient("anybasicfoci");
        ItemRegistry.getItem("meleefoci").addGlobalIngredient("anybasicfoci");
        ItemRegistry.getItem("rangefoci").addGlobalIngredient("anybasicfoci");
        ItemRegistry.getItem("summonfoci").addGlobalIngredient("anybasicfoci");

        // Mobs
        AphMobs.registerCore();

        // Projectiles
        AphProjectiles.registerCore();

        // Buffs
        AphBuffs.registerCore();

        // LevelEvents
        LevelEventRegistry.registerEvent("gelprojectilegroundeffect", GelProjectile.GelProjectileGroundEffectEvent.class);
        LevelEventRegistry.registerEvent("saberdash", SaberDashLevelEvent.class);
        LevelEventRegistry.registerEvent("saberjump", SaberJumpLevelEvent.class);
        LevelEventRegistry.registerEvent("greatsworddash", GreatswordDashLevelEvent.class);
        LevelEventRegistry.registerEvent("volatilegelexplosion", VolatileGelSlime.VolatileGelExplosion.class);
        LevelEventRegistry.registerEvent("spambulletexplosion", SpamBulletProjectile.SpamBulletExplosion.class);
        LevelEventRegistry.registerEvent("firepoolgroundeffect", SpamBulletProjectile.FirePoolGroundEffectEvent.class);

        // Base Runes LevelEvents
        LevelEventRegistry.registerEvent("runeofdetonationevent", AphRuneOfDetonationEvent.class);
        LevelEventRegistry.registerEvent("runeofthunderevent", AphRuneOfThunderEvent.class);
        LevelEventRegistry.registerEvent("runeofqueenspiderevent", AphRuneOfQueenSpiderEvent.class);
        LevelEventRegistry.registerEvent("runeofcryoqueenevent", AphRuneOfCryoQueenEvent.class);
        LevelEventRegistry.registerEvent("runeofpestwardenevent", AphRuneOfPestWardenEvent.class);
        LevelEventRegistry.registerEvent("runeofmotherslimeevent", AphRuneOfMotherSlimeEvent.class);
        LevelEventRegistry.registerEvent("runeofsunlightchampionevent", AphRuneOfSunlightChampionEvent.class);
        LevelEventRegistry.registerEvent("runeofsunlightchampionexplosionevent", AphRuneOfSunlightChampionExplosionEvent.class);
        LevelEventRegistry.registerEvent("runeofcrystaldragonevent", AphRuneOfCrystalDragonEvent.class);

        // Modifier Runes LevelEvents
        LevelEventRegistry.registerEvent("abysmalruneevent", AphAbysmalRuneEvent.class);
        LevelEventRegistry.registerEvent("tildalruneevent", AphTidalRuneEvent.class);

        // Packets
        PacketRegistry.registerPacket(AphCustomPushPacket.class);
        PacketRegistry.registerPacket(AphRunesInjectorAbilityPacket.class);
        PacketRegistry.registerPacket(AphRuneOfUnstableGelSlimePacket.class);

        // Client only Packets
        PacketRegistry.registerPacket(AphSingleAreaShowPacket.class);
        PacketRegistry.registerPacket(LowdsPoisonBuff.LowdsPoisonBuffPacket.class);
        PacketRegistry.registerPacket(WildPhosphorSlime.PhosphorSlimeParticlesPacket.class);

        // Controls
        AphControls.registerCore();

        // Biomes
        BiomeRegistry.registerBiome("infectedfields", INFECTED_FIELDS = new InfectedFieldsBiome(), 200, null);

        // Levels
        LevelRegistry.registerLevel("infectedfieldssurface", InfectedFieldsSurfaceLevel.class);
        LevelRegistry.registerLevel("infectedfieldscave", InfectedFieldsFieldsCaveLevel.class);


        // Journal [Surface]

        JournalEntry forestSurfaceJournal = JournalRegistry.getJournalEntry("forestsurface");
        forestSurfaceJournal.addEntryChallenges(AphJournalChallenges.APH_FOREST_SURFACE_CHALLENGES_ID);
        forestSurfaceJournal.addMobEntries("unstablegelslime");

        JournalEntry swampSurfaceJournal = JournalRegistry.getJournalEntry("swampsurface");
        swampSurfaceJournal.addMobEntries("pinkwitch");

        // Journal [Cave]

        JournalEntry forestCaveJournal = JournalRegistry.getJournalEntry("forestcave");
        forestCaveJournal.addMobEntries("rockygelslime");
        forestCaveJournal.addTreasureEntry(new LootTable(new LootItem("blowgun"), new LootItem("sling")));

        JournalEntry snowCaveJournal = JournalRegistry.getJournalEntry("snowcave");
        snowCaveJournal.addTreasureEntry(new LootTable(new LootItem("frozenperiapt")));

        // Journal [Other]

        JournalEntry dungeonJournal = JournalRegistry.getJournalEntry("dungeon");
        dungeonJournal.addMobEntries("voidadept");
        dungeonJournal.addTreasureEntry(new LootTable(new LootItem("heartring")));

        // Journal [Bulk]
        JournalRegistry.getJournalEntries().forEach(journalEntry -> {
            if (journalEntry.levelType == JournalRegistry.LevelType.SURFACE) {
                journalEntry.addMobEntries("gelslime", "wildphosphorslime");
                journalEntry.addTreasureEntry("blowgun", "sling");
            }
            if (journalEntry.mobsData.stream().anyMatch(m -> Objects.equals(m.mob.getStringID(), "goblin"))) {
                journalEntry.addMobEntries("copperdaggergoblin", "irondaggergoblin", "golddaggergoblin");
            }
        });

        // Journal [Infected Fields]

        JournalEntry infectedFieldsSurface = new JournalEntry(AphoreaMod.INFECTED_FIELDS, JournalRegistry.LevelType.SURFACE);
        infectedFieldsSurface.addMobEntries("rockygelslime", "infectedtreant");
        infectedFieldsSurface.addTreasureEntry(AphLootTables.infectedFieldsSurface);
        infectedFieldsSurface.addEntryChallenges(AphJournalChallenges.INFECTED_SURFACE_CHALLENGES_ID);
        JournalRegistry.registerJournalEntry("infectedfieldssurface", infectedFieldsSurface);

        JournalEntry infectedFieldsCave = new JournalEntry(AphoreaMod.INFECTED_FIELDS, JournalRegistry.LevelType.CAVE);
        infectedFieldsCave.addMobEntries("rockygelslime", "spinelcaveling", "spinelgolem", "spinelmimic");
        infectedFieldsCave.addTreasureEntry(AphLootTables.infectedLootLake);
        infectedFieldsCave.addEntryChallenges(AphJournalChallenges.INFECTED_CAVE_CHALLENGES_ID);
        JournalRegistry.registerJournalEntry("infectedfieldscave", infectedFieldsCave);


        System.out.println("AphoreaMod started");
    }

    public void initResources() {
        AphResources.initResources();
    }

    public void postInit() {

        // Recipes
        AphRecipes.initRecipes();

        // Spawn tables

        Biome.defaultSurfaceMobs
                .addLimited(40, "gelslime", 2, 32 * 32)
                .addLimited(2, "wildphosphorslime", 1, 16 * 32, mob -> mob.isHostile);

        Biome.forestCaveMobs
                .add(10, "rockygelslime");

        SwampBiome.surfaceMobs
                .addLimited(1, "pinkwitch", 1, 1024 * 32);

        DungeonBiome.defaultDungeonMobs
                .add(5, "voidadept");

        // LootTables

        LootTablePresets.startChest.items.addAll(
                new LootItemList(
                        new LootItem("sling", 1),
                        new LootItem("basicbackpack", 1),
                        new LootItem("rusticrunesinjector", 1)
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

        SpiderEmpressMob.privateLootTable.items.add(
                new LootItem("runeofspiderempress")
        );

        try {
            Supplier<Color> newColor = () -> new Color(100, 250, 250);

            Field description = GameColor.class.getDeclaredField("color");
            description.setAccessible(true);
            description.set(GameColor.ITEM_NORMAL, newColor);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
