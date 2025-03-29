package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.RockOreObject;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;

public class TungstenGelRockOreObject extends RockOreObject {
    public TungstenGelRockOreObject(RockObject parentRock, String oreMaskTextureName, String oreTextureName, Color oreColor) {
        super(parentRock, oreMaskTextureName, oreTextureName, oreColor, "tungstenore", 0, 0, 0, false);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (level.isServer()) {
            level.entityManager.addMob(MobRegistry.getMob("tungstencaveling", level), x * 32 + 16, y * 32 + 16);
        }
        if (level.isClient()) {
            level.entityManager.addParticle(new SmokePuffParticle(level, x * 32 + 16, y * 32 + 32, AphColors.tungsten), Particle.GType.CRITICAL);
        }
        level.objectLayer.setObject(layerID, x, y, 0);
    }
}
