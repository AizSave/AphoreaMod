package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.RockObject;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;

public class RockyWallObject extends RockObject {
    public RockyWallObject(String rockTexture, Color rockColor, String droppedStone, int minStoneAmount, int maxStoneAmount, int placedStoneAmount) {
        super(rockTexture, rockColor, droppedStone, minStoneAmount, maxStoneAmount, placedStoneAmount);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (new GameRandom().seeded(getTileSeed(x, y)).getChance(0.05F)) {
            if (level.isServer()) {
                level.entityManager.addMob(MobRegistry.getMob("rockygelslime", level), x * 32 + 16, y * 32 + 16);
            }
            if (level.isClient()) {
                level.entityManager.addParticle(new SmokePuffParticle(level, x * 32 + 16, y * 32 + 32, AphColors.rock), Particle.GType.CRITICAL);
            }
            level.objectLayer.setObject(layerID, x, y, 0);
        } else {
            super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        }
    }
}
