package aphorea.registry;

import aphorea.items.weapons.melee.greatsword.logic.GreatswordDashLevelEvent;
import aphorea.items.weapons.melee.saber.logic.SaberDashLevelEvent;
import aphorea.items.weapons.melee.saber.logic.SaberJumpLevelEvent;
import aphorea.levelevents.*;
import aphorea.levelevents.thepillar.ThePillarFallingCrystalAttackEvent;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.objects.ThePillarEntranceObject;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.registries.LevelEventRegistry;

public class AphLevelEvents {
    public static void registerCore() {
        // Ground Effect
        LevelEventRegistry.registerEvent("gelprojectilegroundeffect", GelProjectile.GelProjectileGroundEffectEvent.class);
        LevelEventRegistry.registerEvent("firepoolgroundeffect", SpamBulletProjectile.FirePoolGroundEffectEvent.class);

        // Dash
        LevelEventRegistry.registerEvent("saberdash", SaberDashLevelEvent.class);
        LevelEventRegistry.registerEvent("saberjump", SaberJumpLevelEvent.class);
        LevelEventRegistry.registerEvent("greatsworddash", GreatswordDashLevelEvent.class);

        // Explosion
        LevelEventRegistry.registerEvent("volatilegelexplosion", VolatileGelSlime.VolatileGelExplosion.class);
        LevelEventRegistry.registerEvent("spambulletexplosion", SpamBulletProjectile.SpamBulletExplosion.class);

        // The Pillar Boss
        LevelEventRegistry.registerEvent("thepillarentrance", ThePillarEntranceObject.ThePillarEntranceEvent.class);
        LevelEventRegistry.registerEvent("thepillarfallingcrystalattack", ThePillarFallingCrystalAttackEvent.class);

        // Runes
        baseRunes();
        modifierRunes();
    }

    public static void baseRunes() {
        LevelEventRegistry.registerEvent("runeofdetonationevent", AphRuneOfDetonationEvent.class);
        LevelEventRegistry.registerEvent("runeofthunderevent", AphRuneOfThunderEvent.class);
        LevelEventRegistry.registerEvent("runeofqueenspiderevent", AphRuneOfQueenSpiderEvent.class);
        LevelEventRegistry.registerEvent("runeofcryoqueenevent", AphRuneOfCryoQueenEvent.class);
        LevelEventRegistry.registerEvent("runeofpestwardenevent", AphRuneOfPestWardenEvent.class);
        LevelEventRegistry.registerEvent("runeofmotherslimeevent", AphRuneOfMotherSlimeEvent.class);
        LevelEventRegistry.registerEvent("runeofsunlightchampionevent", AphRuneOfSunlightChampionEvent.class);
        LevelEventRegistry.registerEvent("runeofsunlightchampionexplosionevent", AphRuneOfSunlightChampionExplosionEvent.class);
        LevelEventRegistry.registerEvent("runeofcrystaldragonevent", AphRuneOfCrystalDragonEvent.class);
    }

    public static void modifierRunes() {
        LevelEventRegistry.registerEvent("abysmalruneevent", AphAbysmalRuneEvent.class);
        LevelEventRegistry.registerEvent("tildalruneevent", AphTidalRuneEvent.class);
    }
}
