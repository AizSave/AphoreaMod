package aphorea.packets;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class AphCustomPushPacket extends PacketForceOfWind {
    public AphCustomPushPacket(byte[] data) {
        super(data);
    }

    public AphCustomPushPacket(Mob mob, float dirX, float dirY, float strength) {
        super(mob, dirX, dirY, strength);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (target != null) {
                applyToMob(target.getLevel(), target, this.dirX, this.dirY, this.strength);
            } else {
                client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
            }

        }
    }

    public static void applyToMob(Level level, Mob mob, float dirX, float dirY, float strength) {
        float forceX = dirX * strength;
        float forceY = dirY * strength;
        if (Math.abs(mob.dx) < Math.abs(forceX)) {
            mob.dx = forceX;
        }

        if (Math.abs(mob.dy) < Math.abs(forceY)) {
            mob.dy = forceY;
        }

    }
}