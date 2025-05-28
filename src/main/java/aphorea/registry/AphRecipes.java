package aphorea.registry;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AphRecipes {

    public static void initRecipes() {
        None();
        Workstation();
        DemonicWorkstation();
        TungstenWorkstation();
        IronAnvil();
        DemonicAnvil();
        TungstenAnvil();
        Alchemy();
        Landscaping();
        Carpenter();
        FallenAnvil();

        // Modded
        Runes();
    }

    public static void None() {
        Tech tech = RecipeTechRegistry.NONE;

        addCraftingList("stonearrow", tech,
                AphCraftingRecipe.showAfter("gelarrow", 10,
                        new Ingredient("stonearrow", 10),
                        new Ingredient("gelball", 1)
                ).setHidden()
        );
    }

    public static void Workstation() {
        Tech tech = RecipeTechRegistry.WORKSTATION;

        addCraftingList("alchemytable", tech,
                AphCraftingRecipe.showAfter("runestable", 1,
                        new Ingredient("goldbar", 2),
                        new Ingredient("rockygel", 4),
                        new Ingredient("anylog", 10)
                )
        );

        addCraftingList("bannerofsummonspeed", tech,
                AphCraftingRecipe.showAfter("blankbanner", 1,
                        new Ingredient("wool", 10),
                        new Ingredient("anylog", 2)
                )
        );

        addCraftingList("stonepathtile", tech,
                AphCraftingRecipe.showAfter("geltile", 1,
                        new Ingredient("stonepathtile", 1),
                        new Ingredient("gelball", 3)
                )
        );

        addCraftingList("trackerboot", tech,
                AphCraftingRecipe.showAfter("iceboots", 1,
                        new Ingredient("trackerboot", 1),
                        new Ingredient("icetile", 10)
                )
        );

        addCraftingList("regenpendant", tech,
                AphCraftingRecipe.showAfter("frozenperiapt", 1,
                        new Ingredient("frostshard", 10),
                        new Ingredient("goldbar", 5)
                ),
                AphCraftingRecipe.showAfter("rockyperiapt", 1,
                        new Ingredient("stone", 10),
                        new Ingredient("rockygel", 4)
                )
        );

        addCraftingList("hardenedshield", tech,
                AphCraftingRecipe.showAfter("swampshield", 1,
                        new Ingredient("willowlog", 10),
                        new Ingredient("swampsludge", 3),
                        new Ingredient("stardust", 2)
                )
        );


        addCraftingList(null, tech,
                AphCraftingRecipe.showAfter("gelslimenullifier", 1,
                        new Ingredient("gelball", 20),
                        new Ingredient("unstablegel", 5)
                ),

                AphCraftingRecipe.showAfter("basicbackpack", 1,
                        new Ingredient("leather", 6),
                        new Ingredient("ironbar", 1),
                        new Ingredient("rope", 1)
                ),
                AphCraftingRecipe.showAfter("sapphirebackpack", 1,
                        new Ingredient("basicbackpack", 1),
                        new Ingredient("sapphire", 4)
                ),
                AphCraftingRecipe.showAfter("amethystbackpack", 1,
                        new Ingredient("sapphirebackpack", 1),
                        new Ingredient("amethyst", 4)
                ),
                AphCraftingRecipe.showAfter("rubybackpack", 1,
                        new Ingredient("amethystbackpack", 1),
                        new Ingredient("ruby", 4)
                ),
                AphCraftingRecipe.showAfter("emeraldbackpack", 1,
                        new Ingredient("rubybackpack", 1),
                        new Ingredient("emerald", 4)
                ),
                AphCraftingRecipe.showAfter("diamondbackpack", 1,
                        new Ingredient("emeraldbackpack", 1),
                        new Ingredient("pearlescentdiamond", 4)
                )
        );
    }

    public static void DemonicWorkstation() {
        Tech tech = RecipeTechRegistry.DEMONIC_WORKSTATION;

        addCraftingList("mysteriousportal", tech,
                AphCraftingRecipe.showAfter("unstablecore", 1,
                        new Ingredient("unstablegel", 4),
                        new Ingredient("gelball", 4)
                )
        );
        addCraftingList("chainshirt", tech,
                AphCraftingRecipe.showAfter("bloodyperiapt", 1,
                        new Ingredient("voidshard", 10),
                        new Ingredient("batwing", 10)
                ),
                AphCraftingRecipe.showAfter("demonicperiapt", 1,
                        new Ingredient("bloodyperiapt", 1),
                        new Ingredient("demonicbar", 5)
                ),
                AphCraftingRecipe.showAfter("heartring", 1,
                        new Ingredient("healthpotion", 5),
                        new Ingredient("firemone", 10),
                        new Ingredient("voidshard", 6)
                ),
                AphCraftingRecipe.showAfter("ringofhealth", 1,
                        new Ingredient("floralring", 1),
                        new Ingredient("gelring", 1),
                        new Ingredient("heartring", 1),
                        new Ingredient("goldbar", 1),
                        new Ingredient("stardust", 1)
                )
        );

        addCraftingList("balancedfoci", tech,
                AphCraftingRecipe.showAfter("inspirationfoci", 1,
                        new Ingredient("anybasicfoci", 1),
                        new Ingredient("blankbanner", 1),
                        new Ingredient("voidshard", 3)
                )
        );
    }

    public static void TungstenWorkstation() {
        Tech tech = RecipeTechRegistry.TUNGSTEN_WORKSTATION;

        addCraftingList("frozensoul", tech,
                AphCraftingRecipe.showAfter("abysmalperiapt", 1,
                        new Ingredient("demonicperiapt", 1),
                        new Ingredient("tungstenbar", 5)
                ),
                AphCraftingRecipe.showAfter("necromancyperiapt", 1,
                        new Ingredient("unstableperiapt", 1),
                        new Ingredient("bone", 10),
                        new Ingredient("demonicbar", 5)
                ),
                AphCraftingRecipe.showAfter("ancientmedallion", 1,
                        new Ingredient("witchmedallion", 1),
                        new Ingredient("cursedmedallion", 1),
                        new Ingredient("ancientfossilbar", 3)
                )
        );

        addCraftingList("lifeelixir", tech,
                AphCraftingRecipe.showBefore("lifespinel", 1,
                        new Ingredient("spinel", 6),
                        new Ingredient("stardust", 2)
                )
        );

    }

    public static void IronAnvil() {
        Tech tech = RecipeTechRegistry.IRON_ANVIL;

        addCraftingList("stonearrow", tech,
                AphCraftingRecipe.showAfter("gelarrow", 10,
                        new Ingredient("stonearrow", 10),
                        new Ingredient("gelball", 1)
                )
        );

        addCraftingList("coppersword", tech,
                AphCraftingRecipe.showAfter("coppersaber", 1,
                        new Ingredient("copperbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("ironhelmet", tech,
                AphCraftingRecipe.showBefore("rockyhelmet", 1,
                        new Ingredient("rockygel", 7),
                        new Ingredient("stone", 30),
                        new Ingredient("clay", 10)
                ),
                AphCraftingRecipe.showAfter("rockychestplate", 1,
                        new Ingredient("rockygel", 10),
                        new Ingredient("stone", 40),
                        new Ingredient("clay", 15)
                ),
                AphCraftingRecipe.showAfter("rockyboots", 1,
                        new Ingredient("rockygel", 5),
                        new Ingredient("stone", 20),
                        new Ingredient("clay", 5)
                )
        );

        addCraftingList("ironsword", tech,
                AphCraftingRecipe.showAfter("ironsaber", 1,
                        new Ingredient("ironbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("goldsword", tech,
                AphCraftingRecipe.showAfter("goldsaber", 1,
                        new Ingredient("goldbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("sparkler", tech,
                AphCraftingRecipe.showAfter("goldenwand", 1,
                        new Ingredient("woodenwand", 1),
                        new Ingredient("goldbar", 3)
                )
        );

        addCraftingList("woodboomerang", tech,
                AphCraftingRecipe.showAfter("woodenrod", 1,
                        new Ingredient("woodenwand", 2),
                        new Ingredient("wool", 2)
                ),
                AphCraftingRecipe.showAfter("blowgun", 1,
                        new Ingredient("anysapling", 10)
                ),
                AphCraftingRecipe.showAfter("sling", 1,
                        new Ingredient("leather", 4),
                        new Ingredient("rope", 2)
                ),
                AphCraftingRecipe.showAfter("firesling", 1,
                        new Ingredient("sling", 1),
                        new Ingredient("torch", 60)
                ),
                AphCraftingRecipe.showAfter("frozensling", 1,
                        new Ingredient("sling", 1),
                        new Ingredient("frostshard", 10)
                ),
                AphCraftingRecipe.showAfter("woodenwand", 1,
                        new Ingredient("anylog", 1),
                        new Ingredient("anysapling", 2),
                        new Ingredient("firemone", 2)
                ),
                AphCraftingRecipe.showAfter("gelsword", 1,
                        new Ingredient("gelball", 15)
                ),
                AphCraftingRecipe.showAfter("gelgreatbow", 1,
                        new Ingredient("gelball", 15)
                ),
                AphCraftingRecipe.showAfter("gelballgroup", 1,
                        new Ingredient("gelball", 12),
                        new Ingredient("mysteriousportal", 1)
                )
        );

        addCraftingList("froststaff", tech,
                AphCraftingRecipe.showAfter("unstablegelsword", 1,
                        new Ingredient("gelsword", 1),
                        new Ingredient("unstablegel", 10)
                ),
                AphCraftingRecipe.showAfter("unstablegelgreatsword", 1,
                        new Ingredient("unstablegel", 10),
                        new Ingredient("rockygel", 10)
                ),
                AphCraftingRecipe.showAfter("unstablegelbattleaxe", 1,
                        new Ingredient("unstablegel", 20)
                ),
                AphCraftingRecipe.showAfter("unstablegelgreatbow", 1,
                        new Ingredient("gelgreatbow", 1),
                        new Ingredient("unstablegel", 10)
                ),
                AphCraftingRecipe.showAfter("unstablegelstaff", 1,
                        new Ingredient("unstablegel", 15)
                ),
                AphCraftingRecipe.showAfter("volatilegelstaff", 1,
                        new Ingredient("unstablegel", 15)
                ),
                AphCraftingRecipe.showAfter("unstablegelveline", 1,
                        new Ingredient("gelballgroup", 1),
                        new Ingredient("unstablegel", 10)
                ),

                AphCraftingRecipe.showAfter("swampmask", 1,
                        new Ingredient("willowlog", 10),
                        new Ingredient("stardust", 3)
                ),
                AphCraftingRecipe.showAfter("swamphood", 1,
                        new Ingredient("swampsludge", 5),
                        new Ingredient("stardust", 3)
                ),
                AphCraftingRecipe.showAfter("swampchestplate", 1,
                        new Ingredient("willowlog", 5),
                        new Ingredient("swampsludge", 3),
                        new Ingredient("stardust", 2)
                ),
                AphCraftingRecipe.showAfter("swampboots", 1,
                        new Ingredient("willowlog", 8),
                        new Ingredient("stardust", 1)
                ),

                AphCraftingRecipe.showAfter("magicalsuit", 1,
                        new Ingredient("clothrobe", 1),
                        new Ingredient("stardust", 4)
                ),
                AphCraftingRecipe.showAfter("magicalboots", 1,
                        new Ingredient("clothboots", 1),
                        new Ingredient("stardust", 3)
                ),
                AphCraftingRecipe.showAfter("healingstaff", 1,
                        new Ingredient("woodstaff", 1),
                        new Ingredient("stardust", 5)
                ),
                AphCraftingRecipe.showAfter("magicalvial", 1,
                        new Ingredient("healthpotion", 10),
                        new Ingredient("stardust", 3)
                )

        );

        addCraftingList("settlementflag", tech,
                AphCraftingRecipe.showAfter("witchstatue", 1,
                        new Ingredient("goldbar", 10),
                        new Ingredient("stardust", 3)
                )
        );

    }

    public static void DemonicAnvil() {
        Tech tech = RecipeTechRegistry.DEMONIC_ANVIL;

        addCraftingList("quartzstaff", tech,
                AphCraftingRecipe.showAfter("magicalbroom", 1,
                        new Ingredient("broom", 1),
                        new Ingredient("voidshard", 8),
                        new Ingredient("stardust", 3)
                )
        );

        addCraftingList("demonicsword", tech,
                AphCraftingRecipe.showAfter("demonicdagger", 1,
                        new Ingredient("demonicbar", 5),
                        new Ingredient("anylog", 1)
                ),
                AphCraftingRecipe.showAfter("demonicsaber", 1,
                        new Ingredient("demonicbar", 12)
                ),
                AphCraftingRecipe.showAfter("demonicbattleaxe", 1,
                        new Ingredient("demonicbar", 20),
                        new Ingredient("rockygel", 10)
                )
        );

        addCraftingList("goldcrown", tech,
                AphCraftingRecipe.showAfter("goldhat", 1,
                        new Ingredient("goldbar", 8),
                        new Ingredient("sapphire", 5)
                )
        );

        addCraftingList("voidboomerang", tech,
                AphCraftingRecipe.showAfter("voidhammer", 1,
                        new Ingredient("heavyhammer", 1),
                        new Ingredient("rockygel", 8),
                        new Ingredient("voidshard", 5)
                )
        );

        addCraftingList("ivyspear", tech,
                AphCraftingRecipe.showAfter("honeysaber", 1,
                        new Ingredient("honey", 6),
                        new Ingredient("blueberry", 16),
                        new Ingredient("swampsludge", 4)
                )
        );
    }

    public static void TungstenAnvil() {
        Tech tech = RecipeTechRegistry.TUNGSTEN_ANVIL;

        addCraftingList("deepladderdown", tech,
                AphCraftingRecipe.showAfter("fakespinelchest", 1,
                        new Ingredient("spinelchest", 1),
                        new Ingredient("lifespinel", 2)
                )
        );

        addCraftingList("bonearrow", tech,
                AphCraftingRecipe.showAfter("unstablegelarrow", 10,
                        new Ingredient("bonearrow", 10),
                        new Ingredient("unstablegel", 1)
                )
        );

        addCraftingList("tungstensword", tech,
                AphCraftingRecipe.showAfter("tungstendagger", 1,
                        new Ingredient("tungstenbar", 4),
                        new Ingredient("goldbar", 2),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("voidbullet", tech,
                AphCraftingRecipe.showAfter("spambullet", 50,
                        new Ingredient("spinel", 2),
                        new Ingredient("rockygel", 5)
                )
        );

        addCraftingList("shadowbolt", tech,
                AphCraftingRecipe.showAfter("infectedstaff", 1,
                        new Ingredient("infectedalloy", 2),
                        new Ingredient("infectedlog", 12),
                        new Ingredient("infectedsapling", 3)
                ),
                AphCraftingRecipe.showAfter("infectedhat", 1,
                        new Ingredient("infectedalloy", 3),
                        new Ingredient("infectedsapling", 9)
                ),
                AphCraftingRecipe.showAfter("infectedchestplate", 1,
                        new Ingredient("infectedalloy", 4),
                        new Ingredient("infectedsapling", 12)
                ),
                AphCraftingRecipe.showAfter("infectedboots", 1,
                        new Ingredient("infectedalloy", 2),
                        new Ingredient("infectedsapling", 6)
                ),
                AphCraftingRecipe.showAfter("thenarcissist", 1,
                        new Ingredient("goldsword", 1),
                        new Ingredient("lifespinel", 1),
                        new Ingredient("spinel", 10)
                ),
                AphCraftingRecipe.showAfter("spinelcrossbow", 1,
                        new Ingredient("lifespinel", 1),
                        new Ingredient("spinel", 20)
                ),
                AphCraftingRecipe.showAfter("harpofharmony", 1,
                        new Ingredient("lifespinel", 1),
                        new Ingredient("spinel", 2),
                        new Ingredient("goldbar", 10)
                ),
                AphCraftingRecipe.showAfter("spinelwand", 1,
                        new Ingredient("goldenwand", 1),
                        new Ingredient("lifespinel", 1),
                        new Ingredient("spinel", 10)
                ),
                AphCraftingRecipe.showAfter("spinelhelmet", 1,
                        new Ingredient("lifespinel", 1),
                        new Ingredient("spinel", 24)
                ),
                AphCraftingRecipe.showAfter("spinelhat", 1,
                        new Ingredient("lifespinel", 2),
                        new Ingredient("spinel", 16)
                ),
                AphCraftingRecipe.showAfter("spinelchestplate", 1,
                        new Ingredient("lifespinel", 2),
                        new Ingredient("spinel", 32)
                ),
                AphCraftingRecipe.showAfter("spinelboots", 1,
                        new Ingredient("spinel", 16)
                )
        );

        addCraftingList("essenceofrebirth", tech,
                AphCraftingRecipe.showAfter("essenceofhealing", 1,
                        new Ingredient("spinel", 16)
                ),
                AphCraftingRecipe.showAfter("spinelshield", 1,
                        new Ingredient("tungstenshield", 1),
                        new Ingredient("spinel", 24)
                ),
                AphCraftingRecipe.showAfter("bloomrushcharm", 1,
                        new Ingredient("zephyrcharm", 1),
                        new Ingredient("adrenalinecharm", 1),
                        new Ingredient("infectedalloy", 1)
                ),
                AphCraftingRecipe.showAfter("infectedperiapt", 1,
                        new Ingredient("unstableperiapt", 1),
                        new Ingredient("infectedalloy", 2)
                )
        );

        addCraftingList("glacialboomerang", tech,
                AphCraftingRecipe.showAfter("glacialsaber", 1,
                        new Ingredient("coppersaber", 1),
                        new Ingredient("glacialbar", 8),
                        new Ingredient("glacialshard", 4)
                )
        );
    }

    public static void FallenAnvil() {
        Tech tech = RecipeTechRegistry.FALLEN_ANVIL;

        addCraftingList("icepickaxe", tech,
                AphCraftingRecipe.showBefore("superiorpickaxe", 1,
                        new Ingredient("woodpickaxe", 1),
                        new Ingredient("slimeessence", 5),
                        new Ingredient("bloodessence", 5),
                        new Ingredient("spideressence", 5),
                        new Ingredient("ancientfossilbar", 1)
                )
        );

        addCraftingList("causticexecutioner", tech,
                AphCraftingRecipe.showBefore("crimsonkora", 1,
                        new Ingredient("brokenkora", 1),
                        new Ingredient("bloodessence", 4),
                        new Ingredient("nightsteelbar", 5),
                        new Ingredient("phantomdust", 5)
                ).setTier1(),
                AphCraftingRecipe.showBefore("lostumbrella", 1,
                        new Ingredient("bloodessence", 5),
                        new Ingredient("silk", 10)
                ).setTier1()
        );


        addCraftingList("cryoglaive", tech,
                AphCraftingRecipe.showAfter("cryokatana", 1,
                        new Ingredient("reinforcedkatana", 1),
                        new Ingredient("cryoessence", 20)
                ).setTier1()
        );
    }

    public static void Alchemy() {
        Tech tech = RecipeTechRegistry.ALCHEMY;

        addCraftingList("fishingpotion", tech,
                AphCraftingRecipe.showAfter("venomextract", 1,
                        new Ingredient("cavespidergland", 5),
                        new Ingredient("glassbottle", 1)
                )
        );
    }

    public static void Landscaping() {
        Tech tech = RecipeTechRegistry.LANDSCAPING;

        addCraftingList(null, tech,
                AphCraftingRecipe.showAfter("gelrock", 1,
                        new Ingredient("rockygel", 2)
                ),
                AphCraftingRecipe.showAfter("surfacegelrock", 1,
                        new Ingredient("rockygel", 2)
                ),
                AphCraftingRecipe.showAfter("infectedgrass", 1,
                        new Ingredient("infectedgrassseed", 1)
                ),
                AphCraftingRecipe.showAfter("spinelgravel", 5,
                        new Ingredient("spinel", 1)
                ),
                AphCraftingRecipe.showAfter("spinelclustersmall", 1,
                        new Ingredient("spinel", 1)
                ),
                AphCraftingRecipe.showAfter("spinelcluster", 1,
                        new Ingredient("spinel", 2)
                ),
                AphCraftingRecipe.showAfter("tungstenoregelrock", 1,
                        new Ingredient("rockygel", 2),
                        new Ingredient("tungstenore", 3)
                )
        );
    }

    public static void Carpenter() {
        Tech tech = RecipeTechRegistry.CARPENTER;

        Recipes.registerModRecipe(new Recipe("infecteddinnertable", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 16}}")));
        Recipes.registerModRecipe(new Recipe("infectedchair", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 4}}")));
        Recipes.registerModRecipe(new Recipe("infectedbookshelf", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 10}}")));
        Recipes.registerModRecipe(new Recipe("infectedcabinet", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 10}}")));
        Recipes.registerModRecipe(new Recipe("infectedbed", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 10}, {wool, 10}}")));
        Recipes.registerModRecipe(new Recipe("infecteddoublebed", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 20}, {wool, 20}}")));
        Recipes.registerModRecipe(new Recipe("infectedcandelabra", 1, tech, Recipes.ingredientsFromScript("{{infectedlog, 6}, {torch, 3}}")));
    }


    public static void Runes() {
        Tech tech = AphTech.RUNES;
        addCraftingList(null, tech,

                // Runes injector

                AphCraftingRecipe.showAfter("rusticrunesinjector", 1,
                        new Ingredient("anylog", 4),
                        new Ingredient("anysapling", 2)
                ),
                AphCraftingRecipe.showAfter("unstablerunesinjector", 1,
                        new Ingredient("gelball", 4),
                        new Ingredient("unstablegel", 2)
                ),
                AphCraftingRecipe.showAfter("demonicrunesinjector", 1,
                        new Ingredient("demonicbar", 4),
                        new Ingredient("voidshard", 2)
                ),
                AphCraftingRecipe.showAfter("tungstenrunesinjector", 1,
                        new Ingredient("tungstenbar", 4),
                        new Ingredient("bone", 2)
                ),
                AphCraftingRecipe.showAfter("ancientrunesinjector", 1,
                        new Ingredient("ancientfossilbar", 4),
                        new Ingredient("lifequartz", 2)
                ),

                // Base runes

                AphCraftingRecipe.showAfter("runeofdetonation", 1,
                        new Ingredient("ironbomb", 5),
                        new Ingredient("goldbar", 3)
                ),
                AphCraftingRecipe.showAfter("runeofwinter", 1,
                        new Ingredient("frostshard", 8),
                        new Ingredient("demonicbar", 2)
                ),
                AphCraftingRecipe.showAfter("runeofimmortality", 1,
                        new Ingredient("healthpotion", 5),
                        new Ingredient("healthregenpotion", 2),
                        new Ingredient("ivybar", 1)
                ),
                AphCraftingRecipe.showAfter("runeoffury", 1,
                        new Ingredient("battlepotion", 2),
                        new Ingredient("tungstenbar", 1)
                ),
                AphCraftingRecipe.showAfter("runeofspeed", 1,
                        new Ingredient("speedpotion", 2),
                        new Ingredient("tungstenbar", 1)
                ),
                AphCraftingRecipe.showAfter("runeofhealing", 1,
                        new Ingredient("healthregenpotion", 2),
                        new Ingredient("tungstenbar", 1)
                ),
                AphCraftingRecipe.showAfter("runeofresistance", 1,
                        new Ingredient("resistancepotion", 2),
                        new Ingredient("tungstenbar", 1)
                ),
                AphCraftingRecipe.showAfter("runeofvalor", 1,
                        new Ingredient("blankbanner", 1),
                        new Ingredient("goldbar", 1),
                        new Ingredient("tungstenbar", 1)
                ),

                // Modifier runes

                AphCraftingRecipe.showAfter("empoweringrune", 1,
                        new Ingredient("ironbar", 2)
                ),
                AphCraftingRecipe.showAfter("recurrentrune", 1,
                        new Ingredient("speedpotion", 1)
                ),
                AphCraftingRecipe.showAfter("devastatingrune", 1,
                        new Ingredient("goldbar", 1)
                ),
                AphCraftingRecipe.showAfter("onyxrune", 1,
                        new Ingredient("batwing", 2),
                        new Ingredient("demonicbar", 1)
                ),
                AphCraftingRecipe.showAfter("vitalrune", 1,
                        new Ingredient("healthpotion", 5),
                        new Ingredient("healthregenpotion", 1),
                        new Ingredient("ivybar", 1)
                ),
                AphCraftingRecipe.showAfter("ascendantrune", 1,
                        new Ingredient("slimeessence", 5),
                        new Ingredient("bloodessence", 5),
                        new Ingredient("spideressence", 5),
                        new Ingredient("ancientfossilbar", 1)
                )
        );
    }

    public static void addCraftingList(String nextToItem, Tech[] tech, AphCraftingRecipe... recipes) {
        Arrays.stream(tech).forEach(techN -> {
            AtomicReference<String> lastRecipe = new AtomicReference<>(nextToItem);
            Arrays.stream(recipes).forEach(r -> {
                r.registerRecipe(lastRecipe.get(), techN);
                lastRecipe.set(r.item);
            });
        });
    }

    public static void addCraftingList(String nextToItem, Tech tech, AphCraftingRecipe... recipes) {
        addCraftingList(nextToItem, new Tech[]{tech}, recipes);
    }

    public static class AphCraftingRecipe {
        public String item;
        private final int amount;
        private final Ingredient[] ingredients;
        private final boolean showAfter;
        private GNDItemMap gndData;
        private boolean isHidden;

        private AphCraftingRecipe(String item, int amount, boolean isHidden, boolean showAfter, Ingredient... ingredients) {
            this.item = item;
            this.amount = amount;
            this.isHidden = isHidden;
            this.showAfter = showAfter;
            this.ingredients = ingredients;
            this.gndData = null;
        }

        public static AphCraftingRecipe showAfter(String item, int amount, Ingredient... ingredients) {
            return new AphCraftingRecipe(item, amount, false, true, ingredients);
        }

        public static AphCraftingRecipe showBefore(String item, int amount, Ingredient... ingredients) {
            return new AphCraftingRecipe(item, amount, false, false, ingredients);
        }

        public AphCraftingRecipe setHidden(boolean hidden) {
            this.isHidden = hidden;
            return this;
        }

        public AphCraftingRecipe setHidden() {
            return setHidden(true);
        }

        public AphCraftingRecipe setTier1() {
            this.gndData = new GNDItemMap().setInt("upgradeLevel", 100);
            return this;
        }

        public void registerRecipe(String nextToItem, Tech tech) {
            Recipe recipe = new Recipe(item, amount, tech, ingredients, isHidden, gndData);

            if (nextToItem != null) {
                if (showAfter) {
                    recipe.showAfter(nextToItem);
                } else {
                    recipe.showBefore(nextToItem);
                }
            }
            Recipes.registerModRecipe(recipe);
        }
    }

}
