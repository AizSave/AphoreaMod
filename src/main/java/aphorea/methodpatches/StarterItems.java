package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = PlayerInventoryManager.class, name = "giveStarterItems", arguments = {})
public class StarterItems {

    @Advice.OnMethodEnter
    static boolean onEnter(@Advice.This PlayerInventoryManager playerInventoryManager) {
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This PlayerInventoryManager playerInventoryManager) {
        if (playerInventoryManager.getAmount(ItemRegistry.getItem("copperdagger"), false, false, false, false, "startitem") == 0) {
            playerInventoryManager.main.addItem(playerInventoryManager.player.getLevel(), playerInventoryManager.player, new InventoryItem("copperdagger"), "startitem", null);
        }
        if (playerInventoryManager.getAmount(ItemRegistry.getItem("initialrune"), false, false, false, false, "startitem") == 0) {
            playerInventoryManager.main.addItem(playerInventoryManager.player.getLevel(), playerInventoryManager.player, new InventoryItem("initialrune"), "startitem", null);
        }
        if (playerInventoryManager.getAmount(ItemRegistry.getItem("rusticrunesinjector"), false, false, false, false, "startitem") == 0) {
            playerInventoryManager.main.addItem(playerInventoryManager.player.getLevel(), playerInventoryManager.player, new InventoryItem("rusticrunesinjector"), "startitem", null);
        }
    }
}