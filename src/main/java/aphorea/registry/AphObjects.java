package aphorea.registry;

import aphorea.objects.*;
import aphorea.utils.AphColors;
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.SingleRockObject;
import necesse.level.gameObject.furniture.StorageBoxInventoryObject;

public class AphObjects {
    public static void registerCore() {
        ObjectRegistry.registerObject("witchstatue", new WitchStatue(), -1.0F, true);
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

        // Rocks
        gelRock();
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
        ObjectRegistry.registerObject("gelrock", gelRock = new RockyWallObject("gelrock", AphColors.rock, "rockygel", 0, 1, 1), -1.0F, true);
        gelRock.toolTier = 1.5F;
        SingleRockObject.registerSurfaceRock(gelRock, "surfacegelrock", AphColors.rock_light, 1, 2, 1, -1.0F, true);
        ObjectRegistry.registerObject("tungstenoregelrock", new TungstenGelRockOreObject(gelRock, "oremask", "tungstenore", AphColors.tungsten), -1.0F, true);
    }
}
