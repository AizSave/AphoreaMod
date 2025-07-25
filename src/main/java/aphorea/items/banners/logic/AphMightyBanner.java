package aphorea.items.banners.logic;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

import java.util.function.Function;

public class AphMightyBanner extends AphBanner {


    public AphMightyBanner(Rarity rarity, int range, Function<Mob, Buff> buff, float baseEffect, String... extraToolTips) {
        super(rarity, range, buff, baseEffect, extraToolTips);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate("global", "aphoreamightybanner"));
        return tooltips;
    }
}