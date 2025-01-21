package aphorea.methodpatches;

import aphorea.mobs.friendly.WildPhosphorSlime;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.miscToolItem.NetToolItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = NetToolItem.class, name = "hitMob", arguments = {InventoryItem.class, ToolItemMobAbilityEvent.class, Level.class, Mob.class, Mob.class})
public class NetHitMob {
    @Advice.OnMethodEnter
    static boolean onEnter(@Advice.This NetToolItem netToolItem, @Advice.Argument(0) InventoryItem item, @Advice.Argument(1) ToolItemMobAbilityEvent event, @Advice.Argument(2) Level level, @Advice.Argument(3) Mob target, @Advice.Argument(4) Mob attacker) {
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This NetToolItem netToolItem, @Advice.Argument(0) InventoryItem item, @Advice.Argument(1) ToolItemMobAbilityEvent event, @Advice.Argument(2) Level level, @Advice.Argument(3) Mob target, @Advice.Argument(4) Mob attacker) {
        if (target instanceof WildPhosphorSlime && attacker.isPlayer) {
            PlayerMob player = (PlayerMob) attacker;
            if (player.isServerClient()) {
                player.getServerClient().newStats.mob_kills.addKill(target);
            }
        }
    }
}
