package aphorea.utils.magichealing;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import org.jetbrains.annotations.Nullable;

public interface AphMagicHealingBuff {
    default int onBeforeMagicalHealing(ActiveBuff activeBuff, Mob healer, Mob target, int healing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        return healing;
    }

    default void onMagicalHealing(ActiveBuff activeBuff, Mob healer, Mob target, int healing, int realHealing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
    }

    default void onMagicalHealingItemUsed(ActiveBuff activeBuff, Mob mob, ToolItem toolItem, InventoryItem item) {
    }
}
