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
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.lootTable.presets.ToolsLootTable;

import java.util.HashSet;
import java.util.Set;

public class AphSimpleTrinketItem extends SimpleTrinketItem {
    public final boolean healingEnchantments;

    public AphSimpleTrinketItem(Rarity rarity, String[] buffStringIDs, int enchantCost, boolean healingEnchantments) {
        super(rarity, buffStringIDs, enchantCost, ToolsLootTable.tools);
        this.healingEnchantments = healingEnchantments;
    }

    public AphSimpleTrinketItem(Rarity rarity, String[] buffStringIDs, int enchantCost) {
        this(rarity, buffStringIDs, enchantCost, false);
    }

    public AphSimpleTrinketItem(Rarity rarity, String buffStringID, int enchantCost, boolean healingEnchantments) {
        this(rarity, new String[]{buffStringID}, enchantCost, healingEnchantments);
    }

    public AphSimpleTrinketItem(Rarity rarity, String buffStringID, int enchantCost) {
        this(rarity, buffStringID, enchantCost, false);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public EquipmentItemEnchant getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), EquipmentItemEnchant.class);
    }

    @Override
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
