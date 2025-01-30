package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = ObjectItem.class, name = "canPlace", arguments = {Level.class, int.class, int.class, PlayerMob.class, InventoryItem.class, PacketReader.class})
public class CanPlaceObject {

    @Advice.OnMethodExit
    static void onExit(@Advice.This ObjectItem objectItem, @Advice.Argument(0) Level level, @Advice.Return(readOnly = false) String returnValue) {
        if ("infectedcave".equals(level.getStringID()) && objectItem.getObject().lightLevel > 0) {
            returnValue = "nolight";
        }
    }
}
