package aphorea;

import aphorea.buffs.LowdsPoisonBuff;
import aphorea.buffs.Trinkets.Healing.WitchMedallionBuff;
import aphorea.items.banners.BlankBannerItem;
import aphorea.items.banners.StrikeBannerItem;
import aphorea.items.healingtools.HealingStaff;
import aphorea.items.weapons.magic.AdeptsBook;
import aphorea.items.weapons.magic.MagicalBroom;
import aphorea.items.weapons.magic.UnstableGelStaff;
import aphorea.mobs.bosses.MiniUnstableGelSlime;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.GelSlime;
import aphorea.mobs.hostile.PinkWitch;
import aphorea.mobs.hostile.RockyGelSlime;
import aphorea.mobs.hostile.VoidAdept;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.UndeadSkeleton;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.objects.WitchStatue;
import aphorea.other.buffs.trinkets.AphBaseRuneTrinketBuff;
import aphorea.other.data.AphSwampLevelData;
import aphorea.other.data.AphWorldData;
import aphorea.other.itemtype.weapons.AphSaberToolItem;
import aphorea.other.journal.AphJournalChallenges;
import aphorea.packets.AphCustomPushPacket;
import aphorea.packets.AphRunesInjectorAbilityPacket;
import aphorea.projectiles.arrow.GelArrowProjectile;
import aphorea.projectiles.arrow.UnstableGelArrowProjectile;
import aphorea.projectiles.mob.PinkWitchProjectile;
import aphorea.projectiles.toolitem.*;
import aphorea.registry.*;
import aphorea.tiles.GelTile;
import necesse.engine.GlobalData;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputID;
import necesse.engine.journal.JournalEntry;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.client.Client;
import necesse.engine.registries.*;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.dungeon.DungeonBiome;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;

import java.awt.*;

@ModEntry
public class AphoreaMod {

    // Load modifiers
    AphModifiers aphModifiers = new AphModifiers();

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
        ItemCategory.equipmentManager.createCategory("C-A-A", "tools");
        ItemCategory.equipmentManager.createCategory("C-B-A", "tools", "healingtools");
        ItemCategory.craftingManager.createCategory("D-B-F", "equipment", "tools", "healingtools");

        // Data
        WorldDataRegistry.registerWorldData("aphoreaworlddata", AphWorldData.class);
        LevelDataRegistry.registerLevelData("aphoreaswampleveldata", AphSwampLevelData.class);

        // Tiles
        TileRegistry.registerTile("geltile", new GelTile("geltile", new Color(20, 80, 255)), -1.0F, true);

        // Objects
        ObjectRegistry.registerObject("witchstatue", new WitchStatue(), -1.0F, true);

        // Items
        AphItems.registerCore();

        // Mobs
        MobRegistry.registerMob("gelslime", GelSlime.class, true);
        MobRegistry.registerMob("rockygelslime", RockyGelSlime.class, true);
        MobRegistry.registerMob("witch", PinkWitch.class, true);
        MobRegistry.registerMob("voidadept", VoidAdept.class, true);
        MobRegistry.registerMob("wildphosphorslime", WildPhosphorSlime.class, true);

        // Bosses [Mobs]
        MobRegistry.registerMob("unstablegelslime", UnstableGelSlime.class, true);
        MobRegistry.registerMob("miniunstablegelslime", MiniUnstableGelSlime.class, true);

        // Summon [Mobs]
        MobRegistry.registerMob("babyunstablegelslime", BabyUnstableGelSlime.class, false);
        MobRegistry.registerMob("volatilegelslime", VolatileGelSlime.class, false);
        MobRegistry.registerMob("undeadskeleton", UndeadSkeleton.class, false);

        // Pets [Mobs]
        MobRegistry.registerMob("petphosphorslime", PetPhosphorSlime.class, false);

