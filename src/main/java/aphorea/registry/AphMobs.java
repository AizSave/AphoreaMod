package aphorea.registry;

import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.bosses.minions.HearthCrystalMob;
import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
import aphorea.mobs.bosses.minions.babylon.BabylonBody;
import aphorea.mobs.bosses.minions.babylon.BabylonHead;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.*;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.runicsummons.RunicBat;
import aphorea.mobs.runicsummons.RunicUnstableGelSlime;
import aphorea.mobs.runicsummons.RunicVultureHatchling;
import aphorea.mobs.summon.*;
import necesse.engine.registries.MobRegistry;

public class AphMobs {
    public static void registerCore() {
        MobRegistry.registerMob("gelslime", GelSlime.class, true);
        MobRegistry.registerMob("rockygelslime", RockyGelSlime.class, true);
        MobRegistry.registerMob("pinkwitch", PinkWitch.class, true);
        MobRegistry.registerMob("voidadept", VoidAdept.class, true);
        MobRegistry.registerMob("wildphosphorslime", WildPhosphorSlime.class, true);
        MobRegistry.registerMob("copperdaggergoblin", DaggerGoblin.CopperDaggerGoblin.class, true);
        MobRegistry.registerMob("irondaggergoblin", DaggerGoblin.IronDaggerGoblin.class, true);
        MobRegistry.registerMob("golddaggergoblin", DaggerGoblin.GoldDaggerGoblin.class, true);
        MobRegistry.registerMob("infectedtreant", InfectedTreant.class, true);
        MobRegistry.registerMob("spinelgolem", SpinelGolem.class, true);
        MobRegistry.registerMob("spinelcaveling", SpinelCaveling.class, true);
        MobRegistry.registerMob("tungstencaveling", TungstenCaveling.class, true);
        MobRegistry.registerMob("spinelmimic", SpinelMimic.class, true);

        // Bosses [Mobs]
        MobRegistry.registerMob("unstablegelslime", UnstableGelSlime.class, true, true);
        MobRegistry.registerMob("miniunstablegelslime", MiniUnstableGelSlime.class, true);

        MobRegistry.registerMob("babylontower", BabylonTowerMob.class, true, true);
        MobRegistry.registerMob("babylon", BabylonHead.class, false);
        MobRegistry.registerMob("babylonbody", BabylonBody.class, false);

        MobRegistry.registerMob("hearthcrystal", HearthCrystalMob.class, true);

        // Summons [Mobs]
        MobRegistry.registerMob("babyunstablegelslime", BabyUnstableGelSlime.class, false);
        MobRegistry.registerMob("volatilegelslime", VolatileGelSlime.class, false);
        MobRegistry.registerMob("undeadskeleton", UndeadSkeleton.class, false);
        MobRegistry.registerMob("onyx", Onyx.class, false);
        MobRegistry.registerMob("livingsapling", LivingSapling.class, false);

        // Runic Summons [Mobs]
        MobRegistry.registerMob("runicunstablegelslime", RunicUnstableGelSlime.class, false);
        MobRegistry.registerMob("runicvulturehatchling", RunicVultureHatchling.class, false);
        MobRegistry.registerMob("runicbat", RunicBat.class, false);

        // Pets [Mobs]
        MobRegistry.registerMob("petphosphorslime", PetPhosphorSlime.class, false);
    }
}
