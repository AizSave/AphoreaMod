package aphorea.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.GameDamage;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = GrassObject.class, name = "attackThrough", arguments = {Level.class, int.class, int.class, GameDamage.class})
public class GrassNoBreakSoundWhenNoDamage {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This GrassObject grassObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) GameDamage damage) {
        if (damage.damage == 0) {
            level.makeGrassWeave(x, y, grassObject.weaveTime, false);
            return true;
        }
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This GrassObject grassObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) GameDamage damage) {

    }
}
