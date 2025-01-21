package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

import java.util.HashMap;
import java.util.Map;

@ModMethodPatch(target = Mob.class, name = "getFlyingHeight", arguments = {})
public class PlayerFlyingHeight {
    public static Map<Integer, Integer> playersFlyingHeight = new HashMap<>();

    @Advice.OnMethodEnter
    static boolean onEnter(@Advice.This Mob mob) {
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This Mob mob, @Advice.Return(readOnly = false) int flyingHeight) {
        if (flyingHeight == 0 && mob.isPlayer) {
            flyingHeight = PlayerFlyingHeight.playersFlyingHeight.getOrDefault(mob.getUniqueID(), 0);
        }
    }

}
