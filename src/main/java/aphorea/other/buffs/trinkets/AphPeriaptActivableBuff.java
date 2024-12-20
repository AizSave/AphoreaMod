package aphorea.other.buffs.trinkets;

import aphorea.other.utils.AphTimeout;
import aphorea.registry.AphBuffs;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.item.ItemStatTip;

import java.awt.*;
import java.util.LinkedList;

abstract public class AphPeriaptActivableBuff extends TrinketBuff implements ActiveBuffAbility {

    public String activeBuff;

    public AphPeriaptActivableBuff(String activeBuff) {
        this.activeBuff = activeBuff;
    }

    abstract public Color getColor();

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !player.buffManager.hasBuff(AphBuffs.PERIAPT_ACTIVE) && !player.buffManager.hasBuff(AphBuffs.COOLDOWNS.PERIAPT_COOLDOWN);
    }

    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        player.buffManager.addBuff(new ActiveBuff(AphBuffs.PERIAPT_ACTIVE, player, 11.0F, null), false);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff(activeBuff), player, 11.0F, null), false);
        SoundManager.playSound(GameResources.magicroar, SoundEffect.effect(player)
                .volume(0.7f)
                .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        player.buffManager.addBuff(new ActiveBuff(AphBuffs.STOP, player, 1F, null), false);

        AphTimeout.setTimeout(
                () -> {
                    for (int i = 0; i < 40; i++) {
                        int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        player.getLevel().entityManager.addParticle(player.x - dx, player.y - dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(getColor()).heightMoves(30.0F, 10.0F).lifeTime(800);
                    }
                },
                100
        );
    }

    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        int angle = 180 + (int) (GameRandom.globalRandom.nextFloat() * 30.0F) - 15;
        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
        player.getLevel().entityManager.addParticle(player, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(getColor()).heightMoves(10.0F, 30.0F).lifeTime(500);

        return player.buffManager.hasBuff(activeBuff);
    }

    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        if (player.getLevel() != null) {
            SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(player)
                    .volume(0.7f)
                    .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
            for (int i = 0; i < 40; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                player.getLevel().entityManager.addParticle(player, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(getColor()).heightMoves(10.0F, 30.0F).lifeTime(1000);
            }

            if (player.buffManager.hasBuff(activeBuff)) {
                player.buffManager.removeBuff(activeBuff, false);
            }
        }
    }
}