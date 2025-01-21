package aphorea.buffs;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class ImmortalBuff extends Buff {
    public ImmortalBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        if (buff.owner.buffManager.hasBuff(AphBuffs.INMORTAL_COOLDOWN)) {
            buff.owner.buffManager.removeBuff(buff.buff, true);
        } else {
            buff.addModifier(BuffModifiers.INCOMING_DAMAGE_MOD, -1000F);
        }
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isVisible() && owner.isPlayer) {
            for (int i = 0; i < 3; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(-30, 30);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(-30, 30);
                owner.getLevel().entityManager.addParticle(dx, dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(0, 0, 0.8F).color(AphColors.gold).heightMoves(30.0F, 30.0F).lifeTime(100);
            }
        }
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.buffManager.hasBuff(AphBuffs.INMORTAL_COOLDOWN)) {
            buff.owner.buffManager.removeBuff(buff.buff, true);
        }
    }

    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event) {
        event.prevent();
        event.damage = new GameDamage(0);
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        event.prevent();
    }

    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.damage != 0) {
            event.target.setHealth(event.target.getHealth() + event.damage);
        }
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        if (!buff.owner.isHostile && !buff.owner.isBoss()) {
            buff.owner.addBuff(new ActiveBuff(AphBuffs.INMORTAL_COOLDOWN, buff.owner, 1000, null), false);
        }
    }
}
