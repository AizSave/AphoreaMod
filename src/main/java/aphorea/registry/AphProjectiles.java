package aphorea.registry;

import aphorea.projectiles.arrow.GelArrowProjectile;
import aphorea.projectiles.arrow.UnstableGelArrowProjectile;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.mob.*;
import aphorea.projectiles.rune.RuneOfCryoQueenProjectile;
import aphorea.projectiles.rune.RuneOfSpiderEmpressProjectile;
import aphorea.projectiles.toolitem.*;
import necesse.engine.registries.ProjectileRegistry;

public class AphProjectiles {
    public static void registerCore() {
        ProjectileRegistry.registerProjectile("gel", GelProjectile.class, "gel", "ball_shadow");
        ProjectileRegistry.registerProjectile("unstablegel", UnstableGelProjectile.class, "unstablegel", "unstablegel_shadow");
        ProjectileRegistry.registerProjectile("slingstone", SlingStoneProjectile.class, "slingstone", "ball_shadow");
        ProjectileRegistry.registerProjectile("slingfirestone", FireSlingStoneProjectile.class, "slingfirestone", "ball_shadow");
        ProjectileRegistry.registerProjectile("slingfrozenstone", FrozenSlingStoneProjectile.class, "slingfrozenstone", "ball_shadow");
        ProjectileRegistry.registerProjectile("unstablegelveline", UnstableGelvelineProjectile.class, "unstablegelveline", "unstablegelveline_shadow");
        ProjectileRegistry.registerProjectile("voidstone", VoidStoneProjectile.class, "voidstone", "voidstone_shadow");
        ProjectileRegistry.registerProjectile("woodenwand", WoodenWandProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("goldenwand", GoldenWandProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("spinelwand", SpinelWandProjectile.class, null, null);

        // Arrows
        ProjectileRegistry.registerProjectile("gelarrow", GelArrowProjectile.class, "gelarrow", "gelarrow_shadow");
        ProjectileRegistry.registerProjectile("unstablegelarrow", UnstableGelArrowProjectile.class, "unstablegelarrow", "unstablegelarrow_shadow");

        // Bullets
        ProjectileRegistry.registerProjectile("spambullet", SpamBulletProjectile.class, "spambullet", "ball_shadow");

        // Daggers
        ProjectileRegistry.registerProjectile("copperdagger", DaggerProjectile.CopperDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("irondagger", DaggerProjectile.IronDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("golddagger", DaggerProjectile.GoldDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("demonicdagger", DaggerProjectile.DemonicDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("tungstendagger", DaggerProjectile.TungstenDaggerProjectile.class, null, null);

        // Aircuts
        ProjectileRegistry.registerProjectile("copperaircut", AircutProjectile.CopperAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ironaircut", AircutProjectile.IronAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("goldaircut", AircutProjectile.GoldAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("demonicaircut", AircutProjectile.DemonicAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("unstablegelaircut", AircutProjectile.UnstableGelAircutProjectile.class, null, null);

        // Mobs
        ProjectileRegistry.registerProjectile("pinkwitch", PinkWitchProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("rockygelslime", RockyGelSlimeProjectile.class, "stone", "stone_shadow");
        ProjectileRegistry.registerProjectile("rockygelslimeloot", RockyGelSlimeLootProjectile.class, "rockygel", "ball_shadow");
        ProjectileRegistry.registerProjectile("spinelgolembeam", SpinelGolemBeamProjectile.class, "rockygel", "ball_shadow");

        // Bosses
        ProjectileRegistry.registerProjectile("miniunstablegelslime", MiniUnstableGelSlimeProjectile.class, "miniunstablegelslime", null);

        // Runes
        ProjectileRegistry.registerProjectile("runeofcryoqueen", RuneOfCryoQueenProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("runeofspiderempress", RuneOfSpiderEmpressProjectile.class, "webball", "webball_shadow");
    }
}