        // Projectiles
        ProjectileRegistry.registerProjectile("gel", GelProjectile.class, "gel", "ball_shadow");
        ProjectileRegistry.registerProjectile("unstablegel", UnstableGelProjectile.class, "unstablegel", "ball_shadow");
        ProjectileRegistry.registerProjectile("copperaircut", AircutProjectile.CopperAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("ironaircut", AircutProjectile.IronAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("goldaircut", AircutProjectile.GoldAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("demonicaircut", AircutProjectile.DemonicAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("unstablegelaircut", AircutProjectile.UnstableGelAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("slingstone", SlingStoneProjectile.class, "slingstone", "ball_shadow");
        ProjectileRegistry.registerProjectile("slingfirestone", FireSlingStoneProjectile.class, "slingfirestone", "ball_shadow");
        ProjectileRegistry.registerProjectile("slingfrozenstone", FrozenSlingStoneProjectile.class, "slingfrozenstone", "ball_shadow");
        ProjectileRegistry.registerProjectile("unstablegelveline", UnstableGelvelineProjectile.class, "unstablegelveline", "unstablegelveline_shadow");
        ProjectileRegistry.registerProjectile("gelarrow", GelArrowProjectile.class, "gelarrow", "gelarrow_shadow");
        ProjectileRegistry.registerProjectile("unstablegelarrow", UnstableGelArrowProjectile.class, "unstablegelarrow", "unstablegelarrow_shadow");
        ProjectileRegistry.registerProjectile("voidstone", VoidStoneProjectile.class, "voidstone", "voidstone_shadow");
        ProjectileRegistry.registerProjectile("woodenwand", WoodenWandProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("goldenwand", GoldenWandProjectile.class, "none", "none");

        // Projectiles [Mobs]
        ProjectileRegistry.registerProjectile("witch", PinkWitchProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("miniunstablegelslime", MiniUnstableGelSlimeProjectile.class, "miniunstablegelslime", "none");

        // Buffs
        AphBuffs.registerCore();

        // LevelEvent
        LevelEventRegistry.registerEvent("saberdashlevelevent", AphSaberToolItem.SaberDashLevelEvent.class);

        // Packets
        PacketRegistry.registerPacket(AphCustomPushPacket.class);
        PacketRegistry.registerPacket(LowdsPoisonBuff.LowdsPoisonBuffPacket.class);
        PacketRegistry.registerPacket(HealingStaff.HealingStaffAreaParticlesPacket.class);
        PacketRegistry.registerPacket(AdeptsBook.AdeptsBookAreaParticlesPacket.class);
        PacketRegistry.registerPacket(WitchMedallionBuff.WitchMedallionAreaParticlesPacket.class);
        PacketRegistry.registerPacket(UnstableGelStaff.UnstableGelStaffAreaParticlesPacket.class);
        PacketRegistry.registerPacket(BlankBannerItem.BlankBannerAreaParticlesPacket.class);
        PacketRegistry.registerPacket(StrikeBannerItem.StrikeBannerAreaParticlesPacket.class);
        PacketRegistry.registerPacket(WildPhosphorSlime.PhosphorSlimeParticlesPacket.class);
        PacketRegistry.registerPacket(AphRunesInjectorAbilityPacket.class);

        // Events
        LevelEventRegistry.registerEvent("gelprojectilegroundeffect", GelProjectile.GelProjectileGroundEffectEvent.class);

        // Controls
        Control.addModControl(new Control(InputID.KEY_G, "runesinjectorability") {
            @Override
            public void activate(InputEvent event) {
                super.activate(event);
                if (isPressed()) {
                    Client client = ((MainGame) GlobalData.getCurrentState()).getClient();
                    if (client != null) {
                        PlayerMob playerMob = client.getPlayer();
                        if (playerMob != null) {
                            playerMob.buffManager.getBuffs().values().stream().filter(b -> b.buff instanceof AphBaseRuneTrinketBuff).map(b -> (AphBaseRuneTrinketBuff) b.buff).findFirst().ifPresent(
                                    runeBuff -> client.network.sendPacket(new AphRunesInjectorAbilityPacket(client.getSlot(), runeBuff))
                            );
                        }
                    }
                }
            }
        });


        // Journal [Surface]

        JournalEntry forestSurfaceJournal = JournalRegistry.getJournalEntry("forestsurface");
        forestSurfaceJournal.addMobEntries("gelslime", "wildphosphorslime", "unstablegelslime");
        forestSurfaceJournal.addEntryChallenges(AphJournalChallenges.APH_FOREST_SURFACE_CHALLENGES_ID);

        JournalEntry swampSurfaceJournal = JournalRegistry.getJournalEntry("swampsurface");
        swampSurfaceJournal.addMobEntries("witch");

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
                journalEntry.addTreasureEntry(new LootTable(new LootItem("blowgun"), new LootItem("sling")));
            }
        });

        System.out.println("AphoreaMod started");
    }

    public void initResources() {

        // MOBS
        GelSlime.texture = GameTexture.fromFile("mobs/gelslime");
        RockyGelSlime.texture = GameTexture.fromFile("mobs/rockygelslime");
        PinkWitch.texture = GameTexture.fromFile("mobs/witch");
        UnstableGelSlime.texture = GameTexture.fromFile("mobs/unstablegelslime");
        UnstableGelSlime.icon = GameTexture.fromFile("mobs/icons/unstablegelslime");
        MiniUnstableGelSlime.texture = GameTexture.fromFile("mobs/miniunstablegelslime");
        VoidAdept.texture = MobRegistry.Textures.humanTexture("voidadept");
        WildPhosphorSlime.texture = GameTexture.fromFile("mobs/phosphorslime");
        WildPhosphorSlime.texture_scared = GameTexture.fromFile("mobs/phosphorslime_scared");
        PetPhosphorSlime.texture = GameTexture.fromFile("mobs/phosphorslime");
        PetPhosphorSlime.texture_scared = GameTexture.fromFile("mobs/phosphorslime_scared");

        // MOBS [SUMMON]
        BabyUnstableGelSlime.texture = GameTexture.fromFile("mobs/babyunstablegelslime");
        VolatileGelSlime.texture = GameTexture.fromFile("mobs/volatilegelslime");

        // ITEMS
        MagicalBroom.worldTexture = GameTexture.fromFile("worlditems/magicalbroom");

        // PROJECTILES
        FireSlingStoneProjectile.texture_2 = GameTexture.fromFile("projectiles/slingfirestone_2");
        AircutProjectile.CopperAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutcopper");
        AircutProjectile.IronAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutiron");
        AircutProjectile.GoldAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutgold");
        AircutProjectile.UnstableGelAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutunstablegel");
        AircutProjectile.DemonicAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutdemonic");

        // PARTICLES
        GelProjectile.GelProjectileParticle.texture = GameTexture.fromFile("particles/gelprojectile");
    }

    public void postInit() {

        // Recipes
        AphRecipes.initRecipes();

        // Spawn tables

        ForestBiome.defaultSurfaceMobs
                .addLimited(40, "gelslime", 2, 32 * 32)
                .addLimited(2, "wildphosphorslime", 1, 16 * 32, mob -> mob.isHostile);

        Biome.forestCaveMobs
                .add(30, "rockygelslime");

        SwampBiome.surfaceMobs
                .addLimited(1, "witch", 1, 1024 * 32);

        DungeonBiome.defaultDungeonMobs
                .add(5, "voidadept");

        // LootTables

        LootTablePresets.startChest.items.addAll(
                new LootItemList(
                        new LootItem("sling", 1),
                        new LootItem("basicbackpack", 1)
                )
        );

        LootTablePresets.caveCryptCoffin.items.add(
                ChanceLootItem.between(0.1f, "bloodyperiapt", 1, 1)
        );

        LootTablePresets.snowCaveChest.items.add(
                ChanceLootItem.between(0.05f, "frozenperiapt", 1, 1)
        );

        LootTablePresets.surfaceRuinsChest.items.addAll(
                new LootItemList(
                        ChanceLootItem.between(0.05f, "blowgun", 1, 1),
                        ChanceLootItem.between(0.05f, "sling", 1, 1)
                )
        );

        LootTablePresets.basicCaveChest.items.addAll(
                new LootItemList(
                        ChanceLootItem.between(0.05f, "blowgun", 1, 1),
                        ChanceLootItem.between(0.05f, "sling", 1, 1)
                )
        );

        LootTablePresets.hunterChest.items.addAll(
                new LootItemList(
                        ChanceLootItem.between(0.05f, "blowgun", 1, 1),
                        ChanceLootItem.between(0.05f, "sling", 1, 1)
                )
        );

        LootTablePresets.dungeonChest.items.addAll(
                new LootItemList(
                        ChanceLootItem.between(0.05f, "heartring", 1, 1)
                )
        );
    }

}
