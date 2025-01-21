package aphorea.buffs;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class StopBuff extends Buff {
    public StopBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SLOW, 10F);
        buff.addModifier(BuffModifiers.SPEED, -10F);
        buff.addModifier(BuffModifiers.PARALYZED, true);
        buff.addModifier(BuffModifiers.INTIMIDATED, true);
        buff.addModifier(BuffModifiers.INCOMING_DAMAGE_MOD, -1000F);
    }

    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event) {
        event.prevent();
        event.damage = new GameDamage(0);
    }

    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.damage != 0) {
            event.target.setHealth(event.target.getHealth() + event.damage);
        }
    }
}
