package aphorea.items.vanillaitemtypes;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem;

public class AphCustomPickaxeToolItem extends CustomPickaxeToolItem {
    public AphCustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost) {
        super(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost);
    }

    public AphCustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, Rarity rarity) {
        super(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, rarity);
    }

    public AphCustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, Rarity rarity, int addedRange) {
        super(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, rarity, addedRange);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
