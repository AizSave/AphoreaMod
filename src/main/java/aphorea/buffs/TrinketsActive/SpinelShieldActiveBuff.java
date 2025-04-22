package aphorea.buffs.TrinketsActive;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SpinelShieldActiveBuff extends Buff {
    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
        activeBuff.setModifier(BuffModifiers.SPEED, -0.6F);
        activeBuff.setModifier(BuffModifiers.FRICTION, 1F);
        activeBuff.setModifier(BuffModifiers.INTIMIDATED, true);
    }

}
