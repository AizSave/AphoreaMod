package aphorea.presets.worldpresets;

import aphorea.objects.InfectedTrialEntranceObject;
import aphorea.registry.AphLootTables;
import aphorea.registry.AphTiles;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.PresetUtils;

import java.awt.*;

public class InfectedForestCaveWorldPreset extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;

    public InfectedForestCaveWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion) {
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.presetsPerRegion = presetsPerRegion;
    }

    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(this.levelIdentifier) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = getTotalBiomePoints(random, presetsRegion, this.biome, this.presetsPerRegion);

        for (int i = 0; i < total; ++i) {
            final Dimension size = new Dimension(random.getIntBetween(14, 20), random.getIntBetween(14, 20));
            Point tile = findRandomBiomePresetTile(random, presetsRegion, generatorStack, this.biome, 20, size, "minibiomes",
                    (tileX, tileY) -> !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2)
            );

            if (tile != null) {
                final LinesGenerationWorldPreset lg = (new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2)).addRandomArms(random, 4, 4.0F, 7.0F, 4.0F, 6.0F);
                if (lg.isWithinPresetRegionBounds(presetsRegion)) {
                    int forestNumber = i;
                    presetsRegion.addPreset(this, lg.getOccupiedTileRectangle(), new String[]{"minibiomes", "loot"}, (random1, level, timer) -> {

                        int tileID = AphTiles.INFECTED_GRASS;
                        GameObject infectedTree = ObjectRegistry.getObject("infectedtree");

                        int centerX = tile.x + size.width / 2 + (size.width % 2 == 1 ? random1.getIntBetween(0, 1) : 0);
                        int centerY = tile.y + size.height / 2 + (size.width % 2 == 1 ? random1.getIntBetween(0, 1) : 0);

                        CellAutomaton ca = lg.doCellularAutomaton(random1);
                        ca.streamAliveOrdered().forEachOrdered((tile1) -> {
                            level.setTile(tile1.x, tile1.y, tileID);
                            level.setObject(tile1.x, tile1.y, 0);
                            if (tile1.x == centerX && tile1.y == centerY) {
                                if (random1.getChance(0.8F)) {
                                    level.setObject(centerX, centerY, ObjectRegistry.getObjectID("barrel"));
                                    AphLootTables.infectedCaveForest.applyToLevel(random1, level.buffManager.getModifier(LevelModifiers.LOOT), level, tile1.x, tile1.y, level);
                                } else {
                                    int trialEntranceID = ObjectRegistry.getObjectID("infectedtrialentrance");
                                    level.setObject(centerX, centerY, trialEntranceID);
                                    ObjectEntity objectEntity = level.entityManager.getObjectEntity(centerX, centerY);
                                    if (objectEntity instanceof InfectedTrialEntranceObject.InfectedTrialEntranceObjectEntity) {
                                        int variousTreasuresLoot = random1.getIntBetween(0, 1);
                                        for (int j = 0; j < 2; ++j) {
                                            LootTable lootTable;
                                            if (j == variousTreasuresLoot) {
                                                lootTable = new LootTable(
                                                        AphLootTables.infectedCaveVariousTreasures,
                                                        AphLootTables.infectedCaveForest
                                                );
                                            } else {
                                                lootTable = new LootTable(
                                                        AphLootTables.infectedCaveForestVariousTreasures,
                                                        AphLootTables.infectedCaveForest
                                                );
                                            }
                                            ((InfectedTrialEntranceObject.InfectedTrialEntranceObjectEntity) objectEntity).addLootList(lootTable.getNewList(random, level.buffManager.getModifier(LevelModifiers.LOOT), forestNumber));
                                        }
                                    }
                                }
                            }
                        });


                        ca.streamAliveOrdered().forEachOrdered((tile1) -> {
                            if (level.getObjectID(tile1.x, tile1.y) == 0 && level.getObjectID(tile1.x - 1, tile1.y) == 0 && level.getObjectID(tile1.x + 1, tile1.y) == 0 && level.getObjectID(tile1.x, tile1.y - 1) == 0 && level.getObjectID(tile1.x, tile1.y + 1) == 0 && random1.getChance(0.08F)) {
                                int rotation = random1.nextInt(4);
                                Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                                if (level.getRelativeAnd(tile1.x, tile1.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> ca.isAlive(tileX, tileY) && level.getObjectID(tileX, tileY) == 0)) {
                                    infectedTree.placeObject(level, tile1.x, tile1.y, rotation, false);
                                }
                            }

                        });

                        ca.spawnMobs(level, random1, "infectedtreant", 3, 45, 2, 5);
                    });
                }
            }
        }
    }
}
