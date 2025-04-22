package aphorea.buffs;

import aphorea.items.tools.weapons.melee.battleaxe.AphBattleaxeToolItem;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.AphTimeout;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BerserkerRushBuff extends Buff {
    public BerserkerRushBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        SoundManager.playSound(GameResources.roar, SoundEffect.effect(buff.owner)
                .volume(0.7f)
                .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        buff.owner.buffManager.addBuff(new ActiveBuff(AphBuffs.STOP, buff.owner, 1F, null), false);

        AphTimeout.setTimeout(() -> {
            for (int i = 0; i < 40; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                buff.owner.getLevel().entityManager.addParticle(buff.owner.x - dx, buff.owner.y - dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(AphColors.red).heightMoves(30.0F, 10.0F).lifeTime(800);
            }
        }, 100);

    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
            owner.getLevel().entityManager.addParticle(owner.x + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(AphColors.red).height(16.0F);
        }

        if (owner.isPlayer && !(((PlayerMob) owner).getSelectedItem().item instanceof AphBattleaxeToolItem)) {
            buff.remove();
        } else {
            updateBuffs(buff, AdrenalineBuff.getAdrenalineLevel(buff.owner));
        }
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        Mob owner = buff.owner;
        if (owner.isPlayer && !(((PlayerMob) owner).getSelectedItem().item instanceof AphBattleaxeToolItem)) {
            buff.remove();
        } else {
            updateBuffs(buff, AdrenalineBuff.getAdrenalineLevel(buff.owner));
        }
    }

    public void updateBuffs(ActiveBuff buff, int level) {
        buff.setModifier(BuffModifiers.SPEED, 0.2F * level);
    }

    public void onRemoved(ActiveBuff buff) {
        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob) buff.owner;
            player.buffManager.addBuff(new ActiveBuff(AphBuffs.BERSERKER_RUSH_COOLDOWN, player, 20.0F, null), false);

            SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(player)
                    .volume(0.7f)
                    .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
            for (int i = 0; i < 40; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                player.getLevel().entityManager.addParticle(player, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(AphColors.red).heightMoves(10.0F, 30.0F).lifeTime(1000);
            }
        }
    }


}
