package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = TileItem.class, name = "canPlace", arguments = {Level.class, int.class, int.class, PlayerMob.class, InventoryItem.class, GNDItemMap.class})
public class CanPlaceTile {

    @Advice.OnMethodExit
    static void onExit(@Advice.This TileItem tileItem, @Advice.Argument(0) Level level, @Advice.Return(readOnly = false) String returnValue) {
        if ("infectedfieldscave".equals(level.getStringID()) && tileItem.getTile().lightLevel > 0 && !tileItem.getStringID().equals("infectedwatertile")) {
            returnValue = "nolight";
        }
    }
}
