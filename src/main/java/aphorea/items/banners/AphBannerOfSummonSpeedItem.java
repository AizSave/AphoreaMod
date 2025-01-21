package aphorea.items.banners;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class AphBannerOfSummonSpeedItem extends AphBanner {
    public AphBannerOfSummonSpeedItem() {
        super(Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.SUMMON_SPEED);
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        float bannerEffect = perspective.buffManager.getModifier(AphModifiers.BANNER_EFFECT);
        tooltips.add(Localization.translate("itemtooltip", "bannerofsummonspeedeffect", "effect", Math.round(75 * bannerEffect * 100.0f) / 100.0f));
    }
}

