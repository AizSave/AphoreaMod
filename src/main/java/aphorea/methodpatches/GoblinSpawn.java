package aphorea.methodpatches;

import aphorea.mobs.hostile.classes.DaggerGoblin;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.GoblinMob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = GoblinMob.class, name = "init", arguments = {})
public class GoblinSpawn {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This GoblinMob goblinMob) {
        if (goblinMob instanceof DaggerGoblin) {
            return false;
        }

        boolean daggerGoblin = GameRandom.globalRandom.getChance(0.1F);
        if (daggerGoblin) {
            String daggerType;
            float randomDagger = GameRandom.globalRandom.getFloatBetween(0, 1);
            if (randomDagger < 0.65) {
                daggerType = "copper";
            } else if (randomDagger < 0.90) {
                daggerType = "iron";
            } else {
                daggerType = "gold";
            }
            goblinMob.getLevel().entityManager.addMob(MobRegistry.getMob(daggerType + "daggergoblin", goblinMob.getLevel()), goblinMob.x, goblinMob.y);
            goblinMob.remove();
        }
        return daggerGoblin;

    }


}
