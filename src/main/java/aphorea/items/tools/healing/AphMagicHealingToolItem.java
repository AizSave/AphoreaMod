package aphorea.items.tools.healing;

import aphorea.items.vanillaitemtypes.AphToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.utils.magichealing.AphMagicHealing;
import aphorea.utils.magichealing.AphMagicHealingBuff;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

abstract public class AphMagicHealingToolItem extends AphToolItem {
    protected IntUpgradeValue magicHealing = new IntUpgradeValue(0, 0.2F);
    public boolean healingEnchantments = true;

    public AphMagicHealingToolItem(int enchantCost) {
        super(enchantCost);

        this.setItemCategory("equipment", "tools", "healing");
        this.setItemCategory(ItemCategory.equipmentManager, "tools", "healingtools");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "tools", "healingtools");
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return new LocalMessage("message", "cantusehealingtools");
    }

    public int getHealing(@Nullable InventoryItem item) {
        return item == null ? magicHealing.getValue(0) : magicHealing.getValue(item.item.getUpgradeTier(item));
    }

    public void healMob(ItemAttackerMob attackerMob, Mob target, int healing, InventoryItem item) {
        AphMagicHealing.healMob(attackerMob, target, healing, item, this);
    }

    public void healMob(ItemAttackerMob attackerMob, Mob target, InventoryItem item) {
        healMob(attackerMob, target, magicHealing.getValue(item.item.getUpgradeTier(item)), item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        onHealingToolItemUsed(attackerMob, item);
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return healingEnchantments ? AphEnchantments.healingItemEnchantments : super.getValidEnchantmentIDs(item);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "healingtool");
    }

    public void onHealingToolItemUsed(Mob mob, InventoryItem item) {
        mob.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingBuff).forEach(buff -> ((AphMagicHealingBuff) buff.buff).onMagicalHealingItemUsed(buff, mob, this, item));
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        if (!this.magicHealing.hasMoreThanOneValue() && !this.attackDamage.hasMoreThanOneValue()) {
            return Localization.translate("ui", "itemnotupgradable");
        } else {
            return this.getUpgradeTier(item) >= (float) IncursionData.ITEM_TIER_UPGRADE_CAP ? Localization.translate("ui", "itemupgradelimit") : null;
        }
    }
}
