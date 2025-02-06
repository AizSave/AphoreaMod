package aphorea.biomes.levels;

import aphorea.biomes.presets.InfectedLootLake;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.PresetUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public class InfectedCaveLevel extends InfectedSurfaceLevel {
    public InfectedCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public InfectedCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.biome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "rocktile", "gelrock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> cg.generateLevel());
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.1F, 2, 2.0F, 10.0F, 2.0F, 10.0F, TileRegistry.getTileID("infectedwatertile"), 1.0F, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("infectedrubyclustersmall"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("infectedrubyclusterpure"), 0.004F);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {

        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        GameObject crystalClusterSmall = ObjectRegistry.getObject("infectedrubyclustersmall");
        GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.001F, 4, 20.0F, 40.0F, 10.0F, 20.0F, (lg) -> {
            CellAutomaton ca = lg.doCellularAutomaton(cg.random);
            ca.streamAliveOrdered().forEachOrdered((tile) -> {
                cg.addIllegalCrateTile(tile.x, tile.y);
                this.setTile(tile.x, tile.y, TileRegistry.getTileID("rubygravel"));
                this.setObject(tile.x, tile.y, 0);
            });
            ca.streamAliveOrdered().forEachOrdered((tile) -> {
                if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08F)) {
                    int rotation = cg.random.nextInt(4);
                    Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                    if (this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> ca.isAlive(tileX, tileY) && this.getObjectID(tileX, tileY) == 0)) {
                        ObjectRegistry.getObject(ObjectRegistry.getObjectID("infectedrubycluster")).placeObject(this, tile.x, tile.y, rotation, false);
                    }
                }

                if (cg.random.getChance(0.3F) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                    crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                }

            });
        });
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), (e) -> {
            presets.findRandomValidPositionAndApply(cg.random, 200, new InfectedLootLake(cg.random), 40, false, false);

        });

        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);

    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
    }

    @Override
    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        Stream<ModifierValue<?>> modifiers = Stream.concat(
                super.getMobModifiers(mob),
                Stream.of(new ModifierValue<>(BuffModifiers.BLINDNESS, 0.6F))
        );
        return modifiers;
    }
}
