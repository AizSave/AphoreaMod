package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

import java.awt.*;

abstract public class AphGreatswordToolItem extends GreatswordToolItem {
    public AphGreatswordToolItem(int enchantCost, GreatswordChargeLevel... chargeLevels) {
        super(enchantCost, chargeLevels);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public static GreatswordChargeLevel[] getThreeChargeLevels(int level1Time, int level2Time, int level3Time, Color level1Color, Color level2Color, Color level3Color) {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(level1Time, 1.0F, level1Color), new GreatswordChargeLevel(level2Time, 1.5F, level2Color), new GreatswordChargeLevel(level3Time, 2.0F, level3Color)};
    }
}
