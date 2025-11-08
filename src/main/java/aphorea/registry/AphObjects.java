package aphorea.registry;

import aphorea.objects.*;
import aphorea.utils.AphColors;
import necesse.engine.registries.ObjectRegistry;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.container.BookshelfObject;
import necesse.level.gameObject.container.CabinetObject;
import necesse.level.gameObject.container.StorageBoxInventoryObject;
import necesse.level.gameObject.furniture.BedObject;
import necesse.level.gameObject.furniture.CandelabraObject;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.gameObject.furniture.DinnerTableObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBedBaseObject;

public class AphObjects {
    public static int GEL_ROCK;

    public static void registerCore() {
        ObjectRegistry.registerObject("goldwitchstatue", new GoldWitchStatue(), -1.0F, true);
        ObjectRegistry.registerObject("fakespinelchest", new FakeSpinelChest(), -1.0F, true);
        ObjectRegistry.registerObject("spinelchest", new StorageBoxInventoryObject("spinelchest", 40, AphColors.spinel), 20.0F, true);

        // Trial Rooms
        ObjectRegistry.registerObject("infectedtrialentrance", new InfectedTrialEntranceObject(), 0, false);

        // Tech
        tech();

        // Multi Tile Objects
        multiTileObjects();

        // Biome Objects
        surfaceObjects();
        caveObjects();

        // Walls
        WallObject.registerWallObjects("infectedwood", "infectedwoodwall", 0.0F, AphColors.infected_wood, ToolType.ALL, 2.0F, 6.0F);

        // Rocks
        gelRock();

        // Infected Wood Furniture
        DinnerTableObject.registerDinnerTable("infecteddinnertable", "infecteddinnertable", AphColors.infected_wood, 20.0F);
        ObjectRegistry.registerObject("infectedchair", new ChairObject("infectedchair", AphColors.infected_wood), 5.0F, true);
        ObjectRegistry.registerObject("infectedbookshelf", new BookshelfObject("infectedbookshelf", AphColors.infected_wood), 10.0F, true);
        ObjectRegistry.registerObject("infectedcabinet", new CabinetObject("infectedcabinet", AphColors.infected_wood), 10.0F, true);
        BedObject.registerBed("infectedbed", "infectedbed", AphColors.infected_wood, 100.0F);
        DoubleBedBaseObject.registerDoubleBed("infecteddoublebed", "infecteddoublebed", AphColors.infected_wood, 150.0F);
        ObjectRegistry.registerObject("infectedcandelabra", new CandelabraObject("infectedcandelabra", AphColors.infected_wood, 50.0F, 0.1F), 10.0F, true);
    }

    public static void tech() {
        ObjectRegistry.registerObject("runestable", new RunesTable(), -1.0F, true);
    }

    public static void multiTileObjects() {
        BabylonTowerObject.registerObject();
        BabylonEntranceObject.registerObject();
        BabylonExitObject.registerObject();
    }

    public static void surfaceObjects() {
        ObjectRegistry.registerObject("infectedgrass", new InfectedGrassObject(), -1.0F, true);
        ObjectRegistry.registerObject("infectedsapling", new AphSaplingObject("infectedsapling", "infectedtree", 900, 1800, true, 50, 340F, 0.6F, "infectedgrasstile"), 10.0F, true);
        ObjectRegistry.registerObject("infectedtree", new AphTreeObject("infectedtree", "infectedlog", "infectedsapling", AphColors.infected_dark, 45, 60, 110, "infectedleaves", 100, 340F, 0.6F), 0.0F, false);
    }

    public static void caveObjects() {
        SpinelClusterObject.registerCrystalCluster("spinelcluster", "spinelcluster", AphColors.spinel, 337.0F, -1.0F, true);
        ObjectRegistry.registerObject("spinelclustersmall", new SpinelClusterSmallObject("spinelcluster_small", AphColors.spinel, 337.0F), -1.0F, true);
    }

    public static void gelRock() {
        RockObject gelRock;
        GEL_ROCK = ObjectRegistry.registerObject("gelrock", gelRock = new RockyWallObject("gelrock", AphColors.rock, "rockygel", 0, 1, 1), -1.0F, true);
        gelRock.toolTier = 3;
        SingleRockObject.registerSurfaceRock(gelRock, "surfacegelrock", AphColors.rock_light, 1, 2, 1, -1.0F, true);
        ObjectRegistry.registerObject("tungstenoregelrock", new TungstenGelRockOreObject(gelRock, "oremask", "tungstenore", AphColors.tungsten), -1.0F, true);
    }
}
