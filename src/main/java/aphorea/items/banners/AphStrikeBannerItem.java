package aphorea.items.banners;

import aphorea.items.banners.logic.AphAbilityBanner;
import aphorea.registry.AphBuffs;
import aphorea.registry.AphDamageType;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;

public class AphStrikeBannerItem extends AphAbilityBanner {
    static int range = 200;
    static Color color = AphColors.blood;
    public static AphAreaList areaList = new AphAreaList(
            new AphArea(range, 0.5F, color).setDamageArea(new GameDamage(AphDamageType.INSPIRATION, 30, 5))
    );

    public AphStrikeBannerItem() {
        super(Rarity.LEGENDARY, 480, (m) -> AphBuffs.BANNER.STRIKE, 2000 / 50, 10);
    }

    @Override
    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
        areaList.execute(player, true);
    }

    @Override
    public DrawOptions getStandDrawOptions(Level level, int tileX, int tileY, int drawX, int drawY, GameLight light) {
        int anim = GameUtils.getAnim(level.getWorldEntity().getTime() + (tileX * 97L) + (tileY * 151L), 5, 800);
        int xOffset = -30;
        int yOffset = -32;

        return this.holdTexture.initDraw().sprite(anim, 2, 128).light(light).pos(drawX - 16 + xOffset, drawY - 40 + yOffset + (anim % 2 != 0 ? 0 : 2));
    }

    @Override
    public void addExtraTooltips(ListGameTooltips tooltips, PlayerMob perspective) {
        areaList.addAreasToolTip(tooltips, perspective, true, null, null);
    }
}

