package aphorea.items.banners;

import aphorea.packets.AphSingleAreaShowPacket;
import aphorea.registry.AphBuffs;
import aphorea.registry.AphDamageType;
import aphorea.registry.AphModifiers;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;

public class AphStrikeBannerItem extends AphBanner {
    static int range = 200;
    static Color color = AphColors.blood;
    public static AphAreaList areaList = new AphAreaList(
            new AphArea(range, 0.5F, color).setDamageArea(30).setArmorPen(5)
    ).setDamageType(AphDamageType.BANNER);

    public AphStrikeBannerItem() {
        super(Rarity.LEGENDARY, 480, (m) -> AphBuffs.BANNER.STRIKE, 2000 / 50);
    }

    @Override
    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
        areaList.executeAreas(player);
        level.getServer().network.sendToAllClients(new AphSingleAreaShowPacket(player.x, player.y, range, color, 0.5F));
    }

    public DrawOptions getStandDrawOptions(Level level, int tileX, int tileY, int drawX, int drawY, GameLight light) {
        int anim = GameUtils.getAnim(level.getWorldEntity().getTime() + (tileX * 97L) + (tileY * 151L), 5, 800);
        int xOffset = -30;
        int yOffset = -32;

        return this.holdTexture.initDraw().sprite(anim, 2, 128).light(light).pos(drawX - 16 + xOffset, drawY - 40 + yOffset + (anim % 2 != 0 ? 0 : 2));
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        float bannerEffect = perspective == null ? AphModifiers.BANNER_EFFECT.defaultBuffManagerValue : perspective.buffManager.getModifier(AphModifiers.BANNER_EFFECT);
        tooltips.add(Localization.translate("itemtooltip", "strikebannereffect", "effect", Math.round(10F * bannerEffect * 100.0f) / 100.0f));
        float bannerAbilitySpeed = perspective == null ? AphModifiers.BANNER_ABILITY_SPEED.defaultBuffManagerValue : perspective.buffManager.getModifier(AphModifiers.BANNER_ABILITY_SPEED);
        tooltips.add(Localization.translate("itemtooltip", "strikebannerability", "time", Math.round(2F / bannerAbilitySpeed * 100.0f) / 100.0f));
        areaList.addAreasToolTip(tooltips, perspective, true, null, null);
    }
}

