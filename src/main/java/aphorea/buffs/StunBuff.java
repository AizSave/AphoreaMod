package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class StunBuff extends Buff {
    public StunBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.isVisible = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SLOW, 10F);
        buff.addModifier(BuffModifiers.SPEED, -10F);
        buff.addModifier(BuffModifiers.PARALYZED, true);
    }
}