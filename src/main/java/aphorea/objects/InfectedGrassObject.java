package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.gameObject.SwampGrassObject;
import necesse.level.maps.Level;

import java.awt.*;

public class InfectedGrassObject extends GrassObject {
    public InfectedGrassObject() {
        super("infectedgrass", 1);
        this.mapColor = AphColors.infected_dark;
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        } else {
            return new LootTable(new ChanceLootItem(0.01F, "infectedgrassseed"));
        }
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        } else if (byPlayer && level.getTile(x, y).isOrganic) {
            return null;
        } else {
            return level.getTileID(x, y) != TileRegistry.getTileID("infectedgrasstile") ? "wrongtile" : null;
        }
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (!super.isValid(level, layerID, x, y)) {
            return false;
        } else if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return false;
        } else if (level.objectLayer.isPlayerPlaced(layerID, x, y) && level.getTile(x, y).isOrganic) {
            return true;
        } else {
            int tileID = level.getTileID(x, y);
            return tileID == TileRegistry.swampRockID || tileID == TileRegistry.swampGrassID || tileID == TileRegistry.overgrownSwampGrassID;
        }
    }
}
