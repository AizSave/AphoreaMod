package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class HarmonyBuff extends AphInspirationEffect {
    @Override
    public void init(ActiveBuff ab, BuffEventSubscriber eventSubscriber) {
        super.init(ab, eventSubscriber);
        float inspirationEffect = getInspirationEffect(ab);
        ab.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, 0.5F * inspirationEffect);
        ab.setModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F * inspirationEffect);
        ab.setModifier(BuffModifiers.MANA_REGEN_FLAT, 2 * inspirationEffect);
        ab.setModifier(BuffModifiers.COMBAT_MANA_REGEN_FLAT, inspirationEffect);
        ab.setModifier(BuffModifiers.SPEED, 0.1F * inspirationEffect);
    }
}
