package aphorea.biomes.presets;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.WallSet;

public class FishingHutNoHumanPreset extends Preset {
    public FishingHutNoHumanPreset(GameRandom random, WallSet wallSet, int newTileID) {
        super(15, 9);
        this.applyScript("PRESET = {\n\twidth = 15,\n\theight = 9,\n\ttileIDs = [12, woodfloor, 15, woodpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 15, 15, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 15, 15, 15, 15, 15, 15, 15, -1, -1, 12, 12, 12, 12, 12, 12, 15, 15, 15, 15, 15, 15, 15, -1, -1, 12, 12, 12, 12, 12, 12, 15, 15, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 15, 15, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 15, 15, -1, -1, -1, -1, -1, -1, -1, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 288, oakbench2, 204, walllantern, 301, sprucedinnertable, 302, sprucedinnertable2, 174, barrel, 206, candle, 49, woodwall, 305, sprucechair, 50, wooddoor, 149, woodfence, 310, sprucebed, 311, sprucebed2, 760, grass, 315, sprucedisplay, 287, oakbench],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 49, 49, 49, 49, 49, 149, 149, 0, 0, 0, 0, 0, 0, 0, 49, 149, 301, 302, 149, 49, 204, 174, 149, 0, 149, 0, 149, 0, 0, 49, 310, 0, 305, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 311, 0, 0, 0, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 149, 0, 0, 315, 49, 288, 0, 0, 0, 0, 0, 0, 0, 0, 49, 49, 50, 49, 49, 49, 287, 0, 0, 0, 0, 0, 0, 0, 0, 149, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 760, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 0, 3, 0, 3, 0, 3, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 206, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        wallSet.replacePreset(WallSet.wood, this);
        this.replaceTile(TileRegistry.woodPathID, newTileID);
        LootTable displayLootTable = new LootTable(new LootItem("fishingpotion", 1));
        LootTable barrelLootTable = new LootTable(new LootItem("woodfishingrod", 1), new LootItem("wormbait", random.getIntBetween(16, 32)));
        this.addInventory(displayLootTable, random, 5, 5);
        this.addInventory(barrelLootTable, random, 8, 2);
        this.addCustomPreApplyRectEach(-1, -1, 9, this.height + 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile(levelX, levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }

            return null;
        });
        this.addCustomPreApplyRectEach(7, -1, this.width - 9, this.height + 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile(levelX, levelY).getStringID().equals("grass")) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }

            return null;
        });
        this.addCanApplyRectPredicate(0, 0, 7, 9, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (level.isLiquidTile(x, y)) {
                        return false;
                    }
                }
            }

            return true;
        });
        this.addCanApplyRectPredicate(7, 0, 5, 5, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int tileY = levelStartY; tileY < levelEndY; ++tileY) {
                for (int tileX = levelStartX; tileX < levelEndX; ++tileX) {
                    if (level.isLiquidTile(tileX, tileY)) {
                        return true;
                    }
                }
            }

            return false;
        });
    }
}
