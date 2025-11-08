package aphorea.registry;

import aphorea.tiles.GelTile;
import aphorea.tiles.InfectedGrassTile;
import aphorea.tiles.InfectedWaterTile;
import aphorea.utils.AphColors;
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.CrystalGravelTile;

public class AphTiles {
    public static int INFECTED_GRASS;
    public static int INFECTED_WATER;

    public static int SPINEL_GRAVEL;

    public static void registerCore() {
        TileRegistry.registerTile("geltile", new GelTile("geltile", AphColors.gel), -1.0F, true);
        SPINEL_GRAVEL = TileRegistry.registerTile("spinelgravel", new CrystalGravelTile("spinelgravel", AphColors.spinel_dark), 10.0F, true);

        grass();
        liquids();
    }

    public static void grass() {
        INFECTED_GRASS = TileRegistry.registerTile("infectedgrasstile", new InfectedGrassTile(), 0.0F, false);
    }

    public static void liquids() {
        INFECTED_WATER = TileRegistry.registerTile("infectedwatertile", new InfectedWaterTile(), 20.0F, true);
    }
}
