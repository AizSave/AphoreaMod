package aphorea.presets.worldpresets;

import aphorea.registry.AphTiles;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.presets.PresetUtils;

import java.awt.*;

public class SpinelFakeChestWorldPreset extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;

    public SpinelFakeChestWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion) {
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
            final Dimension size = new Dimension(16, 16);
            Point tile = findRandomBiomePresetTile(random, presetsRegion, generatorStack, this.biome, 20, size, "minibiomes",
                    (tileX, tileY) -> !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2)
            );

            if (tile != null) {
                final LinesGenerationWorldPreset lg = (new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2)).addRandomArms(random, 4, 4.0F, 7.0F, 4.0F, 6.0F);
                if (lg.isWithinPresetRegionBounds(presetsRegion)) {
                    presetsRegion.addPreset(this, lg.getOccupiedTileRectangle(), "minibiomes", (random1, level, timer) -> {
                        int gravelTileID = AphTiles.SPINEL_GRAVEL;
                        GameObject crystalClusterSmall = ObjectRegistry.getObject("spinelclustersmall");
                        GameObject crystalClusterBig = ObjectRegistry.getObject("spinelcluster");
                        CellAutomaton ca = lg.doCellularAutomaton(random1);

                        int centerX = tile.x + size.width / 2;
                        int centerY = tile.y + size.height / 2;

                        ca.streamAliveOrdered().forEachOrdered((tile1) -> {
                            level.setTile(tile1.x, tile1.y, gravelTileID);
                            level.setObject(tile1.x, tile1.y, 0);
                            if (tile1.x == centerX && tile1.y == centerY) {
                                level.setObject(tile1.x, tile1.y, ObjectRegistry.getObjectID("fakespinelchest"), 2);
                            }
                        });

                        ca.streamAliveOrdered().forEachOrdered((tile1) -> {
                            if (level.getObjectID(tile1.x, tile1.y) == 0 && level.getObjectID(tile1.x - 1, tile1.y) == 0 && level.getObjectID(tile1.x + 1, tile1.y) == 0 && level.getObjectID(tile1.x, tile1.y - 1) == 0 && level.getObjectID(tile1.x, tile1.y + 1) == 0 && random1.getChance(0.08F)) {
                                int rotation = random1.nextInt(4);
                                Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                                if (level.getRelativeAnd(tile1.x, tile1.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> ca.isAlive(tileX, tileY) && level.getObjectID(tileX, tileY) == 0)) {
                                    crystalClusterBig.placeObject(level, tile1.x, tile1.y, rotation, false);
                                }
                            }

                            if (random1.getChance(0.3F) && crystalClusterSmall.canPlace(level, tile1.x, tile1.y, 0, false) == null) {
                                crystalClusterSmall.placeObject(level, tile1.x, tile1.y, 0, false);
                            }

                        });
                    });
                }
            }
        }

    }
}
