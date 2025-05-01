package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class HarpBuff extends AphInspirationEffect {
    @Override
    public void init(ActiveBuff ab, BuffEventSubscriber eventSubscriber) {
        super.init(ab, eventSubscriber);
        float inspirationEffect = getInspirationEffect(ab);
        ab.setModifier(BuffModifiers.INCOMING_DAMAGE_MOD, 1 - 0.05F * inspirationEffect);
        ab.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, 0.25F * inspirationEffect);
        ab.setModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.25F * inspirationEffect);
        ab.setModifier(BuffModifiers.MANA_REGEN_FLAT, inspirationEffect);
        ab.setModifier(BuffModifiers.COMBAT_MANA_REGEN_FLAT, inspirationEffect);
        ab.setModifier(BuffModifiers.SPEED, 0.05F * inspirationEffect);
        ab.setModifier(BuffModifiers.ALL_DAMAGE, 0.05F * inspirationEffect);
        ab.setModifier(BuffModifiers.CRIT_CHANCE, 0.05F * inspirationEffect);
    }
}
