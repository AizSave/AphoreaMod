package aphorea.registry;

import aphorea.items.ammo.GelArrowItem;
import aphorea.items.ammo.UnstableGelArrowItem;
import aphorea.items.armor.Gold.GoldHat;
import aphorea.items.armor.Rocky.RockyBoots;
import aphorea.items.armor.Rocky.RockyChestplate;
import aphorea.items.armor.Rocky.RockyHelmet;
import aphorea.items.armor.Swamp.SwampBoots;
import aphorea.items.armor.Swamp.SwampChestplate;
import aphorea.items.armor.Swamp.SwampHood;
import aphorea.items.armor.Swamp.SwampMask;
import aphorea.items.armor.Witch.MagicalBoots;
import aphorea.items.armor.Witch.MagicalSuit;
import aphorea.items.armor.Witch.PinkWitchHat;
import aphorea.items.backpacks.*;
import aphorea.items.banners.*;
import aphorea.items.consumable.InitialRune;
import aphorea.items.consumable.LowdsPotion;
import aphorea.items.consumable.UnstableCore;
import aphorea.items.healingtools.GoldenWand;
import aphorea.items.healingtools.HealingStaff;
import aphorea.items.healingtools.MagicalVial;
import aphorea.items.healingtools.WoodenWand;
import aphorea.items.misc.GelSlimeNullifier;
import aphorea.items.runes.AphBaseRune;
import aphorea.items.runes.AphModifierRune;
import aphorea.items.runes.AphRunesInjector;
import aphorea.items.trinkets.SwampShield;
import aphorea.items.vanillaitemtypes.AphMatItem;
import aphorea.items.vanillaitemtypes.AphPetItem;
import aphorea.items.vanillaitemtypes.AphSimpleTrinketItem;
import aphorea.items.weapons.magic.AdeptsBook;
import aphorea.items.weapons.magic.MagicalBroom;
import aphorea.items.weapons.magic.UnstableGelStaff;
import aphorea.items.weapons.melee.battleaxe.DemonicBattleaxe;
import aphorea.items.weapons.melee.battleaxe.UnstableGelBattleaxe;
import aphorea.items.weapons.melee.dagger.*;
import aphorea.items.weapons.melee.glaive.WoodenRod;
import aphorea.items.weapons.melee.greatsword.UnstableGelGreatsword;
import aphorea.items.weapons.melee.saber.*;
import aphorea.items.weapons.melee.sword.Broom;
import aphorea.items.weapons.melee.sword.GelSword;
import aphorea.items.weapons.melee.sword.UnstableGelSword;
import aphorea.items.weapons.melee.sword.VoidHammer;
import aphorea.items.weapons.range.blowgun.Blowgun;
import aphorea.items.weapons.range.greatbow.GelGreatbow;
import aphorea.items.weapons.range.greatbow.UnstableGelGreatbow;
import aphorea.items.weapons.range.sling.FireSling;
import aphorea.items.weapons.range.sling.FrozenSling;
import aphorea.items.weapons.range.sling.Sling;
import aphorea.items.weapons.summoner.VolatileGelStaff;
import aphorea.items.weapons.throwable.GelBall;
import aphorea.items.weapons.throwable.GelBallGroup;
import aphorea.items.weapons.throwable.UnstableGelveline;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;

import java.util.ArrayList;

public class AphItems {

    public static final ArrayList<Item> initialRunes = new ArrayList<>();

