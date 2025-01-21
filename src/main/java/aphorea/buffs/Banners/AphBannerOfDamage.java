package aphorea.buffs.Banners;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class AphBannerOfDamage extends AphBannerBuff {
    public AphBannerOfDamage() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.setModifier(BuffModifiers.ALL_DAMAGE, 0.15F * bannerEffect);
    }

}
