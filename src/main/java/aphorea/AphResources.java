package aphorea;

import aphorea.items.weapons.magic.MagicalBroom;
import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
import aphorea.mobs.bosses.ThePillarMob;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.*;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.Onyx;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.particles.ThePillarFallingCrystalParticle;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.projectiles.toolitem.FireSlingStoneProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.HumanTexture;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;

import java.io.FileNotFoundException;

public class AphResources {
    public static GameTexture saberAttackTexture;
    public static GameTexture gunAttackTrackTexture;
    public static GameTexture gunAttackThumbTexture;

    public static void initResources() {
        saberAttackTexture = GameTexture.fromFile("ui/saberattack");
        gunAttackTrackTexture = GameTexture.fromFile("ui/gunattacktrack");
        gunAttackThumbTexture = GameTexture.fromFile("ui/gunattackthumb");

        // MOBS
        mobResources();

        // ITEMS
        MagicalBroom.worldTexture = GameTexture.fromFile("worlditems/magicalbroom");

        // PROJECTILES
        projectileResources();

        // PARTICLES
        GelProjectile.GelProjectileParticle.texture = GameTexture.fromFile("particles/gelprojectile");
        SpamBulletProjectile.FirePoolParticle.texture = GameTexture.fromFile("particles/firepool");
        ThePillarFallingCrystalParticle.projectileTexture = GameTexture.fromFile("particles/thepillarfallingcrystalparticle");
        ThePillarFallingCrystalParticle.shadowTexture = GameTexture.fromFile("particles/thepillarfallingcrystalparticle_shadow");
    }


    public static void mobResources() {
        // HOSTILE
        GelSlime.texture = GameTexture.fromFile("mobs/gelslime");
        RockyGelSlime.texture = GameTexture.fromFile("mobs/rockygelslime");
        PinkWitch.texture = GameTexture.fromFile("mobs/pinkwitch");
        VoidAdept.texture = MobRegistry.Textures.humanTexture("voidadept");
        DaggerGoblin.humanTexture = new HumanTexture(GameTexture.fromFile("mobs/daggergoblin"), null, null);
        InfectedTreant.texture = GameTexture.fromFile("mobs/infectedtreant");
        InfectedTreant.texture_shadow = GameTexture.fromFile("mobs/infectedtreant_shadow");
        if (InfectedTreant.leavesTextureName != null) {
            try {
                GameTexture particleTexture = GameTexture.fromFileRaw("particles/" + InfectedTreant.leavesTextureName);
                int leavesRes = particleTexture.getHeight();
                int leafSprites = particleTexture.getWidth() / leavesRes;
                GameTextureSection particleSection = GameResources.particlesTextureGenerator.addTexture(particleTexture);
                InfectedTreant.leavesTexture = () -> particleSection.sprite(GameRandom.globalRandom.nextInt(leafSprites), 0, leavesRes);
            } catch (FileNotFoundException var5) {
                InfectedTreant.leavesTexture = null;
            }
        }

        SpinelGolem.texture = GameTexture.fromFile("mobs/spinelgolem");
        SpinelCaveling.texture = new HumanTexture(GameTexture.fromFile("mobs/spinelcaveling"), GameTexture.fromFile("mobs/spinelcavelingarms_front"), GameTexture.fromFile("mobs/spinelcavelingarms_back"));;
        TungstenCaveling.texture = new HumanTexture(GameTexture.fromFile("mobs/tungstencaveling"), GameTexture.fromFile("mobs/tungstencavelingarms_front"), GameTexture.fromFile("mobs/tungstencavelingarms_back"));;

        SpinelMimic.texture = GameTexture.fromFile("mobs/spinelmimic");
        SpinelMimic.texture_shadow = GameTexture.fromFile("mobs/spinelmimic_shadow");

        // BOSSES
        UnstableGelSlime.texture = GameTexture.fromFile("mobs/unstablegelslime");
        UnstableGelSlime.icon = GameTexture.fromFile("mobs/icons/unstablegelslime");
        MiniUnstableGelSlime.texture = GameTexture.fromFile("mobs/miniunstablegelslime");

        ThePillarMob.icon = GameTexture.fromFile("mobs/icons/thepillar");

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
        AircutProjectile.RedAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutred");

        DaggerProjectile.CopperDaggerProjectile.texture = GameTexture.fromFile("player/weapons/copperdagger");
        DaggerProjectile.IronDaggerProjectile.texture = GameTexture.fromFile("player/weapons/irondagger");
        DaggerProjectile.GoldDaggerProjectile.texture = GameTexture.fromFile("player/weapons/golddagger");
        DaggerProjectile.DemonicDaggerProjectile.texture = GameTexture.fromFile("player/weapons/demonicdagger");
        DaggerProjectile.TungstenDaggerProjectile.texture = GameTexture.fromFile("player/weapons/tungstendagger");
        DaggerProjectile.LostUmbrellaDaggerProjectile.texture = GameTexture.fromFile("player/weapons/umbrella");
    }

}