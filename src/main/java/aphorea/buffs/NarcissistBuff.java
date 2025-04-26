package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class NarcissistBuff extends Buff {
    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
        activeBuff.setModifier(BuffModifiers.SPEED, -0.25F);
        activeBuff.setModifier(BuffModifiers.INTIMIDATED, true);
    }

}
