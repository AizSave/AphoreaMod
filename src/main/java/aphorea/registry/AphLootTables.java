package aphorea.registry;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CaveChestLootTable;

public class AphLootTables {
    public static LootTable infectedFieldsSurface = new LootTable();
    public static LootTable infectedLootLake = new LootTable();

    public static final RotationLootItem basicChestAllMainItems = RotationLootItem.presetRotation(
            new LootItem("zephyrcharm"), new LootItem("shinebelt"), new LootItem("heavyhammer"), new LootItem("noblehorseshoe"),
            new LootItem("ancientfeather"), new LootItem("miningcharm"), new LootItem("cactusshield"), new LootItem("airvessel"), new LootItem("prophecyslab"),
            new LootItem("swamptome"), new LootItem("slimecanister"), new LootItem("stinkflask"), new LootItem("vambrace"), new LootItem("overgrownfishingrod"),
            new LootItem("frozenwave"), new LootItem("calmingrose"), new LootItem("frozenheart"), new LootItem("sparegemstones"), new LootItem("magicbranch"),
            new LootItem("blowgun"), new LootItem("sling")
    );

    static {
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
                        LootItem.between("lifespinel", 1, 2)
                )
        );

    }

}
