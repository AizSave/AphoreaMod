package aphorea.items.banners;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class AphBannerOfSpeedItem extends AphBanner {
    public AphBannerOfSpeedItem() {
        super(Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.SPEED);
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        float bannerEffect = perspective == null ? AphModifiers.BANNER_EFFECT.defaultBuffManagerValue : perspective.buffManager.getModifier(AphModifiers.BANNER_EFFECT);
        tooltips.add(Localization.translate("itemtooltip", "bannerofspeedeffect", "effect", Math.round(30 * bannerEffect * 100.0f) / 100.0f));
    }
}

