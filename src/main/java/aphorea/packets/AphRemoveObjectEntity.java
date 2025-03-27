package aphorea.packets;

import aphorea.utils.AphColors;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class AphRemoveObjectEntity extends Packet {
    public final int tileX;
    public final int tileY;

    public AphRemoveObjectEntity(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public AphRemoveObjectEntity(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            client.getLevel().entityManager.removeObjectEntity(tileX, tileY);
        }
    }
}