package aphorea;

import aphorea.items.tools.weapons.magic.MagicalBroom;
import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.*;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.LivingSapling;
import aphorea.mobs.summon.Onyx;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.particles.BabylonTowerFallingCrystalParticle;
import aphorea.particles.NarcissistParticle;
import aphorea.particles.SpinelShieldParticle;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AphResources {
    public static GameTexture[] saberAttackTexture = new GameTexture[31];

    public static GameTexture gunAttackTrackTexture;
    public static GameTexture gunAttackThumbTexture;

    public static GameTexture glacialSaberAttackTrackTexture;
    public static GameTexture glacialSaberAttackThumbTexture;

    // Max Width = 855px
    public static Map<String, GameTexture> bookTextures = new HashMap<>();

    public static void initResources() {

        // UI WEAPONS
        for (int i = 0; i < 31; i++) {
            saberAttackTexture[i] = new GameTexture(GameTexture.fromFile("ui/saberattack"), 0, 24 * i, 66, 24);
        }

        gunAttackTrackTexture = GameTexture.fromFile("ui/gunattacktrack");
        gunAttackThumbTexture = GameTexture.fromFile("ui/gunattackthumb");

        glacialSaberAttackTrackTexture = GameTexture.fromFile("ui/glacialsaberattacktrack");
        glacialSaberAttackThumbTexture = GameTexture.fromFile("ui/glacialsaberattackthumb");

        // BOOK TEXTURES
        bookTextures.put("runestutorial_open", GameTexture.fromFile("ui/books/runestutorial_open"));
        bookTextures.put("runestutorial_equip", GameTexture.fromFile("ui/books/runestutorial_equip"));
        bookTextures.put("runestutorial_baserunes", GameTexture.fromFile("ui/books/runestutorial_baserunes"));
        bookTextures.put("runestutorial_modifierrunes", GameTexture.fromFile("ui/books/runestutorial_modifierrunes"));
        bookTextures.put("runestutorial_table", GameTexture.fromFile("ui/books/runestutorial_table"));
        bookTextures.put("runestutorial_craft", GameTexture.fromFile("ui/books/runestutorial_craft"));

        // MOBS
        mobResources();

        // ITEMS
        MagicalBroom.worldTexture = GameTexture.fromFile("worlditems/magicalbroom");

        // PROJECTILES
        projectileResources();

        // PARTICLES
        GelProjectile.GelProjectileParticle.texture = GameTexture.fromFile("particles/gelprojectile");
        SpamBulletProjectile.FirePoolParticle.texture = GameTexture.fromFile("particles/firepool");
        BabylonTowerFallingCrystalParticle.projectileTexture = GameTexture.fromFile("particles/babylontowerfallingcrystalparticle");
        BabylonTowerFallingCrystalParticle.shadowTexture = GameTexture.fromFile("particles/babylontowerfallingcrystalparticle_shadow");
        SpinelShieldParticle.texture = GameTexture.fromFile("particles/spinelshield");
        NarcissistParticle.texture = GameTexture.fromFile("player/weapons/thenarcissist");

        // SOUNDS
        SOUNDS.initSoundResources();
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
        InfectedTreant.leavesTexture = loadLeafTexture(InfectedTreant.leavesTextureName);

        SpinelGolem.texture = GameTexture.fromFile("mobs/spinelgolem");
        SpinelCaveling.texture = new HumanTexture(GameTexture.fromFile("mobs/spinelcaveling"), GameTexture.fromFile("mobs/spinelcavelingarms_front"), GameTexture.fromFile("mobs/spinelcavelingarms_back"));
        TungstenCaveling.texture = new HumanTexture(GameTexture.fromFile("mobs/tungstencaveling"), GameTexture.fromFile("mobs/tungstencavelingarms_front"), GameTexture.fromFile("mobs/tungstencavelingarms_back"));

        SpinelMimic.texture = GameTexture.fromFile("mobs/spinelmimic");
        SpinelMimic.texture_shadow = GameTexture.fromFile("mobs/spinelmimic_shadow");

        // BOSSES
        UnstableGelSlime.texture = GameTexture.fromFile("mobs/unstablegelslime");
        UnstableGelSlime.icon = GameTexture.fromFile("mobs/icons/unstablegelslime");
        MiniUnstableGelSlime.texture = GameTexture.fromFile("mobs/miniunstablegelslime");

        BabylonTowerMob.icon = GameTexture.fromFile("mobs/icons/babylontower");

        // FRIENDLY
        WildPhosphorSlime.texture = GameTexture.fromFile("mobs/phosphorslime");
        WildPhosphorSlime.texture_scared = GameTexture.fromFile("mobs/phosphorslime_scared");

        // SUMMONS
        BabyUnstableGelSlime.texture = GameTexture.fromFile("mobs/babyunstablegelslime");
        VolatileGelSlime.texture = GameTexture.fromFile("mobs/volatilegelslime");
        Onyx.texture = GameTexture.fromFile("mobs/onyx");

        LivingSapling.texture = GameTexture.fromFile("mobs/livingsapling");
        LivingSapling.texture_shadow = GameTexture.fromFile("mobs/livingsapling_shadow");
        LivingSapling.leavesTexture = loadLeafTexture(LivingSapling.leavesTextureName);

        // PETS
        PetPhosphorSlime.texture = GameTexture.fromFile("mobs/phosphorslime");
        PetPhosphorSlime.texture_scared = GameTexture.fromFile("mobs/phosphorslime_scared");
    }

    public static void projectileResources() {
        AircutProjectile.CopperAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutcopper");
        AircutProjectile.IronAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutiron");
        AircutProjectile.GoldAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutgold");
        AircutProjectile.UnstableGelAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutunstablegel");
        AircutProjectile.DemonicAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutdemonic");
        AircutProjectile.CrimsonAircutProjectile.texture = GameTexture.fromFile("projectiles/aircutcrimson");

        DaggerProjectile.CopperDaggerProjectile.texture = GameTexture.fromFile("player/weapons/copperdagger");
        DaggerProjectile.IronDaggerProjectile.texture = GameTexture.fromFile("player/weapons/irondagger");
        DaggerProjectile.GoldDaggerProjectile.texture = GameTexture.fromFile("player/weapons/golddagger");
        DaggerProjectile.DemonicDaggerProjectile.texture = GameTexture.fromFile("player/weapons/demonicdagger");
        DaggerProjectile.TungstenDaggerProjectile.texture = GameTexture.fromFile("player/weapons/tungstendagger");
        DaggerProjectile.LostUmbrellaDaggerProjectile.texture = GameTexture.fromFile("player/weapons/lostumbrella");
    }

    public static Supplier<GameTextureSection> loadLeafTexture(String leavesTextureName) {
        if (leavesTextureName != null) {
            try {
                GameTexture particleTexture = GameTexture.fromFileRaw("particles/" + leavesTextureName);
                int leavesRes = particleTexture.getHeight();
                int leafSprites = particleTexture.getWidth() / leavesRes;
                GameTextureSection particleSection = GameResources.particlesTextureGenerator.addTexture(particleTexture);
                return () -> particleSection.sprite(GameRandom.globalRandom.nextInt(leafSprites), 0, leavesRes);
            } catch (FileNotFoundException var5) {
                return null;
            }
        }
        return null;
    }

    public static class SOUNDS {
        public static class HARP {
            public static GameSound Do;
            public static GameSound Re;
            public static GameSound Mi;
            public static GameSound Fa;
            public static GameSound Sol;
            public static GameSound La;
            public static GameSound Si;

            public static GameSound[] All;
        }

        public static void initSoundResources() {
            HARP.Do = GameSound.fromFile("do_harp");
            HARP.Re = GameSound.fromFile("re_harp");
            HARP.Mi = GameSound.fromFile("mi_harp");
            HARP.Fa = GameSound.fromFile("fa_harp");
            HARP.Sol = GameSound.fromFile("sol_harp");
            HARP.La = GameSound.fromFile("la_harp");
            HARP.Si = GameSound.fromFile("si_harp");
            HARP.All = new GameSound[] {
                    HARP.Do, HARP.Re, HARP.Mi, HARP.Fa, HARP.Sol, HARP.La, HARP.Si
            };
        }
    }
}
