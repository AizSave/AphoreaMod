package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.CrystalClusterObject;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

import java.awt.*;
import java.util.ArrayList;

public class InfectedRubyClusterObject extends CrystalClusterObject {
    public InfectedRubyClusterObject(String textureName, Color mapColor, float glowHue, String dropItem, int minDropAmount, int maxDropAmount, int placedDropAmount) {
        super(textureName, mapColor, glowHue, dropItem, minDropAmount, maxDropAmount, placedDropAmount);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (level.isServer()) {
            level.entityManager.addMob(MobRegistry.getMob("rubygolem", level), x * 32 + 16, y * 32 + 16);
        } else if (level.isClient()) {
            level.entityManager.addParticle(new SmokePuffParticle(level, x * 32 + 16, y * 32, AphColors.ruby), Particle.GType.CRITICAL);
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }

    public static void registerCrystalCluster(String itemStringID, String textureName, Color mapColor, float glowHue, String dropItem, int minDropAmount, int maxDropAmount, int placedDropAmount, float brokerValue, boolean isObtainable) {
        InfectedRubyClusterObject o1 = new InfectedRubyClusterObject(textureName, mapColor, glowHue, dropItem, minDropAmount, maxDropAmount, placedDropAmount);
        int id1 = ObjectRegistry.registerObject(itemStringID, o1, brokerValue, isObtainable);
        InfectedRubyClusterRObject o2 = new InfectedRubyClusterRObject(textureName, mapColor, glowHue);
        o1.counterID = ObjectRegistry.registerObject(itemStringID + "r", o2, 0.0F, false);
        o2.counterID = id1;
    }

    public static void registerCrystalCluster(String itemStringID, String textureName, Color mapColor, float glowHue, String dropItem, float brokerValue, boolean isObtainable) {
        registerCrystalCluster(itemStringID, textureName, mapColor, glowHue, dropItem, 2, 3, 2, brokerValue, isObtainable);
    }

    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 1, true, this.getID(), this.counterID);
    }
}
