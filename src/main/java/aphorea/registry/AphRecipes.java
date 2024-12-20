package aphorea.registry;

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
    }

    public static void None() {
        Tech tech = RecipeTechRegistry.NONE;

        addCraftingList("stonearrow", tech,
                new AphCraftingRecipe("gelarrow", 10, true, true,
                        new Ingredient("stonearrow", 10),
                        new Ingredient("gelball", 1)
                )
        );
    }

    public static void Workstation() {
        Tech tech = RecipeTechRegistry.WORKSTATION;

        addCraftingList("bannerofsummonspeed", tech,
                new AphCraftingRecipe("blankbanner", 1,
                        new Ingredient("wool", 10),
                        new Ingredient("anylog", 2)
                )
        );

        addCraftingList("stonepathtile", tech,
                new AphCraftingRecipe("geltile", 1,
                        new Ingredient("stonepathtile", 1),
                        new Ingredient("gelball", 3)
                )
        );

        addCraftingList("regenpendant", tech,
                new AphCraftingRecipe("frozenperiapt", 1,
                        new Ingredient("frostshard", 10),
                        new Ingredient("goldbar", 5)
                ),
                new AphCraftingRecipe("rockyperiapt", 1,
                        new Ingredient("stone", 10),
                        new Ingredient("rockygel", 4)
                )
        );

        addCraftingList("hardenedshield", tech,
                new AphCraftingRecipe("swampshield", 1,
                        new Ingredient("willowlog", 10),
                        new Ingredient("swampsludge", 3),
                        new Ingredient("stardust", 2)
                )
        );


        addCraftingList(null, tech,
                new AphCraftingRecipe("gelslimenullifier", 1,
                        new Ingredient("gelball", 20),
                        new Ingredient("unstablegel", 5)
                ),

                new AphCraftingRecipe("basicbackpack", 1,
                        new Ingredient("leather", 6),
                        new Ingredient("ironbar", 1),
                        new Ingredient("rope", 1)
                ),
                new AphCraftingRecipe("sapphirebackpack", 1,
                        new Ingredient("basicbackpack", 1),
                        new Ingredient("sapphire", 4)
                ),
                new AphCraftingRecipe("amethystbackpack", 1,
                        new Ingredient("sapphirebackpack", 1),
                        new Ingredient("amethyst", 4)
                ),
                new AphCraftingRecipe("rubybackpack", 1,
                        new Ingredient("amethystbackpack", 1),
                        new Ingredient("ruby", 4)
                ),
                new AphCraftingRecipe("emeraldbackpack", 1,
                        new Ingredient("rubybackpack", 1),
                        new Ingredient("emerald", 4)
                ),
                new AphCraftingRecipe("diamondbackpack", 1,
                        new Ingredient("emeraldbackpack", 1),
                        new Ingredient("pearlescentdiamond", 4)
                )
        );
    }

    public static void DemonicWorkstation() {
        Tech tech = RecipeTechRegistry.DEMONIC_WORKSTATION;

        addCraftingList("chainshirt", tech,
                new AphCraftingRecipe("bloodyperiapt", 1,
                        new Ingredient("voidshard", 10),
                        new Ingredient("batwing", 10)
                ),
                new AphCraftingRecipe("demonicperiapt", 1,
                        new Ingredient("bloodyperiapt", 1),
                        new Ingredient("demonicbar", 5)
                ),
                new AphCraftingRecipe("heartring", 1,
                        new Ingredient("healthpotion", 5),
                        new Ingredient("firemone", 10),
                        new Ingredient("voidshard", 6)
                ),
                new AphCraftingRecipe("ringofhealth", 1,
                        new Ingredient("floralring", 1),
                        new Ingredient("gelring", 1),
                        new Ingredient("heartring", 1),
                        new Ingredient("goldbar", 1),
                        new Ingredient("stardust", 1)
                )
        );

        addCraftingList(null, tech,
                new AphCraftingRecipe("bannerbearerfoci", 1,
                        new Ingredient("meleefoci", 1),
                        new Ingredient("rangefoci", 1),
                        new Ingredient("magicfoci", 1),
                        new Ingredient("summonfoci", 1),
                        new Ingredient("blankbanner", 1)
                )
        );
    }

    public static void TungstenWorkstation() {
        Tech tech = RecipeTechRegistry.TUNGSTEN_WORKSTATION;

        addCraftingList("demonicperiapt", tech,
                new AphCraftingRecipe("abysmalperiapt", 1,
                        new Ingredient("demonicperiapt", 1),
                        new Ingredient("tungstenbar", 5)
                ),
                new AphCraftingRecipe("necromancyperiapt", 1,
                        new Ingredient("unstableperiapt", 1),
                        new Ingredient("bone", 10),
                        new Ingredient("demonicbar", 5)
                )
        );
    }

    public static void IronAnvil() {
        Tech tech = RecipeTechRegistry.IRON_ANVIL;

        addCraftingList("stonearrow", tech,
                new AphCraftingRecipe("gelarrow", 10,
                        new Ingredient("stonearrow", 10),
                        new Ingredient("gelball", 1)
                )
        );

        addCraftingList(null, tech,
                new AphCraftingRecipe("woodenwand", 1,
                        new Ingredient("anylog", 1),
                        new Ingredient("anysapling", 2),
                        new Ingredient("firemone", 2)
                ),
                new AphCraftingRecipe("goldenwand", 1,
                        new Ingredient("woodenwand", 1),
                        new Ingredient("goldbar", 3)
                ),
                new AphCraftingRecipe("healingstaff", 1,
                        new Ingredient("woodstaff", 1),
                        new Ingredient("stardust", 5)
                ),
                new AphCraftingRecipe("magicalvial", 1,
                        new Ingredient("healthpotion", 10),
                        new Ingredient("stardust", 3)
                )
        );

        addCraftingList("frostboomerang", tech,
                new AphCraftingRecipe("woodenrod", 1,
                        new Ingredient("woodenwand", 2),
                        new Ingredient("wool", 2)
                ),
                new AphCraftingRecipe("gelsword", 1,
                        new Ingredient("gelball", 15)
                ),
                new AphCraftingRecipe("unstablegelsword", 1,
                        new Ingredient("gelsword", 1),
                        new Ingredient("unstablegel", 10)
                ),
                new AphCraftingRecipe("unstablegelgreatsword", 1,
                        new Ingredient("unstablegel", 10),
                        new Ingredient("rockygel", 10)
                ),
                new AphCraftingRecipe("unstablegelbattleaxe", 1,
                        new Ingredient("unstablegel", 20)
                )
        );

        addCraftingList("frostbow", tech,
                new AphCraftingRecipe("gelgreatbow", 1,
                        new Ingredient("gelball", 15)
                ),
                new AphCraftingRecipe("unstablegelgreatbow", 1,
                        new Ingredient("gelgreatbow", 1),
                        new Ingredient("unstablegel", 10)
                ),
                new AphCraftingRecipe("blowgun", 1,
                        new Ingredient("anysapling", 10)
                ),
                new AphCraftingRecipe("sling", 1,
                        new Ingredient("leather", 4),
                        new Ingredient("rope", 2)
                ),
                new AphCraftingRecipe("firesling", 1,
                        new Ingredient("sling", 1),
                        new Ingredient("torch", 60)
                ),
                new AphCraftingRecipe("frozensling", 1,
                        new Ingredient("sling", 1),
                        new Ingredient("frostshard", 10)
                )
        );

        addCraftingList(null, tech,
                new AphCraftingRecipe("gelballgroup", 1,
                        new Ingredient("gelball", 12),
                        new Ingredient("mysteriousportal", 1)
                ),
                new AphCraftingRecipe("unstablegelveline", 1,
                        new Ingredient("gelballgroup", 1),
                        new Ingredient("unstablegel", 10)
                )
        );


        addCraftingList("froststaff", tech,
                new AphCraftingRecipe("unstablegelstaff", 1,
                        new Ingredient("unstablegel", 15)
                )
        );

        addCraftingList("spiderstaff", tech,
                new AphCraftingRecipe("volatilegelstaff", 1,
                        new Ingredient("unstablegel", 15)
                )
        );

        addCraftingList(null, tech,
                new AphCraftingRecipe("witchstatue", 1,
                        new Ingredient("goldbar", 10),
                        new Ingredient("stardust", 3)
                )
        );

        addCraftingList("ironboots", tech,
                new AphCraftingRecipe("rockyhelmet", 1,
                        new Ingredient("stone", 30),
                        new Ingredient("rockygel", 7)
                ),
                new AphCraftingRecipe("rockychestplate", 1,
                        new Ingredient("stone", 40),
                        new Ingredient("rockygel", 10)
                ),
                new AphCraftingRecipe("rockyboots", 1,
                        new Ingredient("stone", 20),
                        new Ingredient("rockygel", 5)
                )
        );

        addCraftingList("coppersword", tech,
                new AphCraftingRecipe("coppersaber", 1,
                        new Ingredient("copperbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("ironsword", tech,
                new AphCraftingRecipe("ironsaber", 1,
                        new Ingredient("ironbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("goldsword", tech,
                new AphCraftingRecipe("goldsaber", 1,
                        new Ingredient("goldbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("goldsword", tech,
                new AphCraftingRecipe("goldsaber", 1,
                        new Ingredient("goldbar", 10),
                        new Ingredient("anylog", 1)
                )
        );

        addCraftingList("frostboots", tech,
                new AphCraftingRecipe("swampmask", 1,
                        new Ingredient("willowlog", 10),
                        new Ingredient("stardust", 3)
                ),
                new AphCraftingRecipe("swamphood", 1,
                        new Ingredient("swampsludge", 5),
                        new Ingredient("stardust", 3)
                ),
                new AphCraftingRecipe("swampchestplate", 1,
                        new Ingredient("willowlog", 5),
                        new Ingredient("swampsludge", 3),
                        new Ingredient("stardust", 2)
                ),
                new AphCraftingRecipe("swampboots", 1,
                        new Ingredient("willowlog", 8),
                        new Ingredient("stardust", 1)
                )
        );


        addCraftingList("clothboots", tech,
                new AphCraftingRecipe("magicalsuit", 1,
                        new Ingredient("clothrobe", 1),
                        new Ingredient("stardust", 4)
                ),
                new AphCraftingRecipe("magicalboots", 1,
                        new Ingredient("clothboots", 1),
                        new Ingredient("stardust", 3)
                )
        );

    }

    public static void DemonicAnvil() {
        Tech tech = RecipeTechRegistry.DEMONIC_ANVIL;

        addCraftingList("quartzstaff", tech,
                new AphCraftingRecipe("magicalbroom", 1,
                        new Ingredient("broom", 1),
                        new Ingredient("voidshard", 8),
                        new Ingredient("stardust", 3)
                )
        );

        addCraftingList("demonicsword", tech,
                new AphCraftingRecipe("demonicsaber", 1,
                        new Ingredient("demonicbar", 12)
                ),
                new AphCraftingRecipe("demonicbattleaxe", 1,
                        new Ingredient("demonicbar", 20),
                        new Ingredient("rockygel", 10)
                )
        );

        addCraftingList("goldcrown", tech,
                new AphCraftingRecipe("goldhat", 1,
                        new Ingredient("goldbar", 8),
                        new Ingredient("sapphire", 5)
                )
        );

        addCraftingList("voidboomerang", tech,
                new AphCraftingRecipe("voidhammer", 1,
                        new Ingredient("heavyhammer", 1),
                        new Ingredient("rockygel", 8),
                        new Ingredient("voidshard", 5)
                )
        );

    }

    public static void TungstenAnvil() {
        Tech tech = RecipeTechRegistry.TUNGSTEN_ANVIL;

        addCraftingList("bonearrow", tech,
                new AphCraftingRecipe("unstablegelarrow", 10,
                        new Ingredient("bonearrow", 10),
                        new Ingredient("unstablegel", 1)
                )
        );
    }

    public static void Alchemy() {
        Tech tech = RecipeTechRegistry.ALCHEMY;

        addCraftingList("fishingpotion", tech,
                new AphCraftingRecipe("lowdspotion", 1,
                        new Ingredient("cavespidergland", 5),
                        new Ingredient("glassbottle", 1)
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
        private final boolean hidden;
        private final Ingredient[] ingredients;
        private final boolean showAfter;

        public AphCraftingRecipe(String item, int amount, boolean hidden, boolean showAfter, Ingredient... ingredients) {
            this.item = item;
            this.amount = amount;
            this.hidden = hidden;
            this.showAfter = showAfter;
            this.ingredients = ingredients;
        }

        public AphCraftingRecipe(String item, int amount, Ingredient... ingredients) {
            this(item, amount, false, true, ingredients);
        }

        public void registerRecipe(String nextToItem, Tech tech) {
            Recipe recipe = new Recipe(item, amount, tech, ingredients, hidden);

            if (nextToItem != null) {
                if(showAfter) {
                    recipe.showAfter(nextToItem);
                } else {
                    recipe.showBefore(nextToItem);
                }
            }
            Recipes.registerModRecipe(recipe);
        }
    }

}
