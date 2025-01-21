package aphorea.methodpatches;

import aphorea.mobs.friendly.WildPhosphorSlime;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.inventory.item.toolItem.miscToolItem.NetToolItem;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = NetToolItem.class, name = "canHitMob", arguments = {Mob.class, ToolItemMobAbilityEvent.class})
public class NetCanHitMob {
    @Advice.OnMethodEnter
    static boolean onEnter(@Advice.This NetToolItem netToolItem, @Advice.Argument(0) Mob mob, @Advice.Argument(1) ToolItemMobAbilityEvent toolItemEvent) {
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This NetToolItem netToolItem, @Advice.Argument(0) Mob mob, @Advice.Argument(1) ToolItemMobAbilityEvent toolItemEvent, @Advice.Return(readOnly = false) boolean result) {
        if (mob instanceof WildPhosphorSlime) {
            result = true;
        }
    }
}
