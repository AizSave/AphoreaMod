package aphorea.biomes.presets;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.CampfireObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class HunterCabinNoHumanPreset extends Preset {
    public HunterCabinNoHumanPreset(GameRandom random, FurnitureSet furnitureSet, WallSet wallSet) {
        super("PRESET = {\n\twidth = 15,\n\theight = 8,\n\ttileIDs = [36, graveltile, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, -1, 36, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 36, -1, -1, -1, -1, 36, -1, -1, -1, 12, 12, 12, 12, 12, 12, -1, 36, -1, 36, 36, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, -1, -1, 36, -1, -1, 36, -1, -1, -1, 12, 12, 12, 12, 12, 12, -1, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, wooddoor, 258, trainingdummy, 265, gravestone2, 299, oakmodulartable, 300, oakchair, 301, oakbench, 302, oakbench2, 305, oakbed, 306, oakbed2, 628, roastingstation, 309, oakcandelabra, 597, sunflower, 218, walllantern, 188, barrel, 254, paintingposter, 190, coolingbox, 63, woodwall, 255, sign],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, 63, 63, 63, 63, 255, -1, -1, -1, -1, -1, -1, -1, -1, 265, 63, 309, 299, 597, 63, 0, 0, -1, -1, -1, -1, -1, -1, -1, 63, 63, 0, 300, 0, 64, 0, 0, 0, 188, 301, 302, -1, -1, -1, 63, 254, 0, 0, 0, 63, 218, 0, 0, 0, 0, 0, -1, -1, -1, 63, 305, 306, 0, 190, 63, 0, 0, 0, 0, 628, 0, -1, -1, -1, 63, 63, 63, 63, 63, 63, 258, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 0, 3, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 2, 3, 2, 0, 0, 2, 2, 2, 2, 2, 2, 3, 2, 2, 0, 0, 0, 1, 0, 0, 3, 3, 1, 1, 2, 2, 3, 2, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2, 2, 3, 2, 1, 1, 0, 0, 0, 1, 0, 0, 0, 2, 0, 2, 2, 3, 2, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        if (furnitureSet != null) {
            furnitureSet.replacePreset(FurnitureSet.oak, this);
        }

        if (wallSet != null) {
            wallSet.replacePreset(WallSet.wood, this);
        }

        this.addInventory(new LootTable(LootItem.between("oaklog", 2, 15)), random, 11, 5, new Object[0]);
        this.addInventory(new LootTable(LootItem.between("iceblossom", 8, 14), LootTablePresets.hunterCookedFoodLootTable), random, 5, 5, new Object[0]);
        this.addInventory(new LootTable(LootTablePresets.bowAndArrowsLootTable), random, 10, 3);
        this.addCustomApply(11, 5, 0, (level, levelX, levelY, dir, blackboard) -> {
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(levelX, levelY);
            if (objectEntity instanceof CampfireObjectEntity) {
                CampfireObjectEntity campfire = (CampfireObjectEntity) objectEntity;
                campfire.keepRunning = false;
            }

            return null;
        });
        this.addCustomApply(7, 1, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    ((SignObjectEntity) objEnt).setText(HumanMob.getRandomName(random, random.getOneOf(HumanMob.maleNames, HumanMob.femaleNames, HumanMob.femaleNames, HumanMob.elderNames)) + "'s Hunting Lodge");
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            } catch (Exception var8) {
                System.err.println(var8.getMessage());
            }

            return null;
        });
        this.addCanApplyRectPredicate(-1, -1, this.width + 2, this.height + 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (level.isLiquidTile(x, y)) {
                        return false;
                    }
                }
            }

            return true;
        });
        this.addCanApplyRectPredicate(-10, -10, this.width + 20, this.height + 20, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
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
