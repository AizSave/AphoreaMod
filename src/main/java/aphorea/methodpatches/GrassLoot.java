package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.SurfaceGrassObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = SurfaceGrassObject.class, name = "getLootTable", arguments = {Level.class, int.class, int.class, int.class})
public class GrassLoot {

    @Advice.OnMethodEnter
    static boolean onEnter(@Advice.This SurfaceGrassObject grassObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int tileX, @Advice.Argument(2) int tileY) {
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This SurfaceGrassObject grassObject, @Advice.Argument(0) Level level, @Advice.Return(readOnly = false) LootTable lootTable) {
        if (GameRandom.globalRandom.getChance(0.002F + (level.getWorldEntity().getDay() < 2 ? 0.003F : 0))) {
            lootTable = new LootTable(new LootItem("floralring"));
        }
    }
}
