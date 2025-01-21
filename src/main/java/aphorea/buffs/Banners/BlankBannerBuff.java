package aphorea.buffs.Banners;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class BlankBannerBuff extends AphBannerBuff {
    public BlankBannerBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.setModifier(BuffModifiers.HEALTH_REGEN, 0.10F * bannerEffect);
    }
}
