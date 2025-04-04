package aphorea.registry;

import aphorea.projectiles.arrow.GelArrowProjectile;
import aphorea.projectiles.arrow.UnstableGelArrowProjectile;
import aphorea.projectiles.bullet.ShotgunBulletProjectile;
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
        ProjectileRegistry.registerProjectile("shotgunbullet", ShotgunBulletProjectile.class, "shotgunbullet", null);

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
        ProjectileRegistry.registerProjectile("bigglacialshard", GlacialShardBigProjectile.class, "glacialshard_big", "glacialshard_big_shadow");
        ProjectileRegistry.registerProjectile("mediumglacialshard", GlacialShardMediumProjectile.class, "glacialshard_medium", "glacialshard_medium_shadow");
        ProjectileRegistry.registerProjectile("smallglacialshard", GlacialShardSmallProjectile.class, "glacialshard_small", "glacialshard_small_shadow");

        // Aircuts
        ProjectileRegistry.registerProjectile("copperaircut", AircutProjectile.CopperAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ironaircut", AircutProjectile.IronAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("goldaircut", AircutProjectile.GoldAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("unstablegelaircut", AircutProjectile.UnstableGelAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("demonicaircut", AircutProjectile.DemonicAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("crimsonaircut", AircutProjectile.CrimsonAircutProjectile.class, null, null);

        // Daggers
        ProjectileRegistry.registerProjectile("copperdagger", DaggerProjectile.CopperDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("irondagger", DaggerProjectile.IronDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("golddagger", DaggerProjectile.GoldDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("demonicdagger", DaggerProjectile.DemonicDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("tungstendagger", DaggerProjectile.TungstenDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("lostumbrelladagger", DaggerProjectile.LostUmbrellaDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("openlostumbrella", OpenLostUmbrellaProjectile.class, "lostumbrella_open", null);

        // Mobs
        ProjectileRegistry.registerProjectile("pinkwitch", PinkWitchProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("rockygelslime", RockyGelSlimeProjectile.class, "stone", "stone_shadow");
        ProjectileRegistry.registerProjectile("rockygelslimeloot", RockyGelSlimeLootProjectile.class, "rockygel", "ball_shadow");
        ProjectileRegistry.registerProjectile("spinelgolembeam", SpinelGolemBeamProjectile.class, "rockygel", "ball_shadow");

        // Bosses
        ProjectileRegistry.registerProjectile("miniunstablegelslime", MiniUnstableGelSlimeProjectile.class, "miniunstablegelslime", null);

        // Projectiles [Runes]
        ProjectileRegistry.registerProjectile("runeofcryoqueen", RuneOfCryoQueenProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("runeofspiderempress", RuneOfSpiderEmpressProjectile.class, "webball", "webball_shadow");
    }
}
