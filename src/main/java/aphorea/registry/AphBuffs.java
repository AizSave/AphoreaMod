package aphorea.registry;

import aphorea.buffs.Banners.BlankBannerBuff;
import aphorea.buffs.Banners.StrikeBannerBuff;
import aphorea.buffs.*;
import aphorea.buffs.SetBonus.*;
import aphorea.buffs.Trinkets.Healing.FloralRingBuff;
import aphorea.buffs.Trinkets.Healing.WitchMedallionBuff;
import aphorea.buffs.Trinkets.Periapts.*;
import aphorea.buffs.TrinketsActive.*;
import aphorea.other.buffs.trinkets.AphBaseRuneActiveBuff;
import aphorea.other.buffs.trinkets.AphBaseRuneTrinketBuff;
import aphorea.other.buffs.trinkets.AphModifierRuneTrinketBuff;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.HiddenCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.ShownCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.VicinityBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

public class AphBuffs {

    public static Buff INMORTAL;
    public static Buff STOP;
    public static Buff STUN;
    public static Buff STICKY;
    public static Buff BERSERKER_RUSH;
    public static Buff PERIAPT_ACTIVE;
    public static Buff SABER_DASH_ACTIVE;

    public static class COOLDOWNS {
        public static Buff INMORTAL_COOLDOWN;
        public static Buff BERSERKER_RUSH_COOLDOWN;
        public static Buff GREATSWORD_SPIN_COOLDOWN;
        public static Buff PERIAPT_COOLDOWN;
        public static Buff SABER_DASH_COOLDOWN;
    }

    public static class SET_BONUS {
        public static SetBonusBuff GOLD_HAT;
        public static SetBonusBuff ROCKY;
        public static SetBonusBuff WITCH;
        public static SetBonusBuff SWAMP_MASK;
        public static SetBonusBuff SWAMP_HOOP;
    }

    public static class BANNER {
        public static VicinityBuff BLANK;
        public static VicinityBuff STRIKE;
    }

    public static void registerCore() {
        // Common Buffs
        BuffRegistry.registerBuff("inmortal", INMORTAL = new InmortalBuff());
        BuffRegistry.registerBuff("stop", STOP = new StopBuff());
        BuffRegistry.registerBuff("stun", STUN = new StunBuff());
        BuffRegistry.registerBuff("sticky", STICKY = new StickyBuff());
        BuffRegistry.registerBuff("berserkerrush", BERSERKER_RUSH = new BerserkerRushBuff());
        BuffRegistry.registerBuff("periaptactive", PERIAPT_ACTIVE = new PeriaptActiveBuff());
        BuffRegistry.registerBuff("saberdashactive", SABER_DASH_ACTIVE = new HiddenCooldownBuff());

        // Common Cooldowns
        BuffRegistry.registerBuff("inmortalcooldown", COOLDOWNS.INMORTAL_COOLDOWN = new ShownCooldownBuff());
        BuffRegistry.registerBuff("berserkerrushcooldown", COOLDOWNS.BERSERKER_RUSH_COOLDOWN = new ShownCooldownBuff());
        BuffRegistry.registerBuff("greatswordspincooldown", COOLDOWNS.GREATSWORD_SPIN_COOLDOWN = new ShownCooldownBuff());
        BuffRegistry.registerBuff("periaptcooldown", COOLDOWNS.PERIAPT_COOLDOWN = new ShownCooldownBuff());
        BuffRegistry.registerBuff("saberdashcooldown", COOLDOWNS.SABER_DASH_COOLDOWN = new ShownCooldownBuff());

        // Armor Set Bonus
        BuffRegistry.registerBuff("goldhatsetbonus", SET_BONUS.GOLD_HAT = new GoldHatSetBonusBuff());
        BuffRegistry.registerBuff("rockysetbonus", SET_BONUS.ROCKY = new RockySetBonusBuff());
        BuffRegistry.registerBuff("witchsetbonus", SET_BONUS.WITCH = new PinkWitchSetBonusBuff());
        BuffRegistry.registerBuff("swampmasksetbonus", SET_BONUS.SWAMP_MASK = new SwampMaskSetBonusBuff());
        BuffRegistry.registerBuff("swamphoodsetbonus", SET_BONUS.SWAMP_HOOP = new SwampHoodSetBonusBuff());

        // Banner Buffs
        BuffRegistry.registerBuff("blankbanner", BANNER.BLANK = new BlankBannerBuff());
        BuffRegistry.registerBuff("strikebanner", BANNER.STRIKE = new StrikeBannerBuff());

        // Potion Buffs
        BuffRegistry.registerBuff("lowdspoison", new LowdsPoisonBuff());

        // Trinket Buffs
        BuffRegistry.registerBuff("unstableperiapt", new UnstablePeriaptBuff());
        BuffRegistry.registerBuff("necromancyperiapt", new NecromancyPeriaptBuff());
        BuffRegistry.registerBuff("rockyperiapt", new RockyPeriaptBuff());
        BuffRegistry.registerBuff("bloodyperiapt", new BloodyPeriaptBuff());
        BuffRegistry.registerBuff("demonicperiapt", new DemonicPeriaptBuff());
        BuffRegistry.registerBuff("abysmalperiapt", new AbysmalPeriaptBuff());
        BuffRegistry.registerBuff("frozenperiapt", new FrozenPeriaptBuff());
        BuffRegistry.registerBuff("floralring", new FloralRingBuff());
        BuffRegistry.registerBuff("gelring", new SimpleTrinketBuff("gelring", new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.3F)));
        BuffRegistry.registerBuff("heartring", new SimpleTrinketBuff("heartring", new ModifierValue<>(BuffModifiers.MAX_HEALTH_FLAT, 20)));
        BuffRegistry.registerBuff("witchmedallion", new WitchMedallionBuff());

