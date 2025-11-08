package aphorea.presets.worldpresets;

import aphorea.presets.RuneInventorHouse;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.PresetUtils;

import java.awt.*;

public class RuneInventorWorldPreset extends WorldPreset {
    protected Dimension size = new Dimension(7, 8);

    public Biome biome;
    public float presetsPerRegion;

    public RuneInventorWorldPreset(float presetsPerRegion, Biome biome) {
        this.biome = biome;
        this.presetsPerRegion = presetsPerRegion;
    }

    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = getTotalBiomePoints(random, presetsRegion, this.biome, this.presetsPerRegion);

        for (int i = 0; i < total; ++i) {
            final Point tile = findRandomPresetTile(random, presetsRegion, 20, this.size, new String[]{"loot", "villages"}, (tileX, tileY) -> RuneInventorWorldPreset.this.runCornerCheck(tileX, tileY, RuneInventorWorldPreset.this.size.width, RuneInventorWorldPreset.this.size.height, (tileX1, tileY1) -> !generatorStack.isSurfaceExpensiveWater(tileX1, tileY1)));
            if (tile != null) {
                presetsRegion.addPreset(this, tile.x, tile.y, this.size, "loot", (random1, level, timer) -> {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, RuneInventorWorldPreset.this.size.width, RuneInventorWorldPreset.this.size.height);
                    RuneInventorHouse preset = new RuneInventorHouse(random1);
                    PresetUtils.clearMobsInPreset(preset, level, tile.x, tile.y);
                    preset.applyToLevel(level, tile.x, tile.y);
                }).setRemoveIfWithinSpawnRegionRange(1);
            }
        }
    }
}
