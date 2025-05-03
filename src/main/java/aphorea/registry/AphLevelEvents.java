package aphorea.registry;

import aphorea.items.tools.weapons.melee.greatsword.logic.GreatswordDashLevelEvent;
import aphorea.items.tools.weapons.melee.rapier.logic.RapierDashLevelEvent;
import aphorea.items.tools.weapons.melee.saber.logic.SaberDashLevelEvent;
import aphorea.items.tools.weapons.melee.saber.logic.SaberJumpLevelEvent;
import aphorea.levelevents.AphNarcissistEvent;
import aphorea.levelevents.AphSpinelShieldEvent;
import aphorea.levelevents.babylon.BabylonTowerFallingCrystalAttackEvent;
import aphorea.levelevents.runes.*;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.objects.BabylonEntranceObject;
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
        LevelEventRegistry.registerEvent("rapierdash", RapierDashLevelEvent.class);
        LevelEventRegistry.registerEvent("greatsworddash", GreatswordDashLevelEvent.class);

        // Explosion
        LevelEventRegistry.registerEvent("volatilegelexplosion", VolatileGelSlime.VolatileGelExplosion.class);
        LevelEventRegistry.registerEvent("spambulletexplosion", SpamBulletProjectile.SpamBulletExplosion.class);

        // Trinkets
        LevelEventRegistry.registerEvent("spinelshield", AphSpinelShieldEvent.class);

        // Weapons
        LevelEventRegistry.registerEvent("narcissistbuff", AphNarcissistEvent.class);

        // Babylon Boss
        LevelEventRegistry.registerEvent("babylonentrance", BabylonEntranceObject.BabylonEntranceEvent.class);
        LevelEventRegistry.registerEvent("babylontowerfallingcrystalattack", BabylonTowerFallingCrystalAttackEvent.class);

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
