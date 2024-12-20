package aphorea.other.buffs.trinkets;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.level.maps.Level;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class AphBaseRuneTrinketBuff extends TrinketBuff {

    private final float baseEffectNumber;
    private final int duration;
    private final String buff;
    private final boolean isTemporary;

    private float healthCost;

    private AphBaseRuneTrinketBuff(float baseEffectNumber, int duration, String buff, boolean isTemporary) {
        this.baseEffectNumber = baseEffectNumber;
        this.duration = duration;
        this.buff = buff;
        this.isTemporary = isTemporary;
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int duration) {
        this(baseEffectNumber, duration, null, false);
    }

    public AphBaseRuneTrinketBuff(float baseEffectNumber, int duration, String buff) {
        this(baseEffectNumber, duration, buff, true);
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public String canRun(PlayerMob player) {
        if(player.getHealthPercent() <= getHealthCost(player)) {
            return "insuficienthealth";
        }
        if(!player.buffManager.hasBuff("runesinjectoractive") && !player.buffManager.hasBuff("runesinjectorcooldown")) {
            return "";
        }
        return null;
    }

    public void runServer(Server server, PlayerMob player) {
        int duration = getDuration(player);
        if(duration > 0) {
            if(buff != null) {
                ActiveBuff giveBuff = new ActiveBuff(buff, player, isTemporary ? duration : getCooldownDuration(player), null);
                player.buffManager.addBuff(giveBuff, true);
            }

            ActiveBuff giveBuff2;
            if(isTemporary) {
                giveBuff2 = new ActiveBuff("runesinjectoractive", player, duration, null);
            } else {
                giveBuff2 = new ActiveBuff("runesinjectorcooldown", player, getCooldownDuration(player), null);
            }
            player.buffManager.addBuff(giveBuff2, true);
        }
        float healthCost = getHealthCost(player);
        if(healthCost != 0) {
            LevelEvent changeHeal = new MobHealthChangeEvent(player, (int) (-healthCost * player.getMaxHealth()));
            player.getLevel().entityManager.addLevelEvent(changeHeal);
        }
        run(player.getLevel(), player);
        getRuneModifiers(player).forEach(
                b -> b.runServer(server, player)
        );
    }

    public void runClient(Client client, PlayerMob player) {
        run(client.getLevel(), player);
        getRuneModifiers(player).forEach(
                b -> b.runClient(client, player)
        );
    }

    public void run(Level level, PlayerMob player) {
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
                b -> variation.updateAndGet(v -> v + b.getHealthCost(player))
        );
        return healthCost + variation.get() - getHealing(player);
    }

    public float getBaseHealing() {
        return 0;
    }

    public float getHealing(PlayerMob player) {
        return getBaseHealing();
    }

    public float getBaseEffectNumber() {
        return baseEffectNumber;
    }

    public float getEffectNumber(PlayerMob player) {
        return getBaseEffectNumber() * getEffectNumberVariation(player);
    }

    public static float getCooldownVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<>(0F);
        getRuneModifiers(player).forEach(
                b -> variation.updateAndGet(v -> v + b.getCooldownVariation())
        );
        return Math.max(variation.get(), 0.1F);
    }

    public static float getEffectNumberVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<>(0F);
        getRuneModifiers(player).forEach(
                b -> variation.updateAndGet(v -> v + b.getEffectNumberVariation())
        );
        return Math.max(1F + variation.get(), 0);
    }

    public static Stream<AphModifierRuneTrinketBuff> getRuneModifiers(PlayerMob player) {
        return player.buffManager.getBuffs().values().stream().filter(b -> b.buff instanceof AphModifierRuneTrinketBuff).map(b -> (AphModifierRuneTrinketBuff) b.buff);
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public String getBuff() {
        return buff;
    }
}
