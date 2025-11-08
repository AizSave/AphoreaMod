package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CrystalClusterObject;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

import java.awt.*;
import java.util.ArrayList;

public class SpinelClusterObject extends CrystalClusterObject {
    public SpinelClusterObject(String textureName, Color mapColor, float glowHue) {
        super(textureName, mapColor, glowHue, "spinel", 0, 0, 0);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (level.isServer()) {
            level.entityManager.addMob(MobRegistry.getMob("spinelgolem", level), x * 32 + 16, y * 32 + 16);
        }
        if (level.isClient()) {
            level.entityManager.addParticle(new SmokePuffParticle(level, x * 32 + 16, y * 32 + 32, AphColors.spinel), Particle.GType.CRITICAL);
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }

    public static void registerCrystalCluster(String itemStringID, String textureName, Color mapColor, float glowHue, float brokerValue, boolean isObtainable) {
        SpinelClusterObject o1 = new SpinelClusterObject(textureName, mapColor, glowHue);
        int id1 = ObjectRegistry.registerObject(itemStringID, o1, brokerValue, isObtainable);
        SpinelClusterRObject o2 = new SpinelClusterRObject(textureName, mapColor, glowHue);
        o1.counterID = ObjectRegistry.registerObject(itemStringID + "r", o2, 0.0F, false);
        o2.counterID = id1;
    }

    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 1, true, this.getID(), this.counterID);
    }

    @Override
    public ArrayList<InventoryItem> getObjectDroppedItems(Level level, int layerID, int tileX, int tileY, String purpose) {
        return new ArrayList<>();
    }
}
