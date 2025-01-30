package aphorea.biomes.presets;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class WitchCabinMadsNoHumanPreset extends LandStructurePreset {
    public WitchCabinMadsNoHumanPreset(GameRandom random) {
        super(14, 15);
        this.applyScript("PRESET = {\n\twidth = 14,\n\theight = 15,\n\ttileIDs = [10, dungeonfloor],\n\ttiles = [-1, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, 10, 10, 10, 10, 10, 10, -1, 10, 10, -1, 10, -1, -1, -1, -1, -1, 10, 10, -1, 10, 10, 10, -1, 10, -1, -1, -1, -1, -1, -1, 10, -1, 10, 10, -1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 416, dungeonbench2, 417, dungeonbookshelf, 419, dungeonbed, 420, dungeonbed2, 421, dungeondresser, 166, stonefence, 422, dungeonclock, 423, dungeoncandelabra, 627, cookingpot, 88, swampstonewall, 409, dungeonchest, 89, swampstonedoor, 410, dungeondinnertable, 411, dungeondinnertable2, 412, dungeondesk, 220, candle, 221, wallcandle, 414, dungeonchair, 415, dungeonbench],\n\tobjects = [0, 88, 88, 88, 88, 88, 88, 0, 0, 0, 0, 0, 0, 0, 0, 88, 0, 221, 0, 0, 88, 88, 88, 88, 88, 0, 0, 0, 0, 88, 0, 0, 0, 0, 166, 417, 414, 423, 88, 0, 0, 0, 0, 88, 0, 0, 0, 0, 166, 0, 0, 0, 88, 0, 0, 0, 0, 88, 0, 0, 0, 0, 0, 0, 0, 0, 88, 88, 88, 0, 0, 88, 0, 0, 0, 166, 166, 409, 0, 0, 0, 412, 88, 0, 0, 88, 88, 88, 166, 166, 422, 0, 0, 0, 0, 414, 88, 0, 0, 0, 88, 423, 0, 0, 0, 0, 627, 0, 0, 0, 88, 0, 0, 0, 88, 0, 0, 0, 0, 0, 0, 0, 0, 423, 88, 0, 0, 0, 88, 419, 420, 410, 411, 0, 0, 0, 0, 421, 88, 0, 0, 0, 88, 88, 88, 88, 88, 88, 89, 88, 88, 88, 88, 0, 0, 0, 0, 0, 0, 415, 416, 0, 0, 0, 221, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 1, 3, 3, 3, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1, 1, 2, 0, 3, 3, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 2, 0, 0, 0, 1, 1, 1, 1, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 2, 1, 1, 1, 1, 0, 0, 0, 0, 3, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 220, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 220, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        TicketSystemList<Integer> weightedTiles = new TicketSystemList<>();
        weightedTiles.addObject(120, TileRegistry.dungeonFloorID).addObject(80, TileRegistry.woodFloorID).addObject(30, TileRegistry.swampGrassID).addObject(20, TileRegistry.mudID);

        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                if (this.getTile(x, y) == TileRegistry.dungeonFloorID) {
                    this.setTile(x, y, weightedTiles.getRandomObject(random));
                }
            }
        }

        this.addInventory(LootTablePresets.witchCrate, random, 7, 5);
        PresetUtils.addFuelToInventory(this, 8, 7, random, "oaklog", 20, 40, true);
        this.addMob("spider", 3, 2, false);
        this.addMob("spider", 4, 3, false);
        this.addMob("spider", 3, 4, false);
        this.addMob("enchantedcrawlingzombie", 4, 2, false);
        this.addMob("enchantedcrawlingzombie", 3, 3, false);
        this.addMob("enchantedcrawlingzombie", 4, 4, false);
    }
}
