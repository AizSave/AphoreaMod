package aphorea.biomes.presets;

import aphorea.registry.AphLootTables;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.Preset;

import java.awt.*;

public class RuneInventorHousePreset extends Preset {
    public static int edgeSpace = 5;

    public RuneInventorHousePreset(GameRandom random) {
        super("PRESET = {\n\twidth = 7,\n\theight = 8,\n\ttileIDs = [3, grasstile, 14, woodfloor, 45, graveltile],\n\ttiles = [45, 45, 3, 3, 3, 3, 3, 45, 45, 14, 14, 14, 14, 3, 45, 45, 14, 14, 14, 14, 3, 45, 45, 3, 14, 14, 14, 3, 45, 45, 3, 3, 3, 3, 3, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45],\n\tobjectIDs = [0, air, 69, woodwall, 70, wooddoor, 74, woodwindow, 214, woodfence, 311, sign, 323, gravestone2, 356, oakchest, 357, oakdinnertable, 358, oakdinnertable2, 362, oakbench, 363, oakbench2, 366, oakbed, 367, oakbed2, 1140, runestable],\n\tobjects = [214, 214, 69, 69, 74, 69, 69, 214, 0, 69, 356, 367, 366, 69, 214, 0, 70, 0, 0, 0, 74, 214, 0, 69, 1140, 358, 357, 69, 214, 0, 69, 69, 74, 69, 69, 214, 0, 362, 363, 0, 323, 214, 214, 0, 0, 0, 0, 0, 214, 311, 0, 214, 214, 214, 214, 214],\n\trotations = [0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 2, 3, 3, 0, 2, 0, 3, 0, 0, 3, 0, 2, 0, 0, 0, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 1, 0, 2, 1, 2, 0, 0, 0, 0, 2, 1, 2, 0, 3, 3, 3, 3, 1],\n\tlogicGates = {\n},\n\twire = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");

        this.addInventory(new LootTable(AphLootTables.runeInventorHouse), random, 3, 1);

        this.addCustomApply(0, 7, 2, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    String name;
                    if (BiomeRegistry.defaultSpawnIslandFilter.test(new Point(level.getIslandX(), level.getIslandY()))) {
                        name = "Aritz";
                    } else {
                        name = HumanMob.getRandomName(random, random.getOneOf(HumanMob.maleNames, HumanMob.femaleNames, HumanMob.neutralNames, HumanMob.elderNames));
                    }
                    ((SignObjectEntity) objEnt).setText(name + "'s Rune Workshop");
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            } catch (Exception err) {
                System.err.println(err.getMessage());
            }

            return null;
        });

        this.addCustomApply(5, 3, 0, (level, levelX, levelY, dir, blackboard) -> {
            ObjectRegistry.getObject("candle").placeObjectOnFirstValidLayer(level, levelX, levelY, 0, false, false);
            return null;
        });

        this.addCanApplyRectPredicate(-edgeSpace, -edgeSpace, this.width + edgeSpace * 2, this.height + edgeSpace * 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (level.isLiquidTile(x, y) || level.getObject(x, y).getStringID().contains("fence")) {
                        return false;
                    }
                    String tileStringID = level.getTile(x, y).getStringID();
                    if (!(tileStringID.contains("grass") || tileStringID.contains("snow") || tileStringID.contains("sand"))) {
                        return false;
                    }
                }
            }

            return true;
        });

    }
}
