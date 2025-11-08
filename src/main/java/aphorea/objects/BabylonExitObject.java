package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

import java.awt.*;

public class BabylonExitObject extends StaticMultiObject {
    protected BabylonExitObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "templeexit");
        this.mapColor = AphColors.spinel_light;
        this.displayMapTooltip = true;
        this.lightLevel = 100;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer() && player.isServerClient()) {
            LevelObject master = this.getMultiTile(level, 0, x, y).getMasterLevelObject(level, 0, x, y).orElse(null);
            if (master != null) {
                ObjectEntity objectEntity = level.entityManager.getObjectEntity(master.tileX, master.tileY);
                if (objectEntity instanceof PortalObjectEntity) {
                    ((PortalObjectEntity) objectEntity).use(level.getServer(), player.getServerClient());
                }
            }
        }

        super.interact(level, x, y, player);
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return this.isMultiTileMaster() ? new BabylonExitObjectEntity(level, x, y, 10, 10) : super.getNewObjectEntity(level, x, y);
    }

    public static int[] registerObject() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(8, 8, 82, 56);
        ids[0] = ObjectRegistry.registerObject("babylonexit", new BabylonExitObject(0, 0, 3, 2, ids, collision), 0.0F, false);
        ids[1] = ObjectRegistry.registerObject("babylonexit2", new BabylonExitObject(1, 0, 3, 2, ids, collision), 0.0F, false);
        ids[2] = ObjectRegistry.registerObject("babylonexit3", new BabylonExitObject(2, 0, 3, 2, ids, collision), 0.0F, false);
        ids[3] = ObjectRegistry.registerObject("babylonexit4", new BabylonExitObject(0, 1, 3, 2, ids, collision), 0.0F, false);
        ids[4] = ObjectRegistry.registerObject("babylonexit5", new BabylonExitObject(1, 1, 3, 2, ids, collision), 0.0F, false);
        ids[5] = ObjectRegistry.registerObject("babylonexit6", new BabylonExitObject(2, 1, 3, 2, ids, collision), 0.0F, false);
        return ids;
    }

    public static class BabylonExitObjectEntity extends PortalObjectEntity {
        public BabylonExitObjectEntity(Level level, int x, int y, int entranceX, int entranceY) {
            super(level, "babylonexit", x, y, level.getIdentifier(), entranceX, entranceY);
        }

        public void use(Server server, ServerClient client) {
        }
    }

}
