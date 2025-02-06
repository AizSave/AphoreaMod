package aphorea.items.banners;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class AphBannerOfDamageItem extends AphBanner {
    public AphBannerOfDamageItem() {
        super(Rarity.COMMON, 480, (m) -> AphBuffs.BANNER.DAMAGE);
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        float bannerEffect = perspective == null ? AphModifiers.BANNER_EFFECT.defaultBuffManagerValue : perspective.buffManager.getModifier(AphModifiers.BANNER_EFFECT);
        tooltips.add(Localization.translate("itemtooltip", "bannerofdamageeffect", "effect", Math.round(15 * bannerEffect * 100.0f) / 100.0f));
    }
}

