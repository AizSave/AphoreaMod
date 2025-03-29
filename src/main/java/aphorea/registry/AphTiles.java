package aphorea.registry;

import aphorea.tiles.GelTile;
import aphorea.tiles.InfectedGrassTile;
import aphorea.tiles.InfectedWaterTile;
import aphorea.utils.AphColors;
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.CrystalGravelTile;

public class AphTiles {
    public static void registerCore() {
        TileRegistry.registerTile("geltile", new GelTile("geltile", AphColors.gel), -1.0F, true);
        TileRegistry.registerTile("spinelgravel", new CrystalGravelTile("spinelgravel", AphColors.spinel_dark), 10.0F, true);

        liquids();
        grass();
    }

    public static void liquids() {
        TileRegistry.registerTile("infectedwatertile", new InfectedWaterTile(), 20.0F, true);
    }

    public static void grass() {
        TileRegistry.registerTile("infectedgrasstile", new InfectedGrassTile(), 0.0F, false);
    }
}
