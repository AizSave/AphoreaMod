package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.CrystalClusterSmallObject;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;

public class InfectedRubyClusterSmallObject extends CrystalClusterSmallObject {
    public InfectedRubyClusterSmallObject(String textureName, Color mapColor, float glowHue, String dropItem, int minDropAmount, int maxDropAmount, int placedDropAmount) {
        super(textureName, mapColor, glowHue, dropItem, minDropAmount, maxDropAmount, placedDropAmount);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (level.isServer()) {
            level.entityManager.addMob(MobRegistry.getMob("rubycaveling", level), x * 32 + 16, y * 32 + 16);
        } else if (level.isClient()) {
            level.entityManager.addParticle(new SmokePuffParticle(level, x * 32 + 16, y * 32, AphColors.ruby), Particle.GType.CRITICAL);
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }
}
