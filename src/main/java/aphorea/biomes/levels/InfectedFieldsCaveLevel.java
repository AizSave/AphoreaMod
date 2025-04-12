package aphorea.biomes.levels;

import aphorea.biomes.presets.InfectedLootLake;
import aphorea.registry.AphLootTables;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.InventoryObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.*;
import necesse.level.maps.presets.PresetUtils;

import java.awt.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class InfectedFieldsCaveLevel extends InfectedFieldsSurfaceLevel {
    public InfectedFieldsCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public InfectedFieldsCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.biome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "rocktile", "gelrock");

        Point veinCenter = new Point(cg.random.getIntOffset(this.width / 2, 32), cg.random.getIntOffset(this.height / 2, 32));

        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> cg.generateLevel());
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));

        GameObject crystalClusterSmall = ObjectRegistry.getObject("spinelclustersmall");
        GameObject infectedGrass = ObjectRegistry.getObject("infectedgrass");
        InventoryObject barrel = (InventoryObject) ObjectRegistry.getObject("barrel");

        float minRange, maxRange, minWidth, maxWidth;
        int veinType = cg.random.getIntBetween(0, 2);
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

        Consumer<LinesGeneration> veinGeneration = (lg) -> {
            CellAutomaton ca = lg.doCellularAutomaton(cg.random);
            ca.streamAliveOrdered().forEachOrdered((tile) -> {
                cg.addIllegalCrateTile(tile.x, tile.y);
                this.setTile(tile.x, tile.y, TileRegistry.getTileID("spinelgravel"));
                this.setObject(tile.x, tile.y, 0);
            });
            ca.streamAliveOrdered().forEachOrdered((tile) -> {
                if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08F)) {
                    boolean isChest = cg.random.getChance(0.05F);
                    int rotation = isChest ? 2 : cg.random.nextInt(4);
                    Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                    if (this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> ca.isAlive(tileX, tileY) && this.getObjectID(tileX, tileY) == 0)) {
                        ObjectRegistry.getObject(ObjectRegistry.getObjectID(isChest ? "fakespinelchest" : "spinelcluster")).placeObject(this, tile.x, tile.y, rotation, false);
                    }
                }

                if (cg.random.getChance(0.3F) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                    crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                }

            });
        };

        veinGeneration.accept((new LinesGeneration(veinCenter.x, veinCenter.y)).addRandomArms(cg.random, cg.random.getIntBetween(3, 6), minRange, maxRange, minWidth, maxWidth));

        GameObject airObject = ObjectRegistry.getObject("air");
        airObject.placeObject(this, veinCenter.x - 1, veinCenter.y - 1, 0, false);
        airObject.placeObject(this, veinCenter.x, veinCenter.y - 1, 0, false);
        airObject.placeObject(this, veinCenter.x + 1, veinCenter.y - 1, 0, false);
        airObject.placeObject(this, veinCenter.x - 1, veinCenter.y, 0, false);
        airObject.placeObject(this, veinCenter.x, veinCenter.y, 0, false);
        airObject.placeObject(this, veinCenter.x + 1, veinCenter.y, 0, false);

        int spinelGravel = TileRegistry.getTile("spinelgravel").getID();
        this.setTile(veinCenter.x - 2, veinCenter.y - 2, spinelGravel);
        this.setTile(veinCenter.x - 2, veinCenter.y - 2, spinelGravel);
        this.setTile(veinCenter.x, veinCenter.y - 2, spinelGravel);
        this.setTile(veinCenter.x + 1, veinCenter.y - 2, spinelGravel);
        this.setTile(veinCenter.x + 2, veinCenter.y - 2, spinelGravel);

        this.setTile(veinCenter.x - 2, veinCenter.y - 1, spinelGravel);
        this.setTile(veinCenter.x - 1, veinCenter.y - 1, spinelGravel);
        this.setTile(veinCenter.x, veinCenter.y - 1, spinelGravel);
        this.setTile(veinCenter.x + 1, veinCenter.y - 1, spinelGravel);
        this.setTile(veinCenter.x + 2, veinCenter.y - 1, spinelGravel);

        this.setTile(veinCenter.x - 2, veinCenter.y, spinelGravel);
        this.setTile(veinCenter.x - 1, veinCenter.y, spinelGravel);
        this.setTile(veinCenter.x, veinCenter.y, spinelGravel);
        this.setTile(veinCenter.x + 1, veinCenter.y, spinelGravel);
        this.setTile(veinCenter.x + 2, veinCenter.y, spinelGravel);

        this.setTile(veinCenter.x - 2, veinCenter.y + 1, spinelGravel);
        this.setTile(veinCenter.x - 1, veinCenter.y + 1, spinelGravel);
        this.setTile(veinCenter.x, veinCenter.y + 1, spinelGravel);
        this.setTile(veinCenter.x + 1, veinCenter.y + 1, spinelGravel);
        this.setTile(veinCenter.x + 2, veinCenter.y + 1, spinelGravel);

        ObjectRegistry.getObject("babylontower").placeObject(this, veinCenter.x - 1, veinCenter.y - 1, 0, false);

        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.005F, 4, 4.0F, 7.0F, 4.0F, 6.0F, (lg) -> {
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);

                ca.streamAliveOrdered().forEachOrdered((tile) -> {
                    cg.addIllegalCrateTile(tile.x, tile.y);
                    this.setTile(tile.x, tile.y, TileRegistry.getTileID("spinelgravel"));
                    this.setObject(tile.x, tile.y, 0);
                });


                int centerX = lg.x1 + (lg.x2 - lg.x1) / 2;
                int centerY = lg.y1 + (lg.y2 - lg.y1) / 2;

                ObjectRegistry.getObject(ObjectRegistry.getObjectID("fakespinelchest")).placeObject(this, centerX, centerY, 2, false);

                ca.streamAliveOrdered().forEachOrdered((tile) -> {
                    if (Math.abs(centerX - tile.x) > 1 && Math.abs(centerY - tile.y) > 1) {
                        if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08F)) {
                            int rotation = cg.random.nextInt(4);
                            Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                            if (this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> ca.isAlive(tileX, tileY) && this.getObjectID(tileX, tileY) == 0)) {
                                ObjectRegistry.getObject(ObjectRegistry.getObjectID("spinelcluster")).placeObject(this, tile.x, tile.y, rotation, false);
                            }
                        }

                        if (cg.random.getChance(0.3F) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                            crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                        }
                    }
                });
            });

            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.008F, 4, 6.0F, 9.0F, 6.0F, 8.0F, (lg) -> {
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);

                ca.streamAliveOrdered().forEachOrdered((tile) -> {
                    cg.addIllegalCrateTile(tile.x, tile.y);
                    this.setTile(tile.x, tile.y, TileRegistry.getTileID("infectedgrasstile"));
                    this.setObject(tile.x, tile.y, 0);
                });

                int centerX = lg.x1 + (lg.x2 - lg.x1) / 2;
                int centerY = lg.y1 + (lg.y2 - lg.y1) / 2;

                barrel.placeObject(this, centerX, centerY, 2, false);
                AphLootTables.infectedCaveForest.applyToLevel(cg.random, 1, this, centerX, centerY);

                ca.streamAliveOrdered().forEachOrdered((tile) -> {
                    if (Math.abs(centerX - tile.x) > 1 && Math.abs(centerY - tile.y) > 1) {
                        if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.12F)) {
                            int rotation = cg.random.nextInt(4);
                            Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)};
                            if (this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> ca.isAlive(tileX, tileY) && this.getObjectID(tileX, tileY) == 0)) {
                                ObjectRegistry.getObject(ObjectRegistry.getObjectID("infectedtree")).placeObject(this, tile.x, tile.y, rotation, false);
                            }
                            if (cg.random.getChance(0.3F) && infectedGrass.canPlace(this, tile.x, tile.y, 0, false) == null) {
                                infectedGrass.placeObject(this, tile.x, tile.y, 0, false);
                            }
                        }
                    }
                });

                ca.spawnMobs(this, cg.random, "infectedtreant", 25, 45, 1, 2);
            });

            GameTile waterTile = TileRegistry.getTile("infectedwatertile");
            GenerationTools.generateRandomSmoothVeins(this, cg.random, 0.1F, 2, 2.0F, 10.0F, 2.0F, 10.0F, (l, tileX, tileY) -> {
                if (cg.random.getChance(1.0F) && this.getTile(tileX, tileY).getID() == cg.rockTile) {
                    waterTile.placeTile(l, tileX, tileY, false);
                }
            });

            this.liquidManager.calculateShores();

            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    GameTile tile = this.getTile(x, y);
                    if (tile.getID() == cg.rockTile && this.getObject(x + 1, y).getID() == 0) {
                        if (!tile.isLiquid && cg.random.getChance(0.005F)) {
                            GameObject rock = ObjectRegistry.getObject("spinelcluster");
                            if (rock.canPlace(this, x, y, 0, false) == null) {
                                rock.placeObject(this, x, y, 0, false);
                            }
                        } else if (cg.random.getChance(0.02F)) {
                            GameObject rock = ObjectRegistry.getObject("spinelclustersmall");
                            if (rock.canPlace(this, x, y, 0, false) == null) {
                                rock.placeObject(this, x, y, 0, false);
                            }
                        }
                    }
                }
            }
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateOreVeins(0.2F, 3, 6, ObjectRegistry.getObjectID("tungstenoregelrock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));

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
        return Stream.concat(
                super.getMobModifiers(mob),
                Stream.of(new ModifierValue<>(BuffModifiers.BLINDNESS, 0.6F))
        );
    }
}
