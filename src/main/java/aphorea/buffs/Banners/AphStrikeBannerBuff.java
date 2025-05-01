package aphorea.buffs.Banners;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class AphStrikeBannerBuff extends AphBannerBuff {
    public AphStrikeBannerBuff() {
    }

    public void init(ActiveBuff ab, BuffEventSubscriber eventSubscriber) {
        super.init(ab, eventSubscriber);
        ab.setModifier(BuffModifiers.HEALTH_REGEN, 0.10F * getInspirationEffect(ab));
        ab.setModifier(BuffModifiers.COMBAT_HEALTH_REGEN, 0.10F * getInspirationEffect(ab));
    }
}
