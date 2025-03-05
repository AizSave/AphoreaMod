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
        // Arrows
        ProjectileRegistry.registerProjectile("gelarrow", GelArrowProjectile.class, "gelarrow", "gelarrow_shadow");
        ProjectileRegistry.registerProjectile("unstablegelarrow", UnstableGelArrowProjectile.class, "unstablegelarrow", "unstablegelarrow_shadow");

        // Bullets
        ProjectileRegistry.registerProjectile("spambullet", SpamBulletProjectile.class, "spambullet", "ball_shadow");

        ProjectileRegistry.registerProjectile("gel", GelProjectile.class, "gel", "ball_shadow");
        ProjectileRegistry.registerProjectile("unstablegel", UnstableGelProjectile.class, "unstablegel", "unstablegel_shadow");
        ProjectileRegistry.registerProjectile("copperaircut", AircutProjectile.CopperAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("ironaircut", AircutProjectile.IronAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("goldaircut", AircutProjectile.GoldAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("demonicaircut", AircutProjectile.DemonicAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("unstablegelaircut", AircutProjectile.UnstableGelAircutProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("slingstone", SlingStoneProjectile.class, "slingstone", "ball_shadow");
        ProjectileRegistry.registerProjectile("slingfirestone", FireSlingStoneProjectile.class, "slingfirestone", "ball_shadow");
        ProjectileRegistry.registerProjectile("slingfrozenstone", FrozenSlingStoneProjectile.class, "slingfrozenstone", "ball_shadow");
        ProjectileRegistry.registerProjectile("unstablegelveline", UnstableGelvelineProjectile.class, "unstablegelveline", "unstablegelveline_shadow");
        ProjectileRegistry.registerProjectile("voidstone", VoidStoneProjectile.class, "voidstone", "voidstone_shadow");
        ProjectileRegistry.registerProjectile("woodenwand", WoodenWandProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("goldenwand", GoldenWandProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("copperdagger", DaggerProjectile.CopperDaggerProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("irondagger", DaggerProjectile.IronDaggerProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("golddagger", DaggerProjectile.GoldDaggerProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("demonicdagger", DaggerProjectile.DemonicDaggerProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("tungstendagger", DaggerProjectile.TungstenDaggerProjectile.class, "none", "none");

        // Projectiles [Mobs]
        ProjectileRegistry.registerProjectile("pinkwitch", PinkWitchProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("rockygelslime", RockyGelSlimeProjectile.class, "stone", "stone_shadow");
        ProjectileRegistry.registerProjectile("rockygelslimeloot", RockyGelSlimeLootProjectile.class, "rockygel", "ball_shadow");
        ProjectileRegistry.registerProjectile("spinelgolembeam", SpinelGolemBeamProjectile.class, "rockygel", "ball_shadow");

        // Projectiles [Bosses]
        ProjectileRegistry.registerProjectile("miniunstablegelslime", MiniUnstableGelSlimeProjectile.class, "miniunstablegelslime", "none");

        // Projectiles [Runes]
        ProjectileRegistry.registerProjectile("runeofcryoqueen", RuneOfCryoQueenProjectile.class, "none", "none");
        ProjectileRegistry.registerProjectile("runeofspiderempress", RuneOfSpiderEmpressProjectile.class, "webball", "webball_shadow");
    }
}
