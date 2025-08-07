package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ToolDamageItemAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

import java.awt.geom.Line2D;

public class ToolDamageItemPatches {

    @ModMethodPatch(target = ToolDamageItem.class, name = "onAttack", arguments = {Level.class, int.class, int.class, ItemAttackerMob.class, int.class, InventoryItem.class, ItemAttackSlot.class, int.class, int.class, GNDItemMap.class})
    public static class onAttack {
        @Advice.OnMethodEnter
        static boolean onEnter(@Advice.This ToolDamageItem This) {
            return true;
        }

        @Advice.OnMethodExit
        static void onExit(
                @Advice.This ToolDamageItem This,
                @Advice.Argument(0) Level level,
                @Advice.Argument(1) int x,
                @Advice.Argument(2) int y,
                @Advice.Argument(3) ItemAttackerMob attackerMob,
                @Advice.Argument(4) int attackHeight,
                @Advice.Argument(5) InventoryItem item,
                @Advice.Argument(6) ItemAttackSlot slot,
                @Advice.Argument(7) int animAttack,
                @Advice.Argument(8) int seed,
                @Advice.Argument(9) GNDItemMap mapContent,
                @Advice.Return(readOnly = false) InventoryItem returnItem
        ) {
            if (attackerMob.isPlayer) {
                attackerMob.startAttackHandler(new ToolDamageItemAttackHandler((PlayerMob) attackerMob, slot, x, y, seed, This, mapContent));
            } else {
                item = This.runTileAttack(level, x, y, attackerMob, (Line2D) null, item, animAttack, mapContent);
            }

            item = This.startToolItemEventAbilityEvent(level, x, y, attackerMob, attackHeight, item, seed);

            returnItem = item;
        }
    }
}
