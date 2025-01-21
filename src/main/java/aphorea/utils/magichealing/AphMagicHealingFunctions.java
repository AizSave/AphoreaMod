package aphorea.utils.magichealing;

import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import org.jetbrains.annotations.Nullable;

public interface AphMagicHealingFunctions {
    default void onMagicalHealing(Mob healer, Mob target, int healing, int realHealing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
    }

    default void onMagicalHealingItemUsed(Mob mob, ToolItem toolItem, InventoryItem item) {
    }
}
