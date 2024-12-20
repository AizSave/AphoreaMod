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
import aphorea.items.banners.BlankBannerItem;
import aphorea.items.banners.StrikeBannerItem;
import aphorea.items.consumable.LowdsPotion;
import aphorea.items.consumable.UnstableCore;
import aphorea.items.healingtools.GoldenWand;
import aphorea.items.healingtools.HealingStaff;
import aphorea.items.healingtools.MagicalVial;
import aphorea.items.healingtools.WoodenWand;
import aphorea.items.misc.GelSlimeNullifier;
import aphorea.items.trinkets.SwampShield;
import aphorea.items.weapons.magic.AdeptsBook;
import aphorea.items.weapons.magic.MagicalBroom;
import aphorea.items.weapons.magic.UnstableGelStaff;
import aphorea.items.weapons.melee.*;
import aphorea.items.weapons.range.*;
import aphorea.items.weapons.summoner.VolatileGelStaff;
import aphorea.items.weapons.throwable.GelBall;
import aphorea.items.weapons.throwable.GelBallGroup;
import aphorea.items.weapons.throwable.UnstableGelveline;
import aphorea.other.itemtype.AphBaseRune;
import aphorea.other.itemtype.AphModifierRune;
import aphorea.other.itemtype.AphRunesInjector;
import aphorea.other.vanillaitemtypes.AphMatItem;
import aphorea.other.vanillaitemtypes.AphPetItem;
import aphorea.other.vanillaitemtypes.AphSimpleTrinketItem;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;

public class AphItems {

    public static void registerCore() {
        // Basic Materials
        registerItem("unstablegel", (new AphMatItem(500, Item.Rarity.UNCOMMON)).setItemCategory("materials"), 10.0F);
        registerItem("rockygel", (new AphMatItem(500, Item.Rarity.COMMON)).setItemCategory("materials"), 5.0F);
        registerItem("stardust", (new AphMatItem(500, Item.Rarity.UNCOMMON)).setItemCategory("materials"), 30.0F);

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
        registerItem("unstablegelsaber", new UnstableGelSaber(), 500.0F);
        registerItem("demonicsaber", new DemonicSaber());
        registerItem("broom", new Broom(), 50.0F);
        registerItem("voidhammer", new VoidHammer());

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
        registerItem("adeptsbook", new AdeptsBook(), 200.0F);

        // Summoner Weapons
        registerItem("volatilegelstaff", new VolatileGelStaff());

        // Throwable Weapons
        registerItem("gelball", new GelBall(), 2.0F);
        registerItem("gelballgroup", new GelBallGroup());
        registerItem("unstablegelveline", new UnstableGelveline());

        // Healing Tools
        registerItem("healingstaff", new HealingStaff());
        registerItem("magicalvial", new MagicalVial());
        registerItem("woodenwand", new WoodenWand());
        registerItem("goldenwand", new GoldenWand());

        // Banners
        registerItem("blankbanner", new BlankBannerItem());
        replaceItem("strikebanner", new StrikeBannerItem(), 50.0F);

        // Armor
        registerItem("rockyboots", new RockyBoots());
        registerItem("rockychestplate", new RockyChestplate());
        registerItem("rockyhelmet", new RockyHelmet());
        registerItem("goldhat", new GoldHat());
        registerItem("magicalboots", new MagicalBoots());
        registerItem("magicalsuit", new MagicalSuit());
        registerItem("witchhat", new PinkWitchHat(), 100.0F);
        registerItem("swampboots", new SwampBoots());
        registerItem("swampchestplate", new SwampChestplate());
        registerItem("swampmask", new SwampMask());
        registerItem("swamphood", new SwampHood());

        // Trinkets
        registerItem("floralring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "floralring", 200), 30.0F);
        registerItem("gelring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "gelring", 300), 50.0F);
        registerItem("heartring", new AphSimpleTrinketItem(Item.Rarity.COMMON, "heartring", 300));
        registerItem("ringofhealth", (new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, new String[]{"floralring", "gelring", "heartring"}, 400)).addDisables("floralring", "gelring", "heartring"));
        registerItem("rockyperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "rockyperiapt", 300));
        registerItem("bloodyperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "bloodyperiapt", 300));
        registerItem("demonicperiapt", new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "demonicperiapt", 400));
        registerItem("abysmalperiapt", new AphSimpleTrinketItem(Item.Rarity.RARE, "abysmalperiapt", 500));
        registerItem("frozenperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "frozenperiapt", 300));
        registerItem("unstableperiapt", new AphSimpleTrinketItem(Item.Rarity.COMMON, "unstableperiapt", 300), 100.0F);
        registerItem("necromancyperiapt", new AphSimpleTrinketItem(Item.Rarity.RARE, "necromancyperiapt", 500));
        registerItem("witchmedallion", new AphSimpleTrinketItem(Item.Rarity.COMMON, "witchmedallion", 300), 100.0F);
        registerItem("swampshield", new SwampShield());

        // Ammo
        registerItem("gelarrow", new GelArrowItem(), 0.4F);
        registerItem("unstablegelarrow", new UnstableGelArrowItem(), 2.2F);

        // Consumable Items
        registerItem("unstablecore", new UnstableCore(), 20.0F);
        registerItem("lowdspotion", new LowdsPotion());

        // Pets
        registerItem("cuberry", new AphPetItem("petphosphorslime", Item.Rarity.LEGENDARY), 50.0F);

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
        registerItem("demonicrunesinjector", new AphRunesInjector(1));
        registerItem("piraterunesinjector", new AphRunesInjector(1));
        registerItem("pestrunesinjector", new AphRunesInjector(1));

        // Base Runes
        registerItem("dashrune", new AphBaseRune(1));
        registerItem("healingrune", new AphBaseRune(0));
        registerItem("hardeningrune", new AphBaseRune(2));

        // Modifier Runes
        registerItem("empoweringrune", new AphModifierRune());
        registerItem("recurrentrune", new AphModifierRune());
        registerItem("frenzyrune", new AphModifierRune());
        registerItem("devastatingrune", new AphModifierRune());

        // In Development
        registerItem("bannerbearerfoci", (new SimpleTrinketItem(Item.Rarity.COMMON, "bannerbearerfoci", 500)).addDisables("magicfoci", "rangefoci", "meleefoci", "summonfoci").addDisabledBy("magicfoci", "rangefoci", "meleefoci", "summonfoci"), 200.0F);
        registerItem("iceboots", (new SimpleTrinketItem(Item.Rarity.COMMON, "iceboots", 300)).addDisabledBy("spikedboots", "spikedbatboots"));
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
