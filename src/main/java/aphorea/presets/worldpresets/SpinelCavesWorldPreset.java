package aphorea.presets.worldpresets;

import aphorea.registry.AphBiomes;
import aphorea.registry.AphTiles;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.BiomeCenterWorldPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.presets.PresetUtils;

import java.awt.*;
import java.util.Objects;
import java.util.function.BiPredicate;

public class SpinelCavesWorldPreset extends BiomeCenterWorldPreset {
    public Dimension size = new Dimension(40, 40);

    public SpinelCavesWorldPreset() {
        super(AphBiomes.INFECTED_FIELDS);
        this.randomAttempts = 50;
        this.sectionMaxRegionCount = 625;
        this.sectionMinRegionCount = 36;
    }

    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER);
    }

    public boolean isValidSectionRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return true;
    }

    public boolean isValidFinalRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        int startTileX = centerTileX - this.size.width / 2;
        int startTileY = centerTileY - this.size.height / 2;
        return this.runCornerCheck(startTileX, startTileY, this.size.width, this.size.height, (tileX, tileY) -> generatorStack.getLazyBiomeID(tileX, tileY) == this.biome.getID());
    }

    public void onFoundRegion(int regionX, int regionY, GameRandom random, final LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        final int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        final int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        final float centerRange = 15.5F;

        float minRange, maxRange, minWidth, maxWidth;
        int veinType = random.getIntBetween(0, 2);
        switch (veinType) {
            case 0: // Wide
                minRange = 20F;
                maxRange = 40F;
                minWidth = 20F;
                maxWidth = 40F;
                break;
            case 1: // Medium
                minRange = 40F;
                maxRange = 80F;
                minWidth = 10F;
                maxWidth = 20F;
                break;
            case 2: // Large
                minRange = 80F;
                maxRange = 120F;
                minWidth = 5F;
                maxWidth = 10F;
                break;
            default:
                minRange = 0F;
                maxRange = 0F;
                minWidth = 0F;
                maxWidth = 0F;
        }


        LinesGeneration lg = new LinesGeneration(centerTileX, centerTileY, centerRange);
        BiPredicate<LinesGeneration, Integer> isValidArm = (arm, padding) ->
                SpinelCavesWorldPreset.this.isTileWithinBounds(arm.x2, arm.y2, presetsRegion, padding)
                        && generatorStack.getLazyBiomeID(arm.x2, arm.y2) == SpinelCavesWorldPreset.this.biome.getID();
        int armAngle = random.nextInt(360);
        int arms = random.getIntBetween(3, 6);
        int anglePerArm = 360 / arms;

        int startTileX;
        for (startTileX = 0; startTileX < arms; ++startTileX) {
            armAngle += anglePerArm;
            LinesGeneration lastArm = lg.addMultiArm(random, armAngle, 15, random.getIntBetween(150, 200), minRange, maxRange, minWidth, maxWidth, (armLG) -> !isValidArm.test(armLG, 15));
            if (!isValidArm.test(lastArm, 10)) {
                lg.removeLastLine();
            }
        }

        int x;
        if (!lg.getRoot().isEmpty()) {
            CellAutomaton ca = Performance.record(performanceTimer, "doCellularAutomaton", () -> lg.doCellularAutomaton(random));
            Objects.requireNonNull(ca);
            Performance.record(performanceTimer, "cleanHardEdges", ca::cleanHardEdges);

            int y;
            for (x = centerTileX - (int) Math.floor(centerRange); (double) x <= (double) centerTileX + Math.ceil(centerRange); ++x) {
                for (y = centerTileY - (int) Math.floor(centerRange); (double) y <= (double) centerTileY + Math.ceil(centerRange); ++y) {
                    if (GameMath.getExactDistance((float) centerTileX, (float) centerTileY, (float) x, (float) y) <= centerRange) {
                        ca.setAlive(x, y);
                    }
                }
            }

            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            Performance.record(performanceTimer, "addTileGenerator", () ->
                    ca.streamAliveOrdered().forEach((tile) ->
                            tileGenerator.addTile(tile.x, tile.y, (random1, level, tileX, tileY, timer) ->
                                    Performance.record(timer, "setTile", () -> {
                                        level.setTile(tileX, tileY, AphTiles.SPINEL_GRAVEL);

                                        if (tileX == centerTileX && tileY == centerTileY) {
                                            ObjectRegistry.getObject("babylontower").placeObject(level, tileX - 1, tileY - 1, 2, false);
                                            level.setObject(tileX, tileY, ObjectRegistry.getObjectID("barrel"));
                                        }

                                        if (!level.getObject(tileX, tileY).getStringID().startsWith("babylontower"))
                                            level.setObject(tileX, tileY, 0);
                                    }))));

            GameObject crystalClusterSmall = ObjectRegistry.getObject("spinelclustersmall");
            GameObject crystalClusterBig = ObjectRegistry.getObject("spinelcluster");
            GameObject fakeChest = ObjectRegistry.getObject("fakespinelchest");

            ca.streamAliveOrdered().forEachOrdered((tile) ->
                    tileGenerator.addTile(tile.x, tile.y, (random1, level, tileX, tileY, timer) -> {
                        if (level.getObjectID(tile.x, tile.y) == 0 && level.getObjectID(tile.x - 1, tile.y) == 0 && level.getObjectID(tile.x + 1, tile.y) == 0 && level.getObjectID(tile.x, tile.y - 1) == 0 && level.getObjectID(tile.x, tile.y + 1) == 0 && random.getChance(0.06F)) {
                            if (random.getChance(0.0167F)) {
                                fakeChest.placeObject(level, tile.x, tile.y, 2, false);
                            } else {
                                int rotation = random.nextInt(4);
                                Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                                if (level.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX1, tileY1) -> ca.isAlive(tileX1, tileY1) && level.getObjectID(tileX1, tileY1) == 0)) {
                                    crystalClusterBig.placeObject(level, tile.x, tile.y, rotation, false);
                                }
                            }
                        }

                        if (random.getChance(0.2F) && crystalClusterSmall.canPlace(level, tile.x, tile.y, 0, false) == null) {
                            crystalClusterSmall.placeObject(level, tile.x, tile.y, 0, false);
                        }
                    }));


            tileGenerator.forEachRegion((regionX1, regionY1, placeFunction) -> {
                int tileX = GameMath.getTileCoordByRegion(regionX1);
                int tileY = GameMath.getTileCoordByRegion(regionY1);
                presetsRegion.addPreset(SpinelCavesWorldPreset.this, tileX, tileY, new Dimension(16, 16), new String[]{"minibiomes", "loot"}, placeFunction);
            });
        }

    }
}
