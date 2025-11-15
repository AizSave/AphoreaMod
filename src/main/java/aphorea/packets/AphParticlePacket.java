package aphorea.packets;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

abstract public class AphParticlePacket<T extends Particle> extends Packet {
    public float x;
    public float y;
    public long lifeTime;
    public Particle.GType gType;

    public AphParticlePacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.lifeTime = reader.getNextLong();
        this.gType = Particle.GType.values()[reader.getNextInt()];
    }

    public AphParticlePacket(float x, float y, long lifeTime, Particle.GType gType) {
        this.x = x;
        this.y = y;
        this.lifeTime = lifeTime;
        this.gType = gType;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextLong(lifeTime);
        writer.putNextInt(gType.ordinal());
    }

    abstract public T getParticle(Level level);

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            client.getLevel().entityManager.addParticle(getParticle(client.getLevel()), gType);
        }
    }
}
