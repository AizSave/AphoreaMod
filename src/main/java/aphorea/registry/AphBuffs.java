package aphorea.registry;

import aphorea.AphDependencies;
import aphorea.buffs.*;
import aphorea.buffs.Banners.AphBannerBuff;
import aphorea.buffs.Banners.AphBasicBannerBuff;
import aphorea.buffs.Banners.AphStrikeBannerBuff;
import aphorea.buffs.Banners.BlankBannerBuff;
import aphorea.buffs.Runes.AphBaseRuneActiveBuff;
import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import aphorea.buffs.SetBonus.*;
import aphorea.buffs.Trinkets.AdrenalineCharmBuff;
import aphorea.buffs.Trinkets.BannerBearerFociBuff;
import aphorea.buffs.Trinkets.Healing.*;
import aphorea.buffs.Trinkets.Periapts.*;
import aphorea.buffs.TrinketsActive.BloodyPeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.DemonicPeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.PeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.RockyPeriaptActiveBuff;
import aphorea.levelevents.*;
import aphorea.mobs.runicsummons.RunicAttackingFollowingMob;
import aphorea.mobs.runicsummons.RunicFlyingAttackingFollowingMob;
import aphorea.packets.AphRuneOfUnstableGelSlimePacket;
import aphorea.packets.AphSingleAreaShowPacket;
import aphorea.projectiles.rune.RuneOfSpiderEmpressProjectile;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.GlobalData;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.HiddenCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.gameFont.FontManager;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AphBuffs {

    public static Buff IMMORTAL;
    public static Buff STOP;

    public static Buff STUN;
    public static Buff FALLEN_STUN;
    public static Buff STICKY;
    public static Buff CURSED;

    public static Buff DAGGER_ATTACK;
    public static Buff BERSERKER_RUSH;
    public static Buff PERIAPT_ACTIVE;
    public static Buff SABER_DASH_ACTIVE;
    public static Buff RUNE_INJECTOR_ACTIVE;

    public static Buff INMORTAL_COOLDOWN;
    public static Buff BERSERKER_RUSH_COOLDOWN;
    public static Buff SPIN_ATTACK_COOLDOWN;
    public static Buff PERIAPT_COOLDOWN;
    public static Buff SABER_DASH_COOLDOWN;
    public static Buff RUNE_INJECTOR_COOLDOWN;

    public static class SET_BONUS {
        public static SetBonusBuff GOLD_HAT;
        public static SetBonusBuff ROCKY;
        public static SetBonusBuff WITCH;
        public static SetBonusBuff SWAMP_MASK;
        public static SetBonusBuff SWAMP_HOOP;
    }

    public static class BANNER {
        public static AphBannerBuff BLANK;
        public static AphBannerBuff STRIKE;
        public static AphBannerBuff DAMAGE;
        public static AphBannerBuff DEFENSE;
        public static AphBannerBuff SPEED;
        public static AphBannerBuff SUMMON_SPEED;
    }

    public static void registerCore() {
        // Common Buffs
        BuffRegistry.registerBuff("immortal", IMMORTAL = new ImmortalBuff());
        BuffRegistry.registerBuff("stop", STOP = new StopBuff());
        BuffRegistry.registerBuff("stun", STUN = new StunBuff());
        BuffRegistry.registerBuff("fallenstun", FALLEN_STUN = new StunBuff() {
            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);

                Mob owner = buff.owner;
                GameRandom random = GameRandom.globalRandom;
                AtomicReference<Float> currentAngle = new AtomicReference<>(random.nextFloat() * 360.0F);
                float distance = 75.0F;

                for (int i = 0; i < 4; ++i) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin(currentAngle.get()) * distance + (float) random.getIntBetween(-5, 5), owner.y + GameMath.cos((Float) currentAngle.get()) * distance + (float) random.getIntBetween(-5, 5) * 0.85F, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0F).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                        float angle = currentAngle.accumulateAndGet(delta * 30.0F / 250.0F, Float::sum);
                        float distY = (distance - 20.0F) * 0.85F;
                        pos.x = owner.x + GameMath.sin(angle) * (distance - distance / 2.0F * lifePercent);
                        pos.y = owner.y + GameMath.cos(angle) * distY - 20.0F * lifePercent;
                    }).color((options, lifeTime, timeAlive, lifePercent) -> {
                        options.color(AphColors.palettePinkWitch[2]);
                        if (lifePercent > 0.5F) {
                            options.alpha(2.0F * (1.0F - lifePercent));
                        }

                    }).size((options, lifeTime, timeAlive, lifePercent) -> {
                        options.size(22, 22);
                    }).lifeTime(1000);
                }


            }
        });
        BuffRegistry.registerBuff("sticky", STICKY = new StickyBuff());
        BuffRegistry.registerBuff("cursed", CURSED = new CursedBuff());
        BuffRegistry.registerBuff("daggerattack", DAGGER_ATTACK = new DaggerAttackBuff());
        BuffRegistry.registerBuff("berserkerrush", BERSERKER_RUSH = new BerserkerRushBuff());
        BuffRegistry.registerBuff("periaptactive", PERIAPT_ACTIVE = new PeriaptActiveBuff());
        BuffRegistry.registerBuff("saberdashactive", SABER_DASH_ACTIVE = new HiddenCooldownBuff());

        // Common Cooldowns
        BuffRegistry.registerBuff("immortalcooldown", INMORTAL_COOLDOWN = new HiddenCooldownBuff());
        BuffRegistry.registerBuff("berserkerrushcooldown", BERSERKER_RUSH_COOLDOWN = new AphShownCooldownBuff());
        BuffRegistry.registerBuff("spinattackcooldown", SPIN_ATTACK_COOLDOWN = new AphShownCooldownBuff());
        BuffRegistry.registerBuff("periaptcooldown", PERIAPT_COOLDOWN = new AphShownCooldownBuff());
        BuffRegistry.registerBuff("saberdashcooldown", SABER_DASH_COOLDOWN = new AphShownCooldownBuff());

        // Armor Set Bonus
        BuffRegistry.registerBuff("goldhatsetbonus", SET_BONUS.GOLD_HAT = new GoldHatSetBonusBuff());
        BuffRegistry.registerBuff("rockysetbonus", SET_BONUS.ROCKY = new RockySetBonusBuff());
        BuffRegistry.registerBuff("pinkwitchsetbonus", SET_BONUS.WITCH = new PinkWitchSetBonusBuff());
        BuffRegistry.registerBuff("swampmasksetbonus", SET_BONUS.SWAMP_MASK = new SwampMaskSetBonusBuff());
        BuffRegistry.registerBuff("swamphoodsetbonus", SET_BONUS.SWAMP_HOOP = new SwampHoodSetBonusBuff());

        // Banner Buffs
        BuffRegistry.registerBuff("blankbanner", BANNER.BLANK = new BlankBannerBuff());
        BuffRegistry.registerBuff("strikebanner", BANNER.STRIKE = new AphStrikeBannerBuff());
        BuffRegistry.registerBuff("aph_bannerofdamage", BANNER.DAMAGE = AphBasicBannerBuff.floatModifier(BuffModifiers.ALL_DAMAGE, 0.15F));
        BuffRegistry.registerBuff("aph_bannerofdefense", BANNER.DEFENSE = AphBasicBannerBuff.floatModifier((value, effect) -> Math.max(0.5F, 1F - effect * value), 1, BuffModifiers.INCOMING_DAMAGE_MOD, 0.1F));
        BuffRegistry.registerBuff("aph_bannerofspeed", BANNER.SPEED = AphBasicBannerBuff.floatModifier(BuffModifiers.SPEED, 0.3F));
        BuffRegistry.registerBuff("aph_bannerofsummonspeed", BANNER.SUMMON_SPEED = AphBasicBannerBuff.floatModifier(BuffModifiers.SUMMONS_SPEED, 0.75F));

        registerMightyBannerItems();

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
        BuffRegistry.registerBuff("iceboots", new SimpleTrinketBuff("iceboots", new ModifierValue<>(BuffModifiers.FRICTION, -0.75F), new ModifierValue<>(BuffModifiers.SPEED, 0.5F), new ModifierValue<>(BuffModifiers.ARMOR_FLAT, 4)));
        BuffRegistry.registerBuff("cursedmedallion", new CursedMedallionBuff());
        BuffRegistry.registerBuff("ancientmedallion", new AncientMedallionBuff());
        BuffRegistry.registerBuff("healingessence", new HealingEssenceBuff());
        BuffRegistry.registerBuff("ninjascarf", new SimpleTrinketBuff("ninjascarf"));
        BuffRegistry.registerBuff("bannerbearerfoci", new BannerBearerFociBuff());
        BuffRegistry.registerBuff("adrenalinecharm", new AdrenalineCharmBuff());
        BuffRegistry.registerBuff("adrenalinecharmcharge", new AdrenalineCharmBuff.AdrenalineCharmChargeBuff());
        BuffRegistry.registerBuff("test1", new SimpleTrinketBuff("test1", new ModifierValue<>(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue<>(BuffModifiers.SUMMONS_SPEED, -0.2F), new ModifierValue<>(BuffModifiers.SUMMON_DAMAGE, -0.2F)));
        BuffRegistry.registerBuff("test2", new SimpleTrinketBuff("test2", new ModifierValue<>(BuffModifiers.MAX_SUMMONS, 3), new ModifierValue<>(BuffModifiers.SUMMONS_SPEED, -0.2F), new ModifierValue<>(BuffModifiers.SUMMON_DAMAGE, -0.2F)));

        // Trinket Active Buffs
        BuffRegistry.registerBuff("rockyperiaptactive", new RockyPeriaptActiveBuff());
        BuffRegistry.registerBuff("bloodyperiaptactive", new BloodyPeriaptActiveBuff());
        BuffRegistry.registerBuff("demonicperiaptactive", new DemonicPeriaptActiveBuff());

        // Mobs
        BuffRegistry.registerBuff("unstablegelslimerush", new UnstableGelSlimeRushBuff());

        // Runes Injectors
        BuffRegistry.registerBuff("runesinjectoractive", RUNE_INJECTOR_ACTIVE = new AphShownBuff());
        BuffRegistry.registerBuff("runesinjectorcooldown", RUNE_INJECTOR_COOLDOWN = new AphShownCooldownBuff());
        runesInjectors();

        // Base Runes
        tier0BaseRunes();
        tier1BaseRunes();
        tier2BaseRunes();
        tier3BaseRunes();

        // Modifier Runes
        tier0ModifierRunes();
        tier1ModifierRunes();
        tier2ModifierRunes();
        tier3ModifierRunes();

    }

    public static void runesInjectors() {
        // Non-effect Injectors
        BuffRegistry.registerBuff("rusticrunesinjector", new AphModifierRuneTrinketBuff());
        BuffRegistry.registerBuff("demonicrunesinjector", new AphModifierRuneTrinketBuff());
        BuffRegistry.registerBuff("tungstenrunesinjector", new AphModifierRuneTrinketBuff());
        BuffRegistry.registerBuff("ancientrunesinjector", new AphModifierRuneTrinketBuff());

        // Unstable Injector
        BuffRegistry.registerBuff("unstablerunesinjector", new AphModifierRuneTrinketBuff()
                .setHealthCost(0.1F)
                .setEffectNumberVariation(0.2F)
        );
    }

    public static void tier0BaseRunes() {
        float baseEffectNumber, extraEffectNumberMod;

        // RUNE OF FURY
        baseEffectNumber = 40;
        // On activation
        BuffRegistry.registerBuff("runeoffury", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeoffuryactive")
                .setHealthCost(0.25F)
        );
        // On duration
        BuffRegistry.registerBuff("runeoffuryactive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000) {
            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.ALL_DAMAGE, effectNumber / 100);
                buff.addModifier(BuffModifiers.ATTACK_SPEED, effectNumber / 100);
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.anger).height(16.0F);
                }
            }
        });

        // RUNE OF SPEED
        baseEffectNumber = 120;
        extraEffectNumberMod = 1.5F;
        // On activation
        BuffRegistry.registerBuff("runeofspeed", new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 5000) {
            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float strength = getEffectNumber(player);
                SoundManager.playSound(GameResources.swoosh, SoundEffect.effect(player).volume(0.5F).pitch(1.7F));
                Point2D.Float dir = PacketForceOfWind.getMobDir(player);
                PacketForceOfWind.applyToMob(level, player, dir.x, dir.y, strength);
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, player, 0.15F, null), level.isServer());
                player.buffManager.forceUpdateBuffs();
                player.buffManager.addBuff(new ActiveBuff(AphBuffs.IMMORTAL, player, 500, null), false);
                if (level.isServer()) {
                    ServerClient serverClient = player.getServerClient();
                    player.getServer().network.sendToClientsWithEntityExcept(new PacketForceOfWind(player, player.moveX, player.moveY, strength), player, serverClient);
                }
            }
        });

        // RUNE OF HEALING
        baseEffectNumber = 25;
        // On activation
        BuffRegistry.registerBuff("runeofhealing", new AphBaseRuneTrinketBuff(baseEffectNumber, 30000) {
            @Override
            public float getBaseHealing() {
                return getBaseEffectNumber() / 100;
            }

            @Override
            public float getHealing(float effectNumberVariation) {
                return this.getBaseHealing() * effectNumberVariation;
            }
        });

        // RUNE OF RESISTANCE
        baseEffectNumber = 100;
        // On activation
        BuffRegistry.registerBuff("runeofresistance", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofresistanceactive"));
        // On duration
        BuffRegistry.registerBuff("runeofresistanceactive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000, "runeofresistancecooldown") {
            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.ARMOR, effectNumber / 100);
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.iron).height(16.0F);
                }
            }
        });
        // On cooldown
        BuffRegistry.registerBuff("runeofresistancecooldown", new HiddenCooldownBuff() {
            @Override
            public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
                super.init(buff, eventSubscriber);
                buff.addModifier(BuffModifiers.ARMOR, -0.5F);
            }
        });

        // RUNE OF VALOR
        baseEffectNumber = 10;
        // On activation
        BuffRegistry.registerBuff("runeofvalor", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) (baseEffectNumber * 1000), "runeofvaloractive") {

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }

        });
        // On duration
        BuffRegistry.registerBuff("runeofvaloractive", new AphBaseRuneActiveBuff(baseEffectNumber, 10000, new ModifierValue<>(AphModifiers.BANNER_EFFECT, 1F), new ModifierValue<>(AphModifiers.BANNER_ABILITY_SPEED, 1F)) {

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.gold).height(16.0F);
                }
            }
        });

        // RUNE OF DETONATION
        baseEffectNumber = 50;
        // On activation
        BuffRegistry.registerBuff("runeofdetonation", new AphBaseRuneTrinketBuff(baseEffectNumber, 8000) {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                player.getLevel().entityManager.addLevelEvent(new AphRuneOfDetonationEvent(player, player.x, player.y, getEffectNumber(player) / 100));
            }

        }.setHealthCost(0.05F));

        // RUNE OF THUNDER
        baseEffectNumber = 50;
        // On activation
        BuffRegistry.registerBuff("runeofthunder", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000) {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float range = 600;
                if (player.getDistance(targetX, targetY) > range) {
                    preventUsage = true;
                    if (level.isClient()) {
                        new AphAreaList(new AphArea(range, level.isCave ? AphColors.palettePinkWitch[2] : AphColors.lighting)).executeClient(level, player.x, player.y, 1F, 1F, 0F);
                    }
                } else if (level.isServer()) {
                    player.getLevel().entityManager.addLevelEvent(new AphRuneOfThunderEvent(player, targetX, targetY, getEffectNumber(player) / 100));
                }
            }

        });

        // RUNE OF WINTER
        baseEffectNumber = 10;
        // On activation
        BuffRegistry.registerBuff("runeofwinter", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) (baseEffectNumber * 1000), "runeofwinteractive") {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                AphAreaList areaList = new AphAreaList(
                        new AphArea(500, AphColors.ice)
                                .setDebuffArea((int) (getEffectNumber(player) * 1000), BuffRegistry.Debuffs.FROSTSLOW.getStringID())
                );
                if (level.isServer()) {
                    areaList.executeServer(player);
                } else if (level.isClient()) {
                    areaList.executeClient(level, player.x, player.y);
                }
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }
        });
        BuffRegistry.registerBuff("runeofwinteractive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000));

        // RUNE OF IMMORTALITY
        baseEffectNumber = 5;
        extraEffectNumberMod = 2;
        // On activation
        BuffRegistry.registerBuff("runeofimmortality", new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, (int) (baseEffectNumber * 1000), "runeofimmortalityactive") {
            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }
        });
        // On duration
        BuffRegistry.registerBuff("runeofimmortalityactive", new AphBaseRuneActiveBuff(baseEffectNumber, extraEffectNumberMod, 5000, "runeofimmortalitycooldown") {
            @Override
            public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
                super.onBeforeHitCalculated(buff, event);
                if (buff.owner.isServer() && !event.isPrevented() && event.damage >= buff.owner.getHealth()) {
                    event.prevent();
                    buff.owner.setHealth(buff.owner.getMaxHealth());

                    if (cooldownBuff != null) {
                        buff.owner.buffManager.addBuff(new ActiveBuff(cooldownBuff, buff.owner, 30000, null), true);
                    }
                    if (buff.owner.buffManager.hasBuff(INMORTAL_COOLDOWN)) {
                        buff.owner.buffManager.removeBuff(INMORTAL_COOLDOWN, true);
                    }
                    buff.owner.buffManager.addBuff(new ActiveBuff(AphBuffs.IMMORTAL, buff.owner, 3000, null), true);
                    buff.owner.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN, buff.owner, 30000, null), true);

                }
            }
        });
        // On cooldown
        BuffRegistry.registerBuff("runeofimmortalitycooldown", new HiddenCooldownBuff() {
            @Override
            public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
                super.init(buff, eventSubscriber);
                buff.addModifier(BuffModifiers.MAX_HEALTH, -0.5F);
            }
        });

        // RUNE OF SHADOWS
        baseEffectNumber = 5;
        // On activation
        BuffRegistry.registerBuff("runeofshadows", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) (baseEffectNumber * 1000), "runeofshadowsactive") {

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }

        });
        // On duration
        BuffRegistry.registerBuff("runeofshadowsactive", new AphBaseRuneActiveBuff(baseEffectNumber, 10000, new ModifierValue<>(BuffModifiers.INVISIBILITY, true), new ModifierValue<>(BuffModifiers.TARGET_RANGE, -0.7F), new ModifierValue<>(BuffModifiers.INTIMIDATED, true)));


    }

    public static void tier1BaseRunes() {
        float baseEffectNumber, extraEffectNumberMod;

        // RUNE OF UNSTABLE GEL SLIME
        baseEffectNumber = 5;
        // On activation
        BuffRegistry.registerBuff("runeofunstablegelslime", new AphBaseRuneTrinketBuff(baseEffectNumber, 20000) {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    ServerClient serverClient = player.getServerClient();
                    for (int i = 0; i < 2; i++) {
                        RunicAttackingFollowingMob mob = (RunicAttackingFollowingMob) MobRegistry.getMob("runicunstablegelslime", player.getLevel());

                        player.serverFollowersManager.addFollower("runicunstablegelslimes", mob, FollowPosition.WALK_CLOSE, "runeofunstablegelslime", 1, 2, (MserverClient, Mmob) -> ((RunicAttackingFollowingMob) Mmob).updateEffectNumber(getEffectNumber(player)), true);
                        mob.getLevel().entityManager.addMob(mob, player.x, player.y);
                    }

                    int tileX = player.getX() / 32;
                    int tileY = player.getY() / 32;
                    Point moveOffset = player.getPathMoveOffset();
                    ArrayList<Point> possiblePoints = new ArrayList<>();

                    int maxRange = 5;
                    int minRange = 3;
                    for (int x = tileX - maxRange; x <= tileX + maxRange; ++x) {
                        if (Math.abs(x - tileX) >= minRange) {
                            for (int y = tileY - maxRange; y <= tileY + maxRange; ++y) {
                                if (Math.abs(y - tileY) >= minRange) {
                                    int mobX = x * 32 + moveOffset.x;
                                    int mobY = y * 32 + moveOffset.y;
                                    if (!player.collidesWith(player.getLevel(), mobX, mobY)) {
                                        possiblePoints.add(new Point(mobX, mobY));
                                    }
                                }
                            }
                        }
                    }

                    if (!possiblePoints.isEmpty()) {
                        Point point = possiblePoints.get(GameRandom.globalRandom.nextInt(possiblePoints.size()));
                        server.network.sendToClientsAtEntireLevel(new AphRuneOfUnstableGelSlimePacket(serverClient.slot, point.x, point.y), serverClient.getLevel());
                    }
                }
            }
        });

        // RUNE OF EVIL'S PROTECTOR
        baseEffectNumber = 50;
        // On activation
        BuffRegistry.registerBuff("runeofevilsprotector", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofevilsprotectoractive"));
        // On duration
        BuffRegistry.registerBuff("runeofevilsprotectoractive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue<>(BuffModifiers.SPEED, -0.5F)) {
            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                Level level = buff.owner.getLevel();
                if (level != null) {
                    boolean night = !level.isCave && level.getWorldEntity().isNight();
                    buff.addModifier(BuffModifiers.ATTACK_SPEED, effectNumber / 100 * (night ? 2 : 1));
                }
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.demonic).height(16.0F);
                }
            }
        });

        // RUNE OF QUEEN SPIDER
        baseEffectNumber = 14;
        // On activation
        BuffRegistry.registerBuff("runeofqueenspider", new AphBaseRuneTrinketBuff(baseEffectNumber, 6000) {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.addLevelEvent(new AphRuneOfQueenSpiderEvent(player, (int) player.x, (int) player.y, (int) (getEffectNumber(player) * 1000), GameRandom.globalRandom));
            }

        });

        // RUNE OF VOID WIZARD
        baseEffectNumber = 50;
        extraEffectNumberMod = 2;
        // On activation
        BuffRegistry.registerBuff("runeofvoidwizard", new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 10000, "runeofvoidwizardactive"));
        // On duration
        BuffRegistry.registerBuff("runeofvoidwizardactive", new AphBaseRuneActiveBuff(baseEffectNumber, extraEffectNumberMod, 20000, new ModifierValue<>(BuffModifiers.PROJECTILE_BOUNCES, Integer.MAX_VALUE)) {

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.PROJECTILE_VELOCITY, effectNumber / 100);
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.palettePinkWitch[2]).height(16.0F);
                }
            }

        });

        // RUNE OF SWAMP GUARDIAN
        baseEffectNumber = 3;
        // On activation
        BuffRegistry.registerBuff("runeofswampguardian", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) (baseEffectNumber * 1000), "runeofswampguardianactive") {

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }

        });
        // On duration
        BuffRegistry.registerBuff("runeofswampguardianactive", new AphBaseRuneActiveBuff(baseEffectNumber, 25000, new ModifierValue<>(BuffModifiers.INCOMING_DAMAGE_MOD, 0.8F)) {

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.nature).height(16.0F);
                }
            }

        });

        // RUNE OF ANCIENT VULTURE
        baseEffectNumber = 10;
        // On activation
        BuffRegistry.registerBuff("runeofancientvulture", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000) {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    RunicFlyingAttackingFollowingMob mob = (RunicFlyingAttackingFollowingMob) MobRegistry.getMob("runicvulturehatchling", player.getLevel());
                    player.serverFollowersManager.addFollower("runicvulturehatchlings", mob, FollowPosition.WALK_CLOSE, "runeofancientvulture", 1, 1, (MserverClient, Mmob) -> ((RunicFlyingAttackingFollowingMob) Mmob).updateEffectNumber(getEffectNumber(player)), true);
                    mob.getLevel().entityManager.addMob(mob, player.x, player.y);
                }
            }

        }).setHealthCost(0.05F);

        // RUNE OF PIRATE CAPTAIN
        baseEffectNumber = 1F;
        // On activation
        BuffRegistry.registerBuff("runeofpiratecaptain", new AphBaseRuneTrinketBuff(baseEffectNumber, 8000, "runeofpiratecaptainactive"));
        // On duration
        BuffRegistry.registerBuff("runeofpiratecaptainactive", new AphBaseRuneActiveBuff(baseEffectNumber, 16000) {

            int bound;

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);

                updateCoins(buff);
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && bound == 0 || GameRandom.globalRandom.nextInt(bound) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.gold).height(16.0F);
                }
            }

            public void updateCoins(ActiveBuff buff) {
                PlayerMob player = (PlayerMob) buff.owner;

                int coins = Math.min(
                        player.getInv().getAmount(ItemRegistry.getItem("coin"), false, false, false, false, "buy"),
                        20000
                ) / 200;
                bound = Math.max(4 - coins / 50, 0);
                buff.setModifier(BuffModifiers.ALL_DAMAGE, coins * getEffectNumber(player) / 100);
            }

            @Override
            public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob mob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack) {
                super.onItemAttacked(buff, targetX, targetY, mob, attackHeight, item, slot, animAttack);
                if (mob.isServer() && mob.isPlayer) {
                    ((PlayerMob) mob).getInv().removeItems(ItemRegistry.getItem("coin"), 3, false, false, false, false, "buy");
                }
                updateCoins(buff);
            }
        });

    }

    public static void tier2BaseRunes() {
        float baseEffectNumber, extraEffectNumberMod;

        // RUNE OF REAPER
        baseEffectNumber = 200;
        // On activation
        BuffRegistry.registerBuff("runeofreaper", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000) {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                float range = getEffectNumber(player);
                if (player.getDistance(targetX, targetY) > range) {
                    preventUsage = true;
                    if (level.isClient()) {
                        new AphAreaList(new AphArea(range, AphColors.tungsten)).executeClient(level, player.x, player.y, 1F, 1F, 0F);
                    }
                } else if (level.getObject(targetX / 32, targetY / 32).isSolid || level.isTrialRoom) {
                    preventUsage = true;
                } else {
                    if (level.isClient()) {
                        player.getLevel().entityManager.addParticle(new SmokePuffParticle(player.getLevel(), player.x, player.y, AphColors.tungsten), Particle.GType.CRITICAL);
                        player.getLevel().entityManager.addParticle(new SmokePuffParticle(player.getLevel(), targetX, targetY, AphColors.tungsten), Particle.GType.CRITICAL);
                    }
                    player.setPos((float) targetX, (float) targetY, true);
                }
                super.run(level, player, targetX, targetY);
            }
        }).setHealthCost(0.05F);

        // RUNE OF CRYO QUEEN
        baseEffectNumber = 300;
        // On activation
        BuffRegistry.registerBuff("runeofcryoqueen", new AphBaseRuneTrinketBuff(baseEffectNumber, 30000) {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.addLevelEvent(new AphRuneOfCryoQueenEvent(player, (int) player.x, (int) player.y, GameRandom.globalRandom.getFloatBetween(0, 360), GameRandom.globalRandom.nextBoolean(), getEffectNumber(player)));
                player.buffManager.addBuff(new ActiveBuff(AphBuffs.STOP, player, 1000, null), true);
            }

        });

        // RUNE OF PEST WARDEN
        baseEffectNumber = 12;
        // On activation
        BuffRegistry.registerBuff("runeofpestwarden", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) baseEffectNumber * 1000, "runeofpestwardenactive") {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                player.getLevel().entityManager.addLevelEvent(new AphRuneOfPestWardenEvent(player));
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }

        });
        // On duration
        BuffRegistry.registerBuff("runeofpestwardenactive", new AphBaseRuneActiveBuff(baseEffectNumber, 60000, new ModifierValue<>(BuffModifiers.PARALYZED, true), new ModifierValue<>(BuffModifiers.INCOMING_DAMAGE_MOD, -1000F), new ModifierValue<>(BuffModifiers.SPEED, 4F)) {

            float angle;
            boolean initAngle = true;

            final float maxDeltaAngle = (float) Math.toRadians(10);
            final float speed = 20;

            @Override
            public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
                super.init(buff, eventSubscriber);
                buff.owner.setDir(2);
                initAngle = true;
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                PlayerMob player = (PlayerMob) buff.owner;

                MainGame mainGame = (MainGame) GlobalData.getCurrentState();
                MainGameCamera camera = mainGame.getCamera();

                float mouseLevelX = camera.getMouseLevelPosX();
                float mouseLevelY = camera.getMouseLevelPosY();

                float newAngle = (float) Math.atan2(mouseLevelY - player.y, mouseLevelX - player.x);

                if (initAngle) {
                    initAngle = false;
                    angle = newAngle;
                } else {
                    float deltaAngle = normalizeAngle(newAngle - angle);

                    if (Math.abs(deltaAngle) > maxDeltaAngle) {
                        angle += Math.signum(deltaAngle) * maxDeltaAngle;
                    } else {
                        angle = newAngle;
                    }

                    angle = normalizeAngle(angle);
                }

                float speedX = speed * (float) Math.cos(angle);
                float speedY = speed * (float) Math.sin(angle);

                player.setPos(player.x + speedX, player.y + speedY, true);

                player.getLevel().entityManager.addParticle(player.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), player.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(player.dx / 2, player.dy / 2).color(AphColors.nature).height(16.0F);
            }

            @Override
            public void onRemoved(ActiveBuff buff) {
                super.onRemoved(buff);
            }

            private float normalizeAngle(float angle) {
                while (angle <= -Math.PI) angle += (float) (2 * Math.PI);
                while (angle > Math.PI) angle -= (float) (2 * Math.PI);
                return angle;
            }
        });

        // RUNE OF SAGE & GRIT
        baseEffectNumber = 20;
        // On activation
        BuffRegistry.registerBuff("runeofsageandgrit", new AphBaseRuneTrinketBuff(baseEffectNumber, 20000, "runeofsageandgritactive") {
            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                if (level.isServer()) {
                    List<Mob> arrayList = player.getLevel().entityManager.streamAreaMobsAndPlayers(player.x, player.y, 500).filter(target -> target != player && AphMagicHealing.canHealMob(player, target)).collect(Collectors.toList());

                    if (!arrayList.isEmpty()) {
                        preventBuff = true;
                        temporalBuff = false;

                        arrayList.forEach(
                                target -> target.getLevel().entityManager.addLevelEvent(new MobHealthChangeEvent(target, (int) (target.getMaxHealth() * 0.2F)))
                        );

                        level.getServer().network.sendToClientsAtEntireLevel(new AphSingleAreaShowPacket(player.x, player.y, 500, AphColors.nature), level);
                    }
                }
            }
        });
        // On duration
        BuffRegistry.registerBuff("runeofsageandgritactive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000) {
            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.ALL_DAMAGE, effectNumber / 100);
                buff.addModifier(BuffModifiers.ATTACK_SPEED, effectNumber / 100);
                buff.addModifier(BuffModifiers.SPEED, effectNumber / 100);
            }
        });

        // RUNE OF FALLEN WIZARD
        baseEffectNumber = 3;
        // On activation
        BuffRegistry.registerBuff("runeoffallenwizard", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) baseEffectNumber * 1000, "runeoffallenwizardactive") {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.streamAreaMobsAndPlayers(player.x, player.y, 1024 * 32).filter(target -> target != player).forEach(
                        mob -> {
                            float timeMod = 1F;
                            if (mob.isBoss()) {
                                timeMod = 0.25F;
                            } else if (mob.isPlayer || mob.isHuman) {
                                timeMod = 0.5F;
                            }
                            mob.addBuff(new ActiveBuff(AphBuffs.FALLEN_STUN, player, getEffectNumber(player) * timeMod, player), true);
                        }
                );
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }

        });
        // On duration
        BuffRegistry.registerBuff("runeoffallenwizardactive", new AphBaseRuneActiveBuff(baseEffectNumber, 18000));

    }

    public static void tier3BaseRunes() {
        float baseEffectNumber, extraEffectNumberMod;

        // RUNE OF MOTHER SLIME
        baseEffectNumber = 60;
        // On activation
        BuffRegistry.registerBuff("runeofmotherslime", new AphBaseRuneTrinketBuff(baseEffectNumber, 24000) {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float range = 600;
                if (player.getDistance(targetX, targetY) > range) {
                    preventUsage = true;
                    if (level.isClient()) {
                        new AphAreaList(new AphArea(range, AphColors.paletteMotherSlime)).executeClient(level, player.x, player.y, 1F, 1F, 0F);
                    }
                } else if (level.getObject(targetX / 32, targetY / 32).isSolid || level.isTrialRoom) {
                    preventUsage = true;
                } else {
                    player.dismount();
                    if (level.isServer()) {
                        player.getLevel().entityManager.addLevelEvent(new AphRuneOfMotherSlimeEvent(player, targetX, targetY, getEffectNumber(player)));
                        player.buffManager.addBuff(new ActiveBuff(AphBuffs.STOP, player, 1000, null), true);
                    }
                }
            }

        });

        // RUNE OF NIGHT SWARM
        baseEffectNumber = 5;
        // On activation
        BuffRegistry.registerBuff("runeofnightswarm", new AphBaseRuneTrinketBuff(baseEffectNumber, (int) baseEffectNumber * 1000, "runeofnightswarmactive") {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    for (int i = 0; i < 5; i++) {
                        RunicFlyingAttackingFollowingMob mob = (RunicFlyingAttackingFollowingMob) MobRegistry.getMob("runicbat", player.getLevel());
                        player.serverFollowersManager.addFollower("runicbats", mob, FollowPosition.WALK_CLOSE, "runeofnightswarm", 1, 8, (MserverClient, Mmob) -> ((RunicFlyingAttackingFollowingMob) Mmob).updateEffectNumber(getEffectNumber(player)), true);
                        mob.getLevel().entityManager.addMob(mob, player.x, player.y);
                    }
                }
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int) getEffectNumber(player) * 1000;
            }

        });
        // On duration
        BuffRegistry.registerBuff("runeofnightswarmactive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000));

        // RUNE OF SPIDER EMPRESS
        baseEffectNumber = 7;
        extraEffectNumberMod = 2;
        // On activation
        BuffRegistry.registerBuff("runeofspiderempress", new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 10000) {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);

                float initialAngle = (float) Math.toDegrees(Math.atan2(targetY - player.y, targetX - player.x)) + 45;

                int iterations = Math.max(Math.round(getEffectNumber(player)), 2);
                float anglePerProjectile = (float) (90 / (iterations - 1));

                for (int i = 0; i < iterations; i++) {
                    float currentAngle = initialAngle + anglePerProjectile * i;
                    player.getLevel().entityManager.projectiles.add(new RuneOfSpiderEmpressProjectile(player.x, player.y, currentAngle, new GameDamage(0), 120.0F, player));
                }
            }

        });

        // RUNE OF SUNLIGHT CHAMPION
        baseEffectNumber = 600;
        // On activation
        BuffRegistry.registerBuff("runeofsunlightchampion", new AphBaseRuneTrinketBuff(baseEffectNumber, 40000) {

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.addLevelEvent(new AphRuneOfSunlightChampionEvent((int) getEffectNumber(player), player));
                player.buffManager.addBuff(new ActiveBuff(AphBuffs.STUN, player, 3000, null), true);
            }
        });

        // RUNE OF MOONLIGHT DANCER
        baseEffectNumber = 0.5F;
        // On activation
        BuffRegistry.registerBuff("runeofmoonlightdancer", new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofmoonlightdanceractive"));
        // On duration
        BuffRegistry.registerBuff("runeofmoonlightdanceractive", new AphBaseRuneActiveBuff(baseEffectNumber, 20000) {

            int bound;

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);

                updateBuff(buff);
            }

            @Override
            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                updateBuff(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && bound == 0 || GameRandom.globalRandom.nextInt(bound) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(new Color(0, 0, 0)).height(16.0F);
                }
            }

            @Override
            public void serverTick(ActiveBuff buff) {
                super.serverTick(buff);
                updateBuff(buff);
            }

            public void updateBuff(ActiveBuff buff) {
                PlayerMob player = (PlayerMob) buff.owner;

                float speedModifier = player.getSpeedModifier() - 1;
                bound = (int) Math.max(4 - speedModifier / 25, 0);
                buff.setModifier(BuffModifiers.ALL_DAMAGE, speedModifier * getEffectNumber(player));
            }
        });

        // RUNE OF CRYSTAL DRAGON
        baseEffectNumber = 20;
        // On activation
        BuffRegistry.registerBuff("runeofcrystaldragon", new AphBaseRuneTrinketBuff(baseEffectNumber, 25000) {

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float angle = (float) Math.toDegrees(Math.atan2(targetY - player.y, targetX - player.x));
                player.getLevel().entityManager.addLevelEvent(new AphRuneOfCrystalDragonEvent(player, new GameRandom(), 1000, getEffectNumber(player) / 100, 100, 5000, angle));
            }

        });

    }

    public static void tier0ModifierRunes() {
        // Empowering Rune
        BuffRegistry.registerBuff("empoweringrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(0.2F)
                .setCooldownVariation(0.2F)
        );

        // Recurrent Rune
        BuffRegistry.registerBuff("recurrentrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(-0.2F)
                .setCooldownVariation(-0.2F)
        );

        // Devastating Rune
        BuffRegistry.registerBuff("devastatingrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(0.3F)
                .setHealthCost(0.1F)
        );
    }

    public static void tier1ModifierRunes() {
        // Frenzy Rune
        BuffRegistry.registerBuff("frenzyrune", new AphModifierRuneTrinketBuff()
                .setCooldownVariation(-0.4F)
                .setHealthCost(0.1F)
        );

        // Vital Rune
        BuffRegistry.registerBuff("vitalrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(-0.2F)
                .setCooldownVariation(0.1F)
                .setHealthCost(-0.05F)
        );

        // Onyx Rune
        BuffRegistry.registerBuff("onyxrune", new AphModifierRuneTrinketBuff() {
                    @Override
                    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                        super.runServer(server, player, targetX, targetY);
                        if (player.isServerClient()) {
                            Mob mob = MobRegistry.getMob("onyx", player.getLevel());
                            player.serverFollowersManager.addFollower("onyx", mob, FollowPosition.WALK_CLOSE, "onyxrune", 1, 1, null, true);
                            mob.getLevel().entityManager.addMob(mob, player.x, player.y);
                        }
                    }
                }
                        .setEffectNumberVariation(-0.2F)
                        .setHealthCost(0.05F)
        );


        // Pawning Rune
        BuffRegistry.registerBuff("pawningrune", new AphModifierRuneTrinketBuff() {
                    @Override
                    public void run(Level level, PlayerMob player, int targetX, int targetY) {
                        super.run(level, player, targetX, targetY);
                        if (!player.buffManager.hasBuff("pawningruneactive")) {
                            player.buffManager.addBuff(new ActiveBuff("pawningruneactive", player, 10000, null), false);
                            SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(player).volume(1.0F).pitch(1.0F));
                        }
                    }
                }
        );
        BuffRegistry.registerBuff("pawningruneactive", new AphShownBuff() {
            int pawnHealing;

            @Override
            public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
                if (activeBuff.owner.isServer()) {
                    int healing = Math.min((int) (activeBuff.owner.getMaxHealth() * 0.2F), activeBuff.owner.getMaxHealth() - activeBuff.owner.getHealth());
                    LevelEvent changeHeal = new MobHealthChangeEvent(activeBuff.owner, healing);
                    activeBuff.owner.getLevel().entityManager.addLevelEvent(changeHeal);
                    pawnHealing = (int) (healing * 1.4F);
                }
            }

            @Override
            public void onRemoved(ActiveBuff buff) {
                super.onRemoved(buff);
                if (pawnHealing > 0) {
                    if (buff.owner.isServer()) {
                        LevelEvent changeHeal = new MobHealthChangeEvent(buff.owner, -pawnHealing);
                        buff.owner.getLevel().entityManager.addLevelEvent(changeHeal);
                    } else {
                        SoundManager.playSound(GameResources.npchurt, SoundEffect.effect(buff.owner).pitch(GameRandom.globalRandom.getOneOf(0.95F, 1.0F, 1.05F)));
                    }
                }
            }

            @Override
            public void drawIcon(int x, int y, ActiveBuff buff) {
                super.drawIcon(x, y, buff);
                String text = Integer.toString(pawnHealing);
                int width = FontManager.bit.getWidthCeil(text, durationFontOptions);
                FontManager.bit.drawString((float) (x + 28 - width), (float) (y + 30 - FontManager.bit.getHeightCeil(text, durationFontOptions)), text, durationFontOptions);
            }
        });
    }

    public static void tier2ModifierRunes() {
        // Abysmal Rune
        BuffRegistry.registerBuff("abysmalrune", new AphModifierRuneTrinketBuff() {
                    @Override
                    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                        super.runServer(server, player, targetX, targetY);
                        if (!player.buffManager.hasBuff("abysmalrunecooldown")) {
                            player.getLevel().entityManager.addLevelEvent(new AphAbysmalRuneEvent(player, targetX, targetY));
                            player.buffManager.addBuff(new ActiveBuff("abysmalrunecooldown", player, 10000, null), true);
                        }
                    }
                }.setHealthCost(0.1F)
        );
        BuffRegistry.registerBuff("abysmalrunecooldown", new AphShownCooldownBuff());

        // Tidal Rune
        BuffRegistry.registerBuff("tidalrune", new AphModifierRuneTrinketBuff() {

                    @Override
                    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                        super.runServer(server, player, targetX, targetY);
                        player.getLevel().entityManager.addLevelEvent(new AphTidalRuneEvent(player));
                        player.buffManager.forceUpdateBuffs();
                    }

                    @Override
                    public void runClient(Client client, PlayerMob player, int targetX, int targetY) {
                        super.runClient(client, player, targetX, targetY);

                        float maxDist = 128.0F;
                        int lifeTime = 1100;
                        int minHeight = 0;
                        int maxHeight = 30;
                        int particles = 77;

                        for (int i = 0; i < particles; ++i) {
                            float height = (float) minHeight + (float) (maxHeight - minHeight) * (float) i / (float) particles;
                            AtomicReference<Float> currentAngle = new AtomicReference<>(GameRandom.globalRandom.nextFloat() * 360.0F);
                            float outDistance = GameRandom.globalRandom.getFloatBetween(60.0F, maxDist + 32.0F);
                            boolean counterclockwise = GameRandom.globalRandom.nextBoolean();
                            player.getLevel().entityManager.addParticle(player.x + GameRandom.globalRandom.getFloatBetween(0.0F, GameMath.sin(currentAngle.get()) * maxDist), player.y + GameRandom.globalRandom.getFloatBetween(0.0F, GameMath.cos(currentAngle.get()) * maxDist * 0.75F), Particle.GType.CRITICAL).color(GameRandom.globalRandom.getOneOf(AphColors.paletteOcean)).height(height).moves((pos, delta, cLifeTime, timeAlive, lifePercent) -> {
                                float angle = currentAngle.accumulateAndGet(delta * 150.0F / 250.0F, Float::sum);
                                if (counterclockwise) {
                                    angle = -angle;
                                }

                                float linearDown = GameMath.lerpExp(lifePercent, 0.525F, 0.0F, 1.0F);
                                pos.x = player.x + outDistance * GameMath.sin(angle) * linearDown;
                                pos.y = player.y + outDistance * GameMath.cos(angle) * linearDown * 0.75F;
                            }).lifeTime(lifeTime).sizeFades(14, 18);
                        }

                    }
                }.setCooldownVariation(0.1F)
        );
    }

    public static void tier3ModifierRunes() {
        // Ascendant Rune
        BuffRegistry.registerBuff("ascendantrune", new AphModifierRuneTrinketBuff()
                .setEffectNumberVariation(1F)
                .setCooldownVariation(1F)
        );
    }

    public static class MIGHTY_BANNER {
        public static AphBannerBuff FISHING;
        public static AphBannerBuff FISHING_GREATER;

        public static AphBannerBuff HEALTH_REGEN;
        public static AphBannerBuff HEALTH_REGEN_GREATER;

        public static AphBannerBuff MANA_REGEN;
        public static AphBannerBuff MANA_REGEN_GREATER;

        public static AphBannerBuff RESISTANCE;
        public static AphBannerBuff RESISTANCE_GREATER;

        public static AphBannerBuff SUMMONING;
        public static AphBannerBuff SUMMONING_GREATER;

        public static AphBannerBuff ATTACK_SPEED;
        public static AphBannerBuff ATTACK_SPEED_GREATER;
    }

    public static void registerMightyBannerItems() {
        if(AphDependencies.checkMightyBanner()) {
            BuffRegistry.registerBuff("aph_banneroffishing_normal", MIGHTY_BANNER.FISHING = AphBasicBannerBuff.intModifier(BuffModifiers.FISHING_POWER, 20));
            BuffRegistry.registerBuff("aph_banneroffishing_greater", MIGHTY_BANNER.FISHING_GREATER = AphBasicBannerBuff.intModifier(BuffModifiers.FISHING_POWER, 30));

            BuffRegistry.registerBuff("aph_bannerofhealthregen_normal", MIGHTY_BANNER.HEALTH_REGEN = AphBasicBannerBuff.floatModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5F));
            BuffRegistry.registerBuff("aph_bannerofhealthregen_greater", MIGHTY_BANNER.HEALTH_REGEN_GREATER = AphBasicBannerBuff.floatModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 1F));

            BuffRegistry.registerBuff("aph_bannerofmanaregen_normal", MIGHTY_BANNER.MANA_REGEN = AphBasicBannerBuff.floatModifier(BuffModifiers.COMBAT_MANA_REGEN, 2F));
            BuffRegistry.registerBuff("aph_bannerofmanaregen_greater", MIGHTY_BANNER.MANA_REGEN_GREATER = AphBasicBannerBuff.floatModifier(BuffModifiers.COMBAT_MANA_REGEN, 4F));

            BuffRegistry.registerBuff("aph_bannerofresistance_normal", MIGHTY_BANNER.RESISTANCE = AphBasicBannerBuff.intModifier(BuffModifiers.ARMOR_FLAT, 8));
            BuffRegistry.registerBuff("aph_bannerofresistance_greater", MIGHTY_BANNER.RESISTANCE_GREATER = AphBasicBannerBuff.intModifier(BuffModifiers.ARMOR_FLAT, 12));

            BuffRegistry.registerBuff("aph_bannerofsummoning_normal", MIGHTY_BANNER.SUMMONING = AphBasicBannerBuff.intModifier(BuffModifiers.MAX_SUMMONS, 1));
            BuffRegistry.registerBuff("aph_bannerofsummoning_greater", MIGHTY_BANNER.SUMMONING_GREATER = AphBasicBannerBuff.intModifier(BuffModifiers.MAX_SUMMONS, 2));

            BuffRegistry.registerBuff("aph_bannerofattackspeed_normal", MIGHTY_BANNER.ATTACK_SPEED = AphBasicBannerBuff.floatModifier(BuffModifiers.ATTACK_SPEED, 0.15F));
            BuffRegistry.registerBuff("aph_bannerofattackspeed_greater", MIGHTY_BANNER.ATTACK_SPEED_GREATER = AphBasicBannerBuff.floatModifier(BuffModifiers.ATTACK_SPEED, 0.20F));

        }
    }
}