package aphorea.items.vanillaitemtypes;

import aphorea.registry.AphEnchantments;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

import java.util.HashSet;
import java.util.Set;

abstract public class AphTrinketItem extends TrinketItem {
    public boolean healingEnchantments = false;

    public AphTrinketItem(Rarity rarity, int enchantCost) {
        super(rarity, enchantCost, TrinketsLootTable.trinkets);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public EquipmentItemEnchant getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), EquipmentItemEnchant.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        Set<Integer> enchantments = new HashSet<>(super.getValidEnchantmentIDs(item));
        if (healingEnchantments) {
            enchantments.addAll(AphEnchantments.healingEquipmentEnchantments);
        }
        return enchantments;
    }
}
