package aphorea.items.vanillaitemtypes.armor;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.FeetArmorLootTable;

abstract public class AphBootsArmorItem extends BootsArmorItem {
    public AphBootsArmorItem(int armorValue, int enchantCost, Rarity rarity, String textureName) {
        super(armorValue, enchantCost, rarity, textureName, FeetArmorLootTable.feetArmor);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
