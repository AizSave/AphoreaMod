package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class DaggerAttackBuff extends Buff {
    public DaggerAttackBuff() {
        this.isVisible = false;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        float prevMovementMod = buff.owner.buffManager.getModifier(BuffModifiers.ATTACK_MOVEMENT_MOD);
        if (prevMovementMod != 1F) {
            buff.setModifier(BuffModifiers.SPEED, 1F - prevMovementMod);

        }
        buff.setModifier(BuffModifiers.ATTACK_MOVEMENT_MOD, 0F);
    }
}
