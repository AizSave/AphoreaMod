package aphorea.items.vanillaitemtypes.armor;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

abstract public class AphChestArmorItem extends ChestArmorItem {
    public AphChestArmorItem(int armorValue, int enchantCost, Rarity rarity, String bodyTextureName, String armsTextureName) {
        super(armorValue, enchantCost, rarity, bodyTextureName, armsTextureName, BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