        // Trinket Active Buffs
        BuffRegistry.registerBuff("rockyperiaptactive", new RockyPeriaptActiveBuff());
        BuffRegistry.registerBuff("bloodyperiaptactive", new BloodyPeriaptActiveBuff());
        BuffRegistry.registerBuff("demonicperiaptactive", new DemonicPeriaptActiveBuff());
        BuffRegistry.registerBuff("frozenperiaptactive", new FrozenPeriaptActiveBuff());

        // Mobs
        BuffRegistry.registerBuff("unstablegelslimerush", new UnstableGelSlimeRushBuff());

        // Runes Injectors
        BuffRegistry.registerBuff("runesinjectoractive", new ShownCooldownBuff());
        BuffRegistry.registerBuff("runesinjectorcooldown", new ShownCooldownBuff());

        // Base Runes
        baseRunes();

        // Modifier Runes
        modifierRunes();

        // In Development
        BuffRegistry.registerBuff("bannerbearerfoci", new SimpleTrinketBuff(new String[]{"bannerbearerfoci1", "bannerbearerfoci2"}, new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.3F)));
        BuffRegistry.registerBuff("iceboots", new SimpleTrinketBuff("iceboots", new ModifierValue<>(BuffModifiers.FRICTION, 0F).max(-1F), new ModifierValue<>(BuffModifiers.SPEED, 0.5F)));

    }

    public static void baseRunes() {
        // DASH RUNE
        // On activation
        BuffRegistry.registerBuff("dashrune", new AphBaseRuneTrinketBuff(100, 500, "dashruneactive") {
            @Override
            public void run(Level level, PlayerMob player) {
                super.run(level, player);
                float strength = getEffectNumber(player);
                Point2D.Float dir = PacketForceOfWind.getMobDir(player);
                PacketForceOfWind.applyToPlayer(level, player, dir.x, dir.y, strength);
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, player, 0.15F, null), level.isServer());
                player.buffManager.forceUpdateBuffs();
                if (level.isServer()) {
                    ServerClient serverClient = player.getServerClient();
                    player.getServer().network.sendToClientsWithEntityExcept(new PacketForceOfWind(serverClient.slot, player.moveX, player.moveY, strength), serverClient.playerMob, serverClient);
                }
            }
        });
        // On duration
        BuffRegistry.registerBuff("dashruneactive", new AphBaseRuneActiveBuff(100, 5000, new ModifierValue<>(BuffModifiers.UNTARGETABLE, true)));

        // HEALING RUNE
        // On activation
        BuffRegistry.registerBuff("healingrune", new AphBaseRuneTrinketBuff(0.25F, 60000) {
            @Override
            public float getBaseHealing() {
                return 0.25F;
            }
            @Override
            public float getHealing(PlayerMob player) {
                return super.getHealing(player) + getEffectNumber(player);
            }
        });

        // HARDENING RUNE
        // On activation
        BuffRegistry.registerBuff("hardeningrune", new AphBaseRuneTrinketBuff(1, 10000, "hardeningruneactive"));
        // On duration
        BuffRegistry.registerBuff("hardeningruneactive", new AphBaseRuneActiveBuff(1, 30000, "hardeningrunecooldown", new ModifierValue<>(BuffModifiers.ARMOR, 1F)) {
            @Override
            public void initExtraModifiers(ActiveBuff buff, float numberEffect) {
                super.initExtraModifiers(buff, numberEffect);
                buff.addModifier(BuffModifiers.ARMOR, numberEffect);
            }
        });
        // On cooldown
        BuffRegistry.registerBuff("hardeningrunecooldown", new HiddenCooldownBuff() {
            @Override
            public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
                super.init(buff, eventSubscriber);
                buff.addModifier(BuffModifiers.ARMOR, -0.5F);
            }
        });
    }

    public static void modifierRunes() {
        BuffRegistry.registerBuff("empoweringrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(0.2F)
                .setCooldownVariation(0.2F)
        );

        BuffRegistry.registerBuff("recurrentrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(-0.2F)
                .setCooldownVariation(-0.2F)
        );

        BuffRegistry.registerBuff("frenzyrune", new AphModifierRuneTrinketBuff()
                .setCooldownVariation(-0.5F)
                .setHealthCost(0.1F)
        );

        BuffRegistry.registerBuff("devastatingrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(0.3F)
                .setHealthCost(0.1F)
        );
    }
}