package aphorea.other.itemtype.healing;

import aphorea.other.AphoreaEnchantments;
import aphorea.other.magichealing.AphoreaMagicHealing;
import aphorea.other.magichealing.AphoreaMagicHealingFunctions;
import aphorea.other.vanillaitemtypes.AphoreaToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

abstract public class AphoreaMagicHealingToolItem extends AphoreaToolItem {
    protected IntUpgradeValue magicHealing;
    public boolean healingEnchantments = true;

    public AphoreaMagicHealingToolItem(int enchantCost) {
        super(enchantCost);
        this.magicHealing = new IntUpgradeValue(0, 0.2F);

        this.setItemCategory("equipment", "tools", "healing");
        this.setItemCategory(ItemCategory.equipmentManager, "tools", "healingtools");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "tools", "healingtools");
    }

    public int getHealing(@Nullable InventoryItem item) {
        return item == null ? magicHealing.getValue(0) : magicHealing.getValue(item.item.getUpgradeTier(item));
    }

    public void healMob(PlayerMob player, Mob target, int healing, InventoryItem item) {
        AphoreaMagicHealing.healMob(player, target, healing, this, item);
    }

    public void healMob(PlayerMob player, Mob target, InventoryItem item) {
        healMob(player, target, magicHealing.getValue(item.item.getUpgradeTier(item)), item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        onItemUsed(player, item);
        return super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
    }

    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return healingEnchantments ? AphoreaEnchantments.healingItemEnchantments : super.getValidEnchantmentIDs(item);
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "healingtool");
    }

    public void onItemUsed(Mob mob, InventoryItem item) {
        mob.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphoreaMagicHealingFunctions).forEach(buff -> ((AphoreaMagicHealingFunctions) buff.buff).onMagicalHealingItemUsed(mob, this, item));

        if(this instanceof AphoreaMagicHealingFunctions) {
            ((AphoreaMagicHealingFunctions) this).onMagicalHealingItemUsed(mob, this, item);
        }

    }
}
