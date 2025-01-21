package aphorea.items.banners;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class AphBannerOfDefenseItem extends AphBanner {
    public AphBannerOfDefenseItem() {
        super(Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.DEFENSE);
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        float bannerEffect = perspective.buffManager.getModifier(AphModifiers.BANNER_EFFECT);
        tooltips.add(Localization.translate("itemtooltip", "bannerofdefenseeffect", "effect", Math.round(10 * bannerEffect * 100.0f) / 100.0f));
    }
}