    public static void registerCore() {
        // Basic Materials
        registerItem("unstablegel", (new AphMatItem(500, Item.Rarity.UNCOMMON)).setItemCategory("materials"), 10F);
        registerItem("rockygel", (new AphMatItem(500, Item.Rarity.COMMON)).setItemCategory("materials"), 5F);
        registerItem("stardust", (new AphMatItem(500, Item.Rarity.UNCOMMON)).setItemCategory("materials"), 30F);

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

        // Range Weapons
        registerItem("blowgun", new Blowgun());
        registerItem("sling", new Sling());
        registerItem("firesling", new FireSling());
        registerItem("frozensling", new FrozenSling());
        registerItem("gelgreatbow", new GelGreatbow());
        registerItem("unstablegelgreatbow", new UnstableGelGreatbow());

        // Magic Weapons
        registerItem("unstablegelstaff", new UnstableGelStaff());
        registerItem("magicalbroom", new MagicalBroom());
        registerItem("adeptsbook", new AdeptsBook(), 200F);

        // Summoner Weapons
        registerItem("volatilegelstaff", new VolatileGelStaff());

        // Throwable Weapons
        registerItem("gelball", new GelBall(), 2F);
        registerItem("gelballgroup", new GelBallGroup());
        registerItem("unstablegelveline", new UnstableGelveline());

        // Healing Tools
        registerItem("healingstaff", new HealingStaff());
        registerItem("magicalvial", new MagicalVial());
        registerItem("woodenwand", new WoodenWand());
        registerItem("goldenwand", new GoldenWand());

        // Banners
        registerItem("blankbanner", new BlankBannerItem());
        replaceItem("strikebanner", new AphStrikeBannerItem(), 50F);
        replaceItem("bannerofdamage", new AphBannerOfDamageItem(), 200F);
        replaceItem("bannerofdefense", new AphBannerOfDefenseItem(), 200F);
        replaceItem("bannerofspeed", new AphBannerOfSpeedItem(), 200F);
        replaceItem("bannerofsummonspeed", new AphBannerOfSummonSpeedItem(), 200F);

        // Armor
        registerItem("rockyhelmet", new RockyHelmet());
        registerItem("rockychestplate", new RockyChestplate());
        registerItem("rockyboots", new RockyBoots());
        registerItem("goldhat", new GoldHat());
        registerItem("pinkwitchhat", new PinkWitchHat(), 100F);
        registerItem("magicalsuit", new MagicalSuit());
        registerItem("magicalboots", new MagicalBoots());
        registerItem("swampmask", new SwampMask());
        registerItem("swamphood", new SwampHood());
        registerItem("swampchestplate", new SwampChestplate());
        registerItem("swampboots", new SwampBoots());

        // Trinkets
        registerItem("floralring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "floralring", 200), 30F);
        registerItem("gelring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "gelring", 300), 50F);
        registerItem("heartring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "heartring", 300));
        registerItem("ringofhealth", (new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, new String[]{"floralring", "gelring", "heartring"}, 400)).addDisables("floralring", "gelring", "heartring"));
        registerItem("rockyperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "rockyperiapt", 300));
        registerItem("bloodyperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "bloodyperiapt", 300));
        registerItem("demonicperiapt", new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "demonicperiapt", 400));
        registerItem("abysmalperiapt", new AphSimpleTrinketItem(Item.Rarity.RARE, "abysmalperiapt", 500));
        registerItem("frozenperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "frozenperiapt", 300));
        registerItem("unstableperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "unstableperiapt", 300), 100F);
        registerItem("necromancyperiapt", new AphSimpleTrinketItem(Item.Rarity.RARE, "necromancyperiapt", 500));
        registerItem("witchmedallion", new AphSimpleTrinketItem(Item.Rarity.COMMON, "witchmedallion", 300), 100F);
        registerItem("swampshield", new SwampShield());
        registerItem("iceboots", (new AphSimpleTrinketItem(Item.Rarity.COMMON, "iceboots", 300)).addDisabledBy("spikedboots", "spikedbatboots"));
        registerItem("bannerbearerfoci", (new AphSimpleTrinketItem(Item.Rarity.COMMON, "bannerbearerfoci", 500)).addDisables("magicfoci", "rangefoci", "meleefoci", "summonfoci").addDisabledBy("magicfoci", "rangefoci", "meleefoci", "summonfoci"), 200F);

        // Ammo
        registerItem("gelarrow", new GelArrowItem(), 0.4F);
        registerItem("unstablegelarrow", new UnstableGelArrowItem(), 2.2F);

        // Consumable Items
        registerItem("unstablecore", new UnstableCore(), 20F);
        registerItem("lowdspotion", new LowdsPotion());
        registerItem("initialrune", new InitialRune());

        // Pets
        registerItem("cuberry", new AphPetItem("petphosphorslime", Item.Rarity.LEGENDARY), 50F);

        // Backpacks
        registerItem("basicbackpack", new BasicBackpack());
        registerItem("sapphirebackpack", new SapphireBackpack());
        registerItem("amethystbackpack", new AmethystBackpack());
        registerItem("rubybackpack", new RubyBackpack());
        registerItem("emeraldbackpack", new EmeraldBackpack());
        registerItem("diamondbackpack", new DiamondBackpack());

        // Misc
        registerItem("gelslimenullifier", new GelSlimeNullifier());

        // Runes Injectors
        registerItem("rusticrunesinjector", new AphRunesInjector(Item.Rarity.NORMAL, 0, 0));
        registerItem("unstablerunesinjector", new AphRunesInjector(Item.Rarity.COMMON, 0, 1));
        registerItem("demonicrunesinjector", new AphRunesInjector(Item.Rarity.UNCOMMON, 0, 1));
        registerItem("tungstenrunesinjector", new AphRunesInjector(Item.Rarity.RARE, 0, 2));
        registerItem("ancientrunesinjector", new AphRunesInjector(Item.Rarity.EPIC, 0, 3));

        // Base Runes Tier 0
        registerItem("runeoffury", new AphBaseRune(1).setInitialRune());
        registerItem("runeofspeed", new AphBaseRune(1).setInitialRune());
        registerItem("runeofhealing", new AphBaseRune(1).setInitialRune());
        registerItem("runeofresistance", new AphBaseRune(2).setInitialRune());
        registerItem("runeofvalor", new AphBaseRune(1).setInitialRune());
        registerItem("runeofdetonation", new AphBaseRune(2, "runedamagereduction"));
        registerItem("runeofthunder", new AphBaseRune(2, "runedamagereductionnoboss"), 50F);
        registerItem("runeofwinter", new AphBaseRune(1));
        registerItem("runeofimmortality", new AphBaseRune(3));
        registerItem("runeofshadows", new AphBaseRune(1), 100F);

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
        registerItem("empoweringrune", new AphModifierRune(0));
        registerItem("recurrentrune", new AphModifierRune(0));
        registerItem("devastatingrune", new AphModifierRune(0));

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

    private static void registerItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        ItemRegistry.registerItem(stringID, item, brokerValue, isObtainable);
    }

    private static void registerItem(String stringID, Item item, float brokerValue) {
        registerItem(stringID, item, brokerValue, true);
    }

    private static void registerItem(String stringID, Item item, boolean isObtainable) {
        registerItem(stringID, item, -1F, isObtainable);
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

    private static void replaceItem(String stringID, Item item, boolean isObtainable) {
        replaceItem(stringID, item, -1F, isObtainable);
    }

    private static void replaceItem(String stringID, Item item) {
        replaceItem(stringID, item, -1F, true);
    }
}
