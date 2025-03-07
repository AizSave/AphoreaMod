package aphorea.items.healingtools;

import aphorea.items.vanillaitemtypes.AphToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.utils.magichealing.AphMagicHealing;
import aphorea.utils.magichealing.AphMagicHealingFunctions;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.DamageTypeRegistry;
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

abstract public class AphMagicHealingToolItem extends AphToolItem {
    protected IntUpgradeValue magicHealing;
    public boolean healingEnchantments = true;

    public AphMagicHealingToolItem(int enchantCost) {
        super(enchantCost);
        this.magicHealing = new IntUpgradeValue(0, 0.2F);

        this.setItemCategory("equipment", "tools", "healing");
        this.setItemCategory(ItemCategory.equipmentManager, "tools", "healingtools");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "tools", "healingtools");

        damageType = DamageTypeRegistry.MAGIC;
    }

    public int getHealing(@Nullable InventoryItem item) {
        return item == null ? magicHealing.getValue(0) : magicHealing.getValue(item.item.getUpgradeTier(item));
    }

    public void healMob(PlayerMob player, Mob target, int healing, InventoryItem item) {
        AphMagicHealing.healMob(player, target, healing, item, this);
    }

    public void healMob(PlayerMob player, Mob target, InventoryItem item) {
        healMob(player, target, magicHealing.getValue(item.item.getUpgradeTier(item)), item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        onHealingToolItemUsed(player, item);
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
        return healingEnchantments ? AphEnchantments.healingItemEnchantments : super.getValidEnchantmentIDs(item);
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "healingtool");
    }

    public void onHealingToolItemUsed(Mob mob, InventoryItem item) {
        mob.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingFunctions).forEach(buff -> ((AphMagicHealingFunctions) buff.buff).onMagicalHealingItemUsed(mob, this, item));

        if (this instanceof AphMagicHealingFunctions) {
            ((AphMagicHealingFunctions) this).onMagicalHealingItemUsed(mob, this, item);
        }

    }

}
