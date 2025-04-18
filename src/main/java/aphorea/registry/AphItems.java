package aphorea.registry;

import aphorea.AphDependencies;
import aphorea.items.ammo.GelArrowItem;
import aphorea.items.ammo.SpamBullet;
import aphorea.items.ammo.UnstableGelArrowItem;
import aphorea.items.armor.Gold.GoldHat;
import aphorea.items.armor.Rocky.RockyBoots;
import aphorea.items.armor.Rocky.RockyChestplate;
import aphorea.items.armor.Rocky.RockyHelmet;
import aphorea.items.armor.Spinel.SpinelBoots;
import aphorea.items.armor.Spinel.SpinelChestplate;
import aphorea.items.armor.Spinel.SpinelHelmet;
import aphorea.items.armor.Swamp.SwampBoots;
import aphorea.items.armor.Swamp.SwampChestplate;
import aphorea.items.armor.Swamp.SwampHood;
import aphorea.items.armor.Swamp.SwampMask;
import aphorea.items.armor.Witch.MagicalBoots;
import aphorea.items.armor.Witch.MagicalSuit;
import aphorea.items.armor.Witch.PinkWitchHat;
import aphorea.items.backpacks.*;
import aphorea.items.banners.AphStrikeBannerItem;
import aphorea.items.banners.BlankBannerItem;
import aphorea.items.banners.logic.AphBanner;
import aphorea.items.banners.logic.AphMightyBanner;
import aphorea.items.banners.logic.AphSummonerExpansionBanner;
import aphorea.items.consumable.InitialRune;
import aphorea.items.consumable.LifeSpinel;
import aphorea.items.consumable.UnstableCore;
import aphorea.items.consumable.VenomExtract;
import aphorea.items.misc.AphWrittenBook;
import aphorea.items.misc.GelSlimeNullifier;
import aphorea.items.misc.books.RunesTutorialBook;
import aphorea.items.runes.AphBaseRune;
import aphorea.items.runes.AphModifierRune;
import aphorea.items.runes.AphRunesInjector;
import aphorea.items.tools.healing.*;
import aphorea.items.tools.weapons.magic.AdeptsBook;
import aphorea.items.tools.weapons.magic.MagicalBroom;
import aphorea.items.tools.weapons.magic.UnstableGelStaff;
import aphorea.items.tools.weapons.melee.battleaxe.DemonicBattleaxe;
import aphorea.items.tools.weapons.melee.battleaxe.UnstableGelBattleaxe;
import aphorea.items.tools.weapons.melee.dagger.*;
import aphorea.items.tools.weapons.melee.glaive.WoodenRod;
import aphorea.items.tools.weapons.melee.greatsword.UnstableGelGreatsword;
import aphorea.items.tools.weapons.melee.saber.*;
import aphorea.items.tools.weapons.melee.sword.*;
import aphorea.items.tools.weapons.range.TheSpammer;
import aphorea.items.tools.weapons.range.blowgun.Blowgun;
import aphorea.items.tools.weapons.range.greatbow.GelGreatbow;
import aphorea.items.tools.weapons.range.greatbow.UnstableGelGreatbow;
import aphorea.items.tools.weapons.range.sabergun.ShotgunSaber;
import aphorea.items.tools.weapons.range.sling.FireSling;
import aphorea.items.tools.weapons.range.sling.FrozenSling;
import aphorea.items.tools.weapons.range.sling.Sling;
import aphorea.items.tools.weapons.summoner.InfectedWoodStaff;
import aphorea.items.tools.weapons.summoner.VolatileGelStaff;
import aphorea.items.tools.weapons.throwable.GelBall;
import aphorea.items.tools.weapons.throwable.GelBallGroup;
import aphorea.items.tools.weapons.throwable.UnstableGelveline;
import aphorea.items.trinkets.SwampShield;
import aphorea.items.vanillaitemtypes.AphGrassSeedItem;
import aphorea.items.vanillaitemtypes.AphMatItem;
import aphorea.items.vanillaitemtypes.AphPetItem;
import aphorea.items.vanillaitemtypes.AphSimpleTrinketItem;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem;

