package aphorea.buffs.Runes;

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
        this.effectNumberVariation = 0;
        this.cooldownVariation = 0;
        this.healthCost = 0;
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

    public float getHealthCost() {
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

    public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
        run(player.getLevel(), player, targetX, targetY);
    }

    public void runClient(Client client, PlayerMob player, int targetX, int targetY) {
        run(client.getLevel(), player, targetX, targetY);
    }

    public void run(Level level, PlayerMob player, int targetX, int targetY) {
    }
}