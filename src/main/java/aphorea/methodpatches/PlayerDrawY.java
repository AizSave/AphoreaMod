package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import necesse.level.gameTile.GameTile;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = GameTile.class, name = "getMobSinkingAmount", arguments = {Mob.class})
public class PlayerDrawY {
    @Advice.OnMethodEnter
    static boolean onEnter(@Advice.This GameTile gameTile, @Advice.Argument(0) Mob mob) {
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This GameTile gameTile, @Advice.Argument(0) Mob mob, @Advice.Return(readOnly = false) int y) {
        int flyingHeight = PlayerFlyingHeight.playersFlyingHeight.getOrDefault(mob.getUniqueID(), 0);
        y -= flyingHeight;
    }
}
