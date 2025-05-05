package aphorea.items.tools.weapons.magic;

import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

import java.util.HashSet;
import java.util.Set;

abstract public class AphMagicProjectileSecondaryAreaToolItem extends AphMagicProjectileToolItem implements ItemInteractAction {

    int secondaryAttackAnimTime;
    float consumeManaSecondary;

    public AphMagicProjectileSecondaryAreaToolItem(int enchantCost, int secondaryAttackAnimTime, float consumeManaSecondary) {
        super(enchantCost);
        this.secondaryAttackAnimTime = secondaryAttackAnimTime;
        this.consumeManaSecondary = consumeManaSecondary;
    }

    public abstract AphAreaList getAreaList(ItemAttackerMob attackerMob, InventoryItem item);

    public float getSecondaryManaCost(InventoryItem item) {
        return (consumeManaSecondary * this.getManaUsageModifier(item));
    }

    @Override
    public int getLevelInteractAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.round(secondaryAttackAnimTime * (1.0F / this.getAttackSpeedModifier(item, attackerMob)));
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isAttacking;
    }


    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        this.consumeManaSecondary(attackerMob, item);

        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);

        getAreaList(attackerMob, item).execute(attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, this, true);

        return item;
    }

    public void consumeManaSecondary(ItemAttackerMob attackerMob, InventoryItem item) {
        float manaCost = getSecondaryManaCost(item);
        if (manaCost > 0.0F) {
            attackerMob.useMana(manaCost, (attackerMob.isPlayer && ((PlayerMob) attackerMob).isServerClient()) ? ((PlayerMob) attackerMob).getServerClient() : null);
        }
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
        Set<Integer> enchantments = new HashSet<>(super.getValidEnchantmentIDs(item));
        enchantments.addAll(AphEnchantments.areaItemEnchantments);
        return enchantments;
    }
}
