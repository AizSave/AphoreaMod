package aphorea.packets;

import aphorea.utils.AphColors;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;

public class AphRuneOfUnstableGelSlimePacket extends Packet {
    public final int slot;
    public final int targetX;
    public final int targetY;

    public AphRuneOfUnstableGelSlimePacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
    }

    public AphRuneOfUnstableGelSlimePacket(int slot, int targetX, int targetY) {
        this.slot = slot;
        this.targetX = targetX;
        this.targetY = targetY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(targetX);
        writer.putNextInt(targetY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            ClientClient target = client.getClient(this.slot);
            if (target != null && target.isSamePlace(client.getLevel())) {
                PlayerMob player = target.playerMob;
                player.getLevel().entityManager.addParticle(new SmokePuffParticle(player.getLevel(), player.x, player.y, AphColors.unstableGel), Particle.GType.CRITICAL);
                player.getLevel().entityManager.addParticle(new SmokePuffParticle(player.getLevel(), targetX, targetY, AphColors.unstableGel), Particle.GType.CRITICAL);
                player.setPos(targetX, targetY, true);
            } else {
                client.network.sendPacket(new PacketRequestPlayerData(this.slot));
            }

        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            client.playerMob.setPos(targetX, targetY, true);
        }
    }
}