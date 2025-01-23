package aphorea.utils;

import aphorea.items.weapons.magic.MagicalBroom;
import aphorea.mobs.bosses.MiniUnstableGelSlime;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.GelSlime;
import aphorea.mobs.hostile.PinkWitch;
import aphorea.mobs.hostile.RockyGelSlime;
import aphorea.mobs.hostile.VoidAdept;
import aphorea.mobs.hostile.classes.DaggerGoblin;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.Onyx;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.projectiles.toolitem.FireSlingStoneProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.HumanTexture;
import necesse.gfx.gameTexture.GameTexture;

public class AphResources {
    public static void initResources() {
        // MOBS
        mobResources();

        // ITEMS
        MagicalBroom.worldTexture = GameTexture.fromFile("worlditems/magicalbroom");

        // PROJECTILES
        projectileResources();

        // PARTICLES
        GelProjectile.GelProjectileParticle.texture = GameTexture.fromFile("particles/gelprojectile");
    }

    public static void mobResources() {
        // HOSTILE
        GelSlime.texture = GameTexture.fromFile("mobs/gelslime");
        RockyGelSlime.texture = GameTexture.fromFile("mobs/rockygelslime");
        PinkWitch.texture = GameTexture.fromFile("mobs/pinkwitch");
        VoidAdept.texture = MobRegistry.Textures.humanTexture("voidadept");
        DaggerGoblin.humanTexture = new HumanTexture(GameTexture.fromFile("mobs/daggergoblin"), null, null);

        // BOSSES
        UnstableGelSlime.texture = GameTexture.fromFile("mobs/unstablegelslime");
        UnstableGelSlime.icon = GameTexture.fromFile("mobs/icons/unstablegelslime");
        MiniUnstableGelSlime.texture = GameTexture.fromFile("mobs/miniunstablegelslime");

        // FRIENDLY
        WildPhosphorSlime.texture = GameTexture.fromFile("mobs/phosphorslime");
        WildPhosphorSlime.texture_scared = GameTexture.fromFile("mobs/phosphorslime_scared");

        // SUMMONS
        BabyUnstableGelSlime.texture = GameTexture.fromFile("mobs/babyunstablegelslime");
        VolatileGelSlime.texture = GameTexture.fromFile("mobs/volatilegelslime");
        Onyx.texture = GameTexture.fromFile("mobs/onyx");

        // PETS
        PetPhosphorSlime.texture = GameTexture.fromFile("mobs/phosphorslime");
        PetPhosphorSlime.texture_scared = GameTexture.fromFile("mobs/phosphorslime_scared");
    }

    private static void projectileResources() {
        FireSlingStoneProjectile.texture_2 = GameTexture.fromFile("projectiles/slingfirestone_2");

        AircutProjectile.CopperAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutcopper");
        AircutProjectile.IronAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutiron");
        AircutProjectile.GoldAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutgold");
        AircutProjectile.UnstableGelAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutunstablegel");
        AircutProjectile.DemonicAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutdemonic");

        DaggerProjectile.CopperDaggerProjectile.texture = GameTexture.fromFile("player/weapons/copperdagger");
        DaggerProjectile.IronDaggerProjectile.texture = GameTexture.fromFile("player/weapons/irondagger");
        DaggerProjectile.GoldDaggerProjectile.texture = GameTexture.fromFile("player/weapons/golddagger");
        DaggerProjectile.DemonicDaggerProjectile.texture = GameTexture.fromFile("player/weapons/demonicdagger");
        DaggerProjectile.TungstenDaggerProjectile.texture = GameTexture.fromFile("player/weapons/tungstendagger");
    }

}