import java.util.ArrayList;

public class AphItems {

    public static final ArrayList<Item> initialRunes = new ArrayList<>();

    public static void registerCore() {
        // Basic Materials
        registerMaterials();

        // Tools
        registerTools();

        // Armor
        registerArmor();

        // Trinkets
        registerTrinkets();

        // Consumables
        registerConsumables();

        // Misc
        registerMisc();

        // Runes
        registerRunes();

        // Other mods
        registerMightyBannerItems();
        registerSummonerExpansionItems();
    }

    public static void registerMaterials() {
        registerItem("unstablegel", (new AphMatItem(500, Item.Rarity.COMMON)).setItemCategory("materials"), 10F);
        registerItem("rockygel", (new AphMatItem(500, Item.Rarity.NORMAL)).setItemCategory("materials"), 5F);
        registerItem("stardust", (new AphMatItem(500, Item.Rarity.COMMON)).setItemCategory("materials"), 30F);
        registerItem("infectedlog", (new AphMatItem(500, "anylog")).setItemCategory("materials", "logs"), 2F);
        registerItem("spinel", (new AphMatItem(500, Item.Rarity.UNCOMMON)).setItemCategory("materials", "minerals"), 15F);
    }

    public static void registerTools() {
        // Pickaxes Tooltips
        replaceItem("ivypickaxe", new CustomPickaxeToolItem(450, 125, 1.5F, 18, 50, 50, 700), 100.0F); // TOOL TIER CHANGE

        // Melee Weapons
        registerItem("woodenrod", new WoodenRod());
        registerItem("gelsword", new GelSword());
        registerItem("unstablegelsword", new UnstableGelSword());
        registerItem("unstablegelgreatsword", new UnstableGelGreatsword());
        registerItem("unstablegelbattleaxe", new UnstableGelBattleaxe());
        registerItem("demonicbattleaxe", new DemonicBattleaxe());
        registerItem("coppersaber", new CopperSaber());
        registerItem("ironsaber", new IronSaber());
        registerItem("goldsaber", new GoldSaber());
        registerItem("unstablegelsaber", new UnstableGelSaber(), 500F);
        registerItem("demonicsaber", new DemonicSaber());
        registerItem("broom", new Broom(), 50F);
        registerItem("voidhammer", new VoidHammer());
        registerItem("copperdagger", new CopperDagger(), 15F);
        registerItem("irondagger", new IronDagger(), 20F);
        registerItem("golddagger", new GoldDagger(), 25F);
        registerItem("demonicdagger", new DemonicDagger());
        registerItem("tungstendagger", new TungstenDagger());
        replaceItem("cutlass", new AphCutlassSaber(), 500F); // REWORKED
        registerItem("honeysaber", new HoneySaber());
        registerItem("glacialsaber", new GlacialSaber());
        registerItem("lostumbrella", new LostUmbrellaDagger());
        registerItem("brokenkora", new BrokenKora(), 200F);
        registerItem("crimsonkora", new CrimsonKora());

        // Range Weapons
        registerItem("blowgun", new Blowgun());
        registerItem("sling", new Sling());
        registerItem("firesling", new FireSling());
        registerItem("frozensling", new FrozenSling());
        registerItem("gelgreatbow", new GelGreatbow());
        registerItem("unstablegelgreatbow", new UnstableGelGreatbow());
        registerItem("thespammer", new TheSpammer(), 200F);
        registerItem("shotgunsaber", new ShotgunSaber(), 200F);

        // Magic Weapons
        registerItem("unstablegelstaff", new UnstableGelStaff());
        registerItem("magicalbroom", new MagicalBroom());
        registerItem("adeptsbook", new AdeptsBook(), 200F);

        // Summoner Weapons
        registerItem("volatilegelstaff", new VolatileGelStaff());
        registerItem("infectedwoodstaff", new InfectedWoodStaff());

        // Throwable Weapons
        registerItem("gelball", new GelBall(), 2F);
        registerItem("gelballgroup", new GelBallGroup());
        registerItem("unstablegelveline", new UnstableGelveline());

        // Work Tools
        registerItem("superiorpickaxe", new CustomPickaxeToolItem(350, 220, 6, 30, 60, 60, 1200, Item.Rarity.EPIC));

        // Healing Tools
        registerItem("healingstaff", new HealingStaff());
        registerItem("magicalvial", new MagicalVial());
        registerItem("woodenwand", new WoodenWand());
        registerItem("goldenwand", new GoldenWand());
        registerItem("spinelwand", new SpinelWand());

        // Banners
        registerItem("blankbanner", new BlankBannerItem());
        replaceItem("strikebanner", new AphStrikeBannerItem(), 50F); // REWORKED
        replaceItem("bannerofdamage", new AphBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.DAMAGE, 15), 200F); // REWORKED
        replaceItem("bannerofdefense", new AphBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.DEFENSE, 10), 200F); // REWORKED
        replaceItem("bannerofspeed", new AphBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.SPEED, 30), 200F); // REWORKED
        replaceItem("bannerofsummonspeed", new AphBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.SUMMON_SPEED, 75), 200F); // REWORKED

        // Ammo
        registerItem("gelarrow", new GelArrowItem(), 0.4F);
        registerItem("unstablegelarrow", new UnstableGelArrowItem(), 2.2F);
        registerItem("spambullet", new SpamBullet());
    }

    public static void registerArmor() {
        // Rocky
        registerItem("rockyhelmet", new RockyHelmet());
        registerItem("rockychestplate", new RockyChestplate());
        registerItem("rockyboots", new RockyBoots());

        // Gold
        registerItem("goldhat", new GoldHat());

        // Witch
        registerItem("pinkwitchhat", new PinkWitchHat(), 100F);
        registerItem("magicalsuit", new MagicalSuit());
        registerItem("magicalboots", new MagicalBoots());

        // Swamp
        registerItem("swampmask", new SwampMask());
        registerItem("swamphood", new SwampHood());
        registerItem("swampchestplate", new SwampChestplate());
        registerItem("swampboots", new SwampBoots());

        // Spinel
        registerItem("spinelhelmet", new SpinelHelmet());
        registerItem("spinelchestplate", new SpinelChestplate());
        registerItem("spinelboots", new SpinelBoots());
    }

    public static void registerTrinkets() {
        registerItem("floralring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "floralring", 200, true), 30F);
        registerItem("gelring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "gelring", 300, true), 50F);
        registerItem("heartring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "heartring", 300, true));
        registerItem("ringofhealth", (new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, new String[]{"floralring", "gelring", "heartring"}, 400, true)).addDisables("floralring", "gelring", "heartring"));
        registerItem("rockyperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "rockyperiapt", 300));
        registerItem("bloodyperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "bloodyperiapt", 300).addDisabledBy("demonicperiapt", "abysmalperiapt"));
        registerItem("demonicperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "demonicperiapt", 400).addDisabledBy("abysmalperiapt"));
        registerItem("abysmalperiapt", new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "abysmalperiapt", 500));
        registerItem("frozenperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "frozenperiapt", 300));
        registerItem("unstableperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "unstableperiapt", 300), 100F);
        registerItem("necromancyperiapt", new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "necromancyperiapt", 500));
        registerItem("witchmedallion", new AphSimpleTrinketItem(Item.Rarity.COMMON, "witchmedallion", 300, true), 100F);
        registerItem("swampshield", new SwampShield());
        registerItem("iceboots", (new AphSimpleTrinketItem(Item.Rarity.COMMON, "iceboots", 300)).addDisabledBy("spikedboots", "spikedbatboots"));
        registerItem("inspirationfoci", (new AphSimpleTrinketItem(Item.Rarity.COMMON, "inspirationfoci", 500)).addDisables("magicfoci", "rangefoci", "meleefoci", "summonfoci").addDisabledBy("magicfoci", "rangefoci", "meleefoci", "summonfoci"));
        registerItem("cursedmedallion", (new AphSimpleTrinketItem(Item.Rarity.RARE, "cursedmedallion", 500, true)), 200F);
        registerItem("ancientmedallion", (new AphSimpleTrinketItem(Item.Rarity.EPIC, "ancientmedallion", 700, true)).addDisables("witchmedallion", "cursedmedallion"), -1F);
        registerItem("essenceofhealing", (new AphSimpleTrinketItem(Item.Rarity.RARE, "essenceofhealing", 600, true)), -1F);
        registerItem("ninjascarf", (new AphSimpleTrinketItem(Item.Rarity.RARE, "ninjascarf", 700)), 250F);
        registerItem("adrenalinecharm", (new AphSimpleTrinketItem(Item.Rarity.RARE, "adrenalinecharm", 500)), 200F);
    }

    public static void registerConsumables() {
        // Bosses
        registerItem("unstablecore", new UnstableCore(), 20F);

        // Potions
        registerItem("venomextract", new VenomExtract());

        // Permanent Buffs
        registerItem("lifespinel", new LifeSpinel(), 50F);

        // Others
        registerItem("initialrune", new InitialRune(), 0F);
    }

    public static void registerMisc() {
        // Pets
        registerItem("cuberry", new AphPetItem("petphosphorslime", Item.Rarity.LEGENDARY), 50F);

        // Backpacks
        registerItem("basicbackpack", new BasicBackpack());
        registerItem("sapphirebackpack", new SapphireBackpack());
        registerItem("amethystbackpack", new AmethystBackpack());
        registerItem("rubybackpack", new RubyBackpack());
        registerItem("emeraldbackpack", new EmeraldBackpack());
        registerItem("diamondbackpack", new DiamondBackpack());

        // Books
        registerItem("runestutorialbook", new RunesTutorialBook());

        // Pure Misc
        registerItem("gelslimenullifier", new GelSlimeNullifier());
        registerItem("infectedgrassseed", new AphGrassSeedItem("infectedgrasstile"), 0.2F);
    }

    public static void registerRunes() {
        // Runes Injectors
        registerItem("rusticrunesinjector", new AphRunesInjector(Item.Rarity.NORMAL, 0, 1));
        registerItem("unstablerunesinjector", new AphRunesInjector(Item.Rarity.COMMON, 0, 1));
        registerItem("demonicrunesinjector", new AphRunesInjector(Item.Rarity.UNCOMMON, 0, 1));
        registerItem("tungstenrunesinjector", new AphRunesInjector(Item.Rarity.RARE, 0, 2));
        registerItem("ancientrunesinjector", new AphRunesInjector(Item.Rarity.EPIC, 0, 3));

        // Base Runes Tier 0
        registerItem("runeoffury", new AphBaseRune(Item.Rarity.COMMON, 1).setInitialRune());
        registerItem("runeofspeed", new AphBaseRune(Item.Rarity.COMMON, 1).setInitialRune());
        registerItem("runeofhealing", new AphBaseRune(Item.Rarity.COMMON, 0).setInitialRune());
        registerItem("runeofresistance", new AphBaseRune(Item.Rarity.COMMON, 2).setInitialRune());
        registerItem("runeofvalor", new AphBaseRune(Item.Rarity.COMMON, 1) {
            @Override
            public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "inspiration"));
                return tooltips;
            }
        }.setInitialRune());
        registerItem("runeofdetonation", new AphBaseRune(Item.Rarity.COMMON, 2, "runedamagereduction"));
        registerItem("runeofthunder", new AphBaseRune(Item.Rarity.COMMON, 2, "runedamagereductionnoboss"), 50F);
        registerItem("runeofwinter", new AphBaseRune(Item.Rarity.COMMON, 1));
        registerItem("runeofimmortality", new AphBaseRune(Item.Rarity.COMMON, 3));
        registerItem("runeofshadows", new AphBaseRune(Item.Rarity.COMMON, 1), 100F);

        // Base Runes Tier 1
        int value = 40;
        registerItem("runeofunstablegelslime", new AphBaseRune(Item.Rarity.UNCOMMON, 2, "runedamagereduction"), value += 10);
        registerItem("runeofevilsprotector", new AphBaseRune(Item.Rarity.UNCOMMON, 2), value += 10);
        registerItem("runeofqueenspider", new AphBaseRune(Item.Rarity.UNCOMMON, 1), value += 10);
        registerItem("runeofvoidwizard", new AphBaseRune(Item.Rarity.UNCOMMON, 1), value += 10);
        registerItem("runeofswampguardian", new AphBaseRune(Item.Rarity.UNCOMMON, 1), value += 10);
        registerItem("runeofancientvulture", new AphBaseRune(Item.Rarity.UNCOMMON, 2, "runedamagereduction"), value += 10);
        registerItem("runeofpiratecaptain", new AphBaseRune(Item.Rarity.UNCOMMON, 2), value += 10);

        // Base Runes Tier 2
        registerItem("runeofreaper", new AphBaseRune(Item.Rarity.RARE, 1), value += 10);
        registerItem("runeofcryoqueen", new AphBaseRune(Item.Rarity.RARE, 2, "runedamagereductionnoboss"), value += 10);
        registerItem("runeofpestwarden", new AphBaseRune(Item.Rarity.RARE, 2, "runedamagereductionnoboss"), value += 10);
        registerItem("runeofsageandgrit", new AphBaseRune(Item.Rarity.RARE, 2), value += 10);
        registerItem("runeoffallenwizard", new AphBaseRune(Item.Rarity.RARE, 2), value += 10);

        // Base Runes Tier 3
        registerItem("runeofmotherslime", new AphBaseRune(Item.Rarity.EPIC, 1, "runedamagereductionnoboss"), value += 10);
        registerItem("runeofnightswarm", new AphBaseRune(Item.Rarity.EPIC, 2), value += 10);
        registerItem("runeofspiderempress", new AphBaseRune(Item.Rarity.EPIC, 2), value += 10);
        registerItem("runeofsunlightchampion", new AphBaseRune(Item.Rarity.EPIC, 2, "runedamagereduction"), value += 10);
        registerItem("runeofmoonlightdancer", new AphBaseRune(Item.Rarity.EPIC, 1), value += 10);
        registerItem("runeofcrystaldragon", new AphBaseRune(Item.Rarity.EPIC, 2, "runedamagereductionnoboss"), value += 10);

        // Modifier Runes Tier 0
        registerItem("empoweringrune", new AphModifierRune(Item.Rarity.COMMON, 0));
        registerItem("recurrentrune", new AphModifierRune(Item.Rarity.COMMON, 0));
        registerItem("devastatingrune", new AphModifierRune(Item.Rarity.COMMON, 0));

        // Modifier Runes Tier 1
        registerItem("frenzyrune", new AphModifierRune(Item.Rarity.UNCOMMON, 0), 50F);
        registerItem("vitalrune", new AphModifierRune(Item.Rarity.UNCOMMON, 0));
        registerItem("onyxrune", new AphModifierRune(Item.Rarity.UNCOMMON, 3, "runedamagereduction"), 100F);
        registerItem("pawningrune", new AphModifierRune(Item.Rarity.UNCOMMON, 2), 200F);

        // Modifier Runes Tier 2
        registerItem("abysmalrune", new AphModifierRune(Item.Rarity.RARE, 2), 200F);
        registerItem("tidalrune", new AphModifierRune(Item.Rarity.RARE, 1), 300F);

        // Modifier Runes Tier 3
        registerItem("ascendantrune", new AphModifierRune(Item.Rarity.EPIC, 0), 300F);
    }

    public static void registerMightyBannerItems() {
        if (AphDependencies.checkMightyBanner()) {
            replaceItem("banner_of_fishing", new AphMightyBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.MIGHTY_BANNER.FISHING, 20, "banneroffishingeffect"), 200F); // REWORKED
            replaceItem("banner_of_greater_fishing", new AphMightyBanner(Item.Rarity.RARE, 480, (m) -> AphBuffs.MIGHTY_BANNER.FISHING_GREATER, 30, "banneroffishingeffect")); // REWORKED

            replaceItem("banner_of_health_regen", new AphMightyBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.MIGHTY_BANNER.HEALTH_REGEN, 0.5F, "bannerofhealthregeneffect").addFloatReplacements(true), 200F); // REWORKED
            replaceItem("banner_of_greater_health_regen", new AphMightyBanner(Item.Rarity.RARE, 480, (m) -> AphBuffs.MIGHTY_BANNER.HEALTH_REGEN_GREATER, 1, "bannerofhealthregeneffect").addFloatReplacements(true)); // REWORKED

            replaceItem("banner_of_mana_regen", new AphMightyBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.MIGHTY_BANNER.MANA_REGEN, 200, "bannerofmanaregeneffect"), 200F); // REWORKED
            replaceItem("banner_of_greater_mana_regen", new AphMightyBanner(Item.Rarity.RARE, 480, (m) -> AphBuffs.MIGHTY_BANNER.MANA_REGEN_GREATER, 400, "bannerofmanaregeneffect")); // REWORKED

            replaceItem("banner_of_resistance", new AphMightyBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.MIGHTY_BANNER.RESISTANCE, 8, "bannerofresistanceeffect"), 200F); // REWORKED
            replaceItem("banner_of_greater_resistance", new AphMightyBanner(Item.Rarity.RARE, 480, (m) -> AphBuffs.MIGHTY_BANNER.RESISTANCE_GREATER, 12, "bannerofresistanceeffect")); // REWORKED

            replaceItem("banner_of_summoning", new AphMightyBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.MIGHTY_BANNER.SUMMONING, 1, "bannerofsummoningeffect"), 200F); // REWORKED
            replaceItem("banner_of_greater_summoning", new AphMightyBanner(Item.Rarity.RARE, 480, (m) -> AphBuffs.MIGHTY_BANNER.SUMMONING_GREATER, 2, "bannerofsummoningeffect")); // REWORKED

            replaceItem("banner_of_attack_speed", new AphMightyBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.MIGHTY_BANNER.ATTACK_SPEED, 15, "bannerofattackspeedeffect"), 200F); // REWORKED
            replaceItem("banner_of_greater_attack_speed", new AphMightyBanner(Item.Rarity.RARE, 480, (m) -> AphBuffs.MIGHTY_BANNER.ATTACK_SPEED_GREATER, 20, "bannerofattackspeedeffect")); // REWORKED
        }
    }

    public static void registerSummonerExpansionItems() {
        if (AphDependencies.checkSummonerExpansion()) {
            replaceItem("bannerofresilience", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_RESILIENCE, 10), 200.0F, true);
            replaceItem("bannerofbouncing", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_BOUNCING, 4), 200.0F, true);
            replaceItem("bannerofessence", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_ESSENCE, 200), 200.0F, true);
            replaceItem("bannerofstamina", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_STAMINA, 40, 10), 200.0F, true);
            replaceItem("bannerofpicking", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_PICKING, 8).addFloatReplacements(true), 200.0F, true);
            replaceItem("bannerofdashing", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_DASHING, 1, 10), 200.0F, true);
            replaceItem("bannerofmana", new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, (m) -> AphBuffs.SUMMONER_EXPANSION.BANNER_MANA, 10, 25), 200.0F, true);
        }
    }

    private static void registerItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        ItemRegistry.registerItem(stringID, item, brokerValue, isObtainable);
    }

    private static void registerItem(String stringID, Item item, float brokerValue) {
        registerItem(stringID, item, brokerValue, true);
    }

    private static void registerItem(String stringID, Item item) {
        registerItem(stringID, item, -1F, true);
    }

    private static void replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        ItemRegistry.replaceItem(stringID, item, brokerValue, isObtainable);
    }

    private static void replaceItem(String stringID, Item item, float brokerValue) {
        replaceItem(stringID, item, brokerValue, true);
    }

    private static void replaceItem(String stringID, Item item) {
        replaceItem(stringID, item, -1F, true);
    }
}
