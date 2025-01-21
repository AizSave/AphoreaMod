package aphorea.buffs.Banners;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class AphBannerOfDefense extends AphBannerBuff {
    public AphBannerOfDefense() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.setModifier(BuffModifiers.INCOMING_DAMAGE_MOD, Math.max(0.5F, 1F - 0.1F * bannerEffect));
    }

}
