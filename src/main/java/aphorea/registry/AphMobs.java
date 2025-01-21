package aphorea.registry;

import aphorea.mobs.bosses.MiniUnstableGelSlime;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.*;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.runicsummons.RunicBat;
import aphorea.mobs.runicsummons.RunicUnstableGelSlime;
import aphorea.mobs.runicsummons.RunicVultureHatchling;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.Onyx;
import aphorea.mobs.summon.UndeadSkeleton;
import aphorea.mobs.summon.VolatileGelSlime;
import necesse.engine.registries.MobRegistry;

public class AphMobs {
    public static void registerCore() {
        MobRegistry.registerMob("gelslime", GelSlime.class, true);
        MobRegistry.registerMob("rockygelslime", RockyGelSlime.class, true);
        MobRegistry.registerMob("pinkwitch", PinkWitch.class, true);
        MobRegistry.registerMob("voidadept", VoidAdept.class, true);
        MobRegistry.registerMob("wildphosphorslime", WildPhosphorSlime.class, true);
        MobRegistry.registerMob("copperdaggergoblin", CopperDaggerGoblin.class, true);
        MobRegistry.registerMob("irondaggergoblin", IronDaggerGoblin.class, true);
        MobRegistry.registerMob("golddaggergoblin", GoldDaggerGoblin.class, true);

        // Bosses [Mobs]
        MobRegistry.registerMob("unstablegelslime", UnstableGelSlime.class, true, true);
        MobRegistry.registerMob("miniunstablegelslime", MiniUnstableGelSlime.class, true);

        // Summons [Mobs]
        MobRegistry.registerMob("babyunstablegelslime", BabyUnstableGelSlime.class, false);
        MobRegistry.registerMob("volatilegelslime", VolatileGelSlime.class, false);
        MobRegistry.registerMob("undeadskeleton", UndeadSkeleton.class, false);
        MobRegistry.registerMob("onyx", Onyx.class, false);

        // Runic Summons [Mobs]
        MobRegistry.registerMob("runicunstablegelslime", RunicUnstableGelSlime.class, false);
        MobRegistry.registerMob("runicvulturehatchling", RunicVultureHatchling.class, false);
        MobRegistry.registerMob("runicbat", RunicBat.class, false);

        // Pets [Mobs]
        MobRegistry.registerMob("petphosphorslime", PetPhosphorSlime.class, false);
    }
}
