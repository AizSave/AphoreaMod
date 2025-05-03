package aphorea.buffs;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.InvulnerableActiveBuff;

public class StopBuff extends InvulnerableActiveBuff {
    public StopBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SLOW, 10F);
        buff.addModifier(BuffModifiers.SPEED, -10F);
        buff.addModifier(BuffModifiers.PARALYZED, true);
        buff.addModifier(BuffModifiers.INTIMIDATED, true);
    }
}
