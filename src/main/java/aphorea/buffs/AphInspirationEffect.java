package aphorea.buffs;

import aphorea.registry.AphModifiers;
import necesse.entity.mobs.buffs.ActiveBuff;

public class AphInspirationEffect extends AphShownBuff {
    public float getInspirationEffect(ActiveBuff ab) {
        return ab.owner == null ? 1 : ab.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT);
    }
}
