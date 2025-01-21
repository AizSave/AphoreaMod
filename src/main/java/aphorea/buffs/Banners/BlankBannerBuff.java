package aphorea.buffs.Banners;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class BlankBannerBuff extends AphBannerBuff {
    public BlankBannerBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.setModifier(BuffModifiers.HEALTH_REGEN, 0.10F * bannerEffect);
    }
}
