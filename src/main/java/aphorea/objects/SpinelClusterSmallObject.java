package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CrystalClusterSmallObject;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;

public class SpinelClusterSmallObject extends CrystalClusterSmallObject {
    public SpinelClusterSmallObject(String textureName, Color mapColor, float glowHue) {
        super(textureName, mapColor, glowHue, "spinel", 0, 0, 0);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if(new GameRandom().seeded(getTileSeed(x, y)).getChance(0.5F)) {
            if (level.isServer()) {
                level.entityManager.addMob(MobRegistry.getMob("spinelcaveling", level), x * 32 + 16, y * 32 + 16);
            }
            if (level.isClient()) {
                level.entityManager.addParticle(new SmokePuffParticle(level, x * 32 + 16, y * 32 + 32, AphColors.spinel), Particle.GType.CRITICAL);
            }
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems(Level level, int layerID, int x, int y) {
        return new ArrayList<>();
    }
}
