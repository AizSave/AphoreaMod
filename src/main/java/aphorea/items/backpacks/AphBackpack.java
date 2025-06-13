package aphorea.items.backpacks;

import necesse.engine.localization.Localization;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.IngredientUser;
import necesse.level.maps.Level;

abstract public class AphBackpack extends PouchItem {
    public AphBackpack() {
        canUseHealthPotionsFromPouch = true;
        canUseManaPotionsFromPouch = true;
        canEatFoodFromPouch = true;
        canUseBuffPotionsFromPouch = true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "backpackslots", "slots", getInternalInventorySize()));
        tooltips.add(Localization.translate("itemtooltip", "backpack"));
        tooltips.add(Localization.translate("itemtooltip", "backpackcraft"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "stored", "items", this.getStoredItemAmounts(item)));
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public boolean isValidPouchItem(InventoryItem item) {
        if (item == null || item.item == null) return false;
        return this.isValidRequestItem(item.item);
    }

    @Override
    public boolean isValidRequestItem(Item item) {
        if (item == null) return false;
        return !(item instanceof InternalInventoryItemInterface);
    }

    @Override
    public boolean isValidRequestType(Type type) {
        return false;
    }

    @Override
    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Type requestType, String purpose) {
        int amount = super.getInventoryAmount(level, player, item, requestType, purpose);
        if (this.isValidRequestItem(item.item)) {
            Inventory internalInventory = this.getInternalInventory(item);
            amount += internalInventory.getAmount(level, player, requestType, purpose);
        }

        return amount;
    }

    @Override
    public Item getInventoryFirstItem(Level level, PlayerMob player, InventoryItem item, Type requestType, String purpose) {
        if (this.isValidRequestItem(item.item)) {
            Inventory internalInventory = this.getInternalInventory(item);
            Item firstItem = internalInventory.getFirstItem(level, player, requestType, purpose);
            if (firstItem != null) {
                return firstItem;
            }
        }

        return super.getInventoryFirstItem(level, player, item, requestType, purpose);
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Type requestType, int amount, String purpose) {
        int removed = 0;
        if (this.isValidRequestItem(item.item)) {
            Inventory internalInventory = this.getInternalInventory(item);
            removed = internalInventory.removeItems(level, player, requestType, amount, purpose);
            if (removed > 0) {
                this.saveInternalInventory(item, internalInventory);
            }
        }

        return removed < amount ? removed + super.removeInventoryAmount(level, player, item, requestType, amount, purpose) : removed;
    }

    @Override
    public boolean ignoreCombineStackLimit(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        return false;
    }

    @Override
    public ComparableSequence<Integer> getInventoryAddPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, InventoryItem input, String purpose) {
        boolean inInventory = inventory.streamSlots()
                .anyMatch(slot -> slot != null && slot.getItem() != null && slot.getItem().item.getID() == item.item.getID());

        if (inInventory) {
            return new ComparableSequence<>(inventorySlot);
        } else {
            return super.getInventoryAddPriority(level, player, inventory, inventorySlot, item, input, purpose);
        }
    }

    public boolean canBeUsedForCrafting(InventoryItem item) {
        Inventory internalInventory = this.getInternalInventory(item);
        return internalInventory.streamSlots().allMatch(InventorySlot::isSlotClear);
    }

    @Override
    public void countIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientCounter handler) {
        if (canBeUsedForCrafting(item)) {
            super.countIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
        }
    }

    @Override
    public void useIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientUser handler) {
        if (canBeUsedForCrafting(item)) {
            super.useIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
        }
    }
}
