package aphorea.buffs;

import aphorea.utils.AphColors;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle.GType;

public class HoneyedBuff extends Buff {
    public HoneyedBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
            owner.getLevel().entityManager.addParticle(owner.x + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), GType.IMPORTANT_COSMETIC).movesFriction(owner.dx / 10.0F, owner.dy / 10.0F, 1F).color(AphColors.honey).heightMoves(16.0F, 0);
        }

    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SLOW, 0.4F);
    }
}
