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
import necesse.inventory.item.trinketItem.ShieldTrinketItem;

import java.util.HashSet;
import java.util.Set;

abstract public class AphShieldTrinketItem extends ShieldTrinketItem {
    public final boolean healingEnchantments;

    public AphShieldTrinketItem(Rarity rarity, int armorValue, float minSlowModifier, int msToDepleteStamina, float staminaUsageOnBlock, int damageTakenPercent, float angleCoverage, int enchantCost, boolean healingEnchantments) {
        super(rarity, armorValue, minSlowModifier, msToDepleteStamina, staminaUsageOnBlock, damageTakenPercent, angleCoverage, enchantCost);
        this.healingEnchantments = healingEnchantments;
    }
    public AphShieldTrinketItem(Rarity rarity, int armorValue, float minSlowModifier, int msToDepleteStamina, float staminaUsageOnBlock, int damageTakenPercent, float angleCoverage, int enchantCost) {
        this(rarity, armorValue, minSlowModifier, msToDepleteStamina, staminaUsageOnBlock, damageTakenPercent, angleCoverage, enchantCost, false);
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
