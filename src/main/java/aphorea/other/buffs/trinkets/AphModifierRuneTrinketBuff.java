package aphorea.other.buffs.trinkets;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.level.maps.Level;

public class AphModifierRuneTrinketBuff extends TrinketBuff {
    private float effectNumberVariation;
    private float cooldownVariation;
    private float healthCost;

    public AphModifierRuneTrinketBuff() {
    }

    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    public float getEffectNumberVariation() {
        return effectNumberVariation;
    }

    public float getCooldownVariation() {
        return cooldownVariation;
    }

    public float getHealthCost(PlayerMob player) {
        return healthCost;
    }

    public AphModifierRuneTrinketBuff setEffectNumberVariation(float effectNumberVariation) {
        this.effectNumberVariation = effectNumberVariation;
        return this;
    }

    public AphModifierRuneTrinketBuff setCooldownVariation(float cooldownVariation) {
        this.cooldownVariation = cooldownVariation;
        return this;
    }

    public AphModifierRuneTrinketBuff setHealthCost(float healthCost) {
        this.healthCost = healthCost;
        return this;
    }

    public void runServer(Server server, PlayerMob player) {
        run(player.getLevel(), player);
    }

    public void runClient(Client client, PlayerMob player) {
        run(client.getLevel(), player);
    }

    public void run(Level level, PlayerMob player) {
    }
}