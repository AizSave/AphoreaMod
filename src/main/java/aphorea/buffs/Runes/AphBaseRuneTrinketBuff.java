package aphorea.buffs.Runes;

import aphorea.items.runes.AphRunesInjector;
import aphorea.registry.AphBuffs;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AphBaseRuneTrinketBuff extends TrinketBuff {

    protected float baseEffectNumber;
    protected float extraEffectNumberMod;
    protected final int duration;
    protected String buff;
    protected final boolean isTemporary;

    /**
     * Server only
     */
    protected float durationModifier;
    /**
     * Server only
     */
    protected float cooldownModifier;
    /**
     * Server and client
     */
    protected boolean preventUsage;
    /**
     * Server only
     */
    protected boolean preventCooldown;
    /**
     * Server only
     */
    protected boolean preventBuff;
    /**
     * Server only
     */
    protected boolean temporalBuff;

    protected float healthCost;

    private AphBaseRuneTrinketBuff(float baseEffectNumber, float extraEffectNumberMod, int duration, String buff, boolean isTemporary) {
        this.baseEffectNumber = baseEffectNumber;
        this.extraEffectNumberMod = extraEffectNumberMod;
        this.duration = duration;
        this.buff = buff;
        this.isTemporary = isTemporary;
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, float extraEffectNumberMod, int duration, String buff) {
        this(baseEffectNumber, extraEffectNumberMod, duration, buff, true);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int duration, String buff, boolean isTemporary) {
        this(baseEffectNumber, 1F, duration, buff, isTemporary);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int duration, String buff) {
        this(baseEffectNumber, 1F, duration, buff, true);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, float extraEffectNumberMod, int cooldownDuration) {
        this(baseEffectNumber, extraEffectNumberMod, cooldownDuration, null, false);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int cooldownDuration) {
        this(baseEffectNumber, 1F, cooldownDuration, null, false);
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public String canRun(PlayerMob player) {
        if (player.buffManager.hasBuff(AphBuffs.RUNE_INJECTOR_ACTIVE) || player.buffManager.hasBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN)) {
            return "";
        }
        if (player.getLevel().isTrialRoom) {
            return "cannotusetrial";
        }
        if (player.getHealthPercent() <= getHealthCost(player)) {
            return "insuficienthealth";
        }
        return null;
    }

    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
        initRun(player.getLevel(), player, targetX, targetY);
        if (!preventUsage) run(player.getLevel(), player, targetX, targetY);
        if (!preventUsage) {
            int duration = getDuration(player);
            if (duration > 0) {
                if (buff != null && !preventBuff) {
                    player.buffManager.addBuff(new ActiveBuff(buff, player, temporalBuff ? (int) (duration * durationModifier) : (int) (getCooldownDuration(player) * cooldownModifier), null), true);
                }

                if (temporalBuff) {
                    player.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_ACTIVE, player, (int) (duration * durationModifier), null), true);
                } else if (!preventCooldown) {
                    player.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN, player, (int) (getCooldownDuration(player) * cooldownModifier), null), true);
                }
            }
            List<AphModifierRuneTrinketBuff> modifiersList = getRuneModifiers(player)
                    .collect(Collectors.toList());
            for (AphModifierRuneTrinketBuff aphModifierRuneTrinketBuff : modifiersList) {
                aphModifierRuneTrinketBuff.runServer(server, player, targetX, targetY);
            }
        }
        postRun(player.getLevel(), player, targetX, targetY);
    }

    public void runClient(Client client, PlayerMob player, int targetX, int targetY) {
        initRun(client.getLevel(), player, targetX, targetY);
        if (!preventUsage) run(client.getLevel(), player, targetX, targetY);
        if (!preventUsage) {
            List<AphModifierRuneTrinketBuff> modifiersList = getRuneModifiers(player)
                    .collect(Collectors.toList());
            for (AphModifierRuneTrinketBuff aphModifierRuneTrinketBuff : modifiersList) {
                aphModifierRuneTrinketBuff.runClient(client, player, targetX, targetY);
            }
        }
        postRun(client.getLevel(), player, targetX, targetY);
    }

    public void initRun(Level level, PlayerMob player, int targetX, int targetY) {
        durationModifier = 1F;
        cooldownModifier = 1F;
        preventUsage = false;
        preventCooldown = false;
        preventBuff = false;
        temporalBuff = isTemporary;
    }

    public void run(Level level, PlayerMob player, int targetX, int targetY) {
        if (!preventUsage) {
            float healthCost = getHealthCost(player);
            if (healthCost != 0) {
                int healthMod = (int) (-healthCost * player.getMaxHealth());
                if(healthMod != 0) {
                    if (level.isServer()) {
                        player.getLevel().entityManager.addLevelEvent(new MobHealthChangeEvent(player, healthMod));
                    } else if (level.isClient()) {
                        if (healthCost > 0) {
                            SoundManager.playSound(GameResources.npchurt, SoundEffect.effect(player).pitch(GameRandom.globalRandom.getOneOf(0.95F, 1.0F, 1.05F)));
                        } else {
                            SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(player).volume(1.0F).pitch(1.0F));
                        }
                    }
                }
            }
        }
    }

    public void postRun(Level level, PlayerMob player, int targetX, int targetY) {
    }

    public int getDuration(PlayerMob player) {
        return duration;
    }

    public int getBaseCooldown() {
        return duration;
    }

    public int getCooldownDuration(PlayerMob player) {
        return (int) (duration * getCooldownVariation(player));
    }

    public AphBaseRuneTrinketBuff setHealthCost(float healthCost) {
        this.healthCost = healthCost;
        return this;
    }

    public float getBaseHealthCost() {
        return healthCost - getBaseHealing();
    }

    public float getHealthCost(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<>(0F);
        getRuneModifiers(player).forEach(
                b -> variation.updateAndGet(v -> v + b.getHealthCost())
        );
        return healthCost + variation.get() - getHealing(getEffectNumberVariation(player));
    }

    public float getBaseHealing() {
        return 0;
    }

    public float getHealing(float effectNumberVariation) {
        return getBaseHealing();
    }

    public float getBaseEffectNumber() {
        return baseEffectNumber;
    }

    public float getEffectNumber(PlayerMob player) {
        return getBaseEffectNumber() * getEffectNumberVariation(player);
    }

    public static float getCooldownVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<>(1F);
        getRuneModifiers(player).forEach(
                b -> variation.updateAndGet(v -> v + b.getCooldownVariation())
        );
        return Math.max(variation.get(), 0.1F);
    }

    public float getEffectNumberVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<>(1F);
        getRuneModifiers(player).forEach(
                b -> variation.updateAndGet(v -> v + b.getEffectNumberVariation())
        );
        return Math.max(1 + (variation.get() - 1) * extraEffectNumberMod, 0);
    }

    public float getEffectNumberVariation(InventoryItem item, AphRunesInjector runesInjector) {
        AtomicReference<Float> variation = new AtomicReference<>(1F);
        runesInjector.getModifierBuffs(item).forEach(
                b -> variation.updateAndGet(v -> v + b.getEffectNumberVariation())
        );
        return Math.max(1 + (variation.get() - 1) * extraEffectNumberMod, 0);
    }

    public static float getCooldownVariation(InventoryItem item, AphRunesInjector runesInjector) {
        AtomicReference<Float> variation = new AtomicReference<>(1F);
        runesInjector.getModifierBuffs(item).forEach(
                b -> variation.updateAndGet(v -> v + b.getCooldownVariation())
        );
        return Math.max(variation.get(), 0);
    }

    public float getFinalHealthCost(InventoryItem item, AphRunesInjector runesInjector) {
        return getFinalHealthCost(item, runesInjector, getEffectNumberVariation(item, runesInjector));
    }

    public float getFinalHealthCost(InventoryItem item, AphRunesInjector runesInjector, float effectNumberVariation) {
        AtomicReference<Float> variation = new AtomicReference<>(0F);
        runesInjector.getModifierBuffs(item).forEach(
                b -> variation.updateAndGet(v -> v + b.getHealthCost())
        );
        return healthCost + variation.get() - getHealing(effectNumberVariation);
    }

    public static Stream<AphModifierRuneTrinketBuff> getRuneModifiers(PlayerMob player) {
        return player.buffManager.getBuffs().values().stream().filter(b -> b.buff instanceof AphModifierRuneTrinketBuff).map(b -> (AphModifierRuneTrinketBuff) b.buff);
    }

    public String getBuff() {
        return buff;
    }


    public float getExtraEffectNumberMod() {
        return extraEffectNumberMod;
    }

    public boolean isTemporary() {
        return isTemporary;
    }
}
