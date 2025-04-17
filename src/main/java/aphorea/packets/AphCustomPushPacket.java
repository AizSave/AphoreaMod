package aphorea.packets;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

import java.awt.*;

public class AphCustomPushPacket extends Packet {
    public final int mobUniqueID;
    public final float dirX;
    public final float dirY;
    public final float strength;

    public final int r;
    public final int g;
    public final int b;
    public final int a;

    public AphCustomPushPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.strength = reader.getNextFloat();
        this.r = reader.getNextInt();
        this.g = reader.getNextInt();
        this.b = reader.getNextInt();
        this.a = reader.getNextInt();
    }

    public AphCustomPushPacket(Mob mob, float dirX, float dirY, float strength, Color color) {
        this.mobUniqueID = mob.getUniqueID();
        this.dirX = dirX;
        this.dirY = dirY;
        this.strength = strength;
        if (color == null) {
            this.r = -1;
            this.g = -1;
            this.b = -1;
            this.a = -1;
        } else {
            this.r = color.getRed();
            this.g = color.getGreen();
            this.b = color.getBlue();
            this.a = color.getAlpha();
        }
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(dirX);
        writer.putNextFloat(dirY);
        writer.putNextFloat(strength);
        writer.putNextInt(r);
        writer.putNextInt(g);
        writer.putNextInt(b);
        writer.putNextInt(a);
    }

    public AphCustomPushPacket(Mob mob, float dirX, float dirY, float strength) {
        this(mob, dirX, dirY, strength, null);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (target != null) {
                Color color = null;
                if (r != -1) {
                    color = new Color(r, g, b, a);
                }
                applyToMob(target.getLevel(), target, this.dirX, this.dirY, this.strength, color);
            } else {
                client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
            }

        }
    }

    public static void applyToMob(Level level, Mob mob, float dirX, float dirY, float strength, Color color) {
        float forceX = dirX * strength;
        float forceY = dirY * strength;
        if (Math.abs(mob.dx) < Math.abs(forceX)) {
            mob.dx = forceX;
        }

        if (Math.abs(mob.dy) < Math.abs(forceY)) {
            mob.dy = forceY;
        }

        if (color != null && level != null && level.isClient()) {
            for (int i = 0; i < 30; ++i) {
                level.entityManager.addParticle(mob.x + (float) GameRandom.globalRandom.nextGaussian() * 15.0F + forceX / 10.0F, mob.y + (float) GameRandom.globalRandom.nextGaussian() * 20.0F + forceY / 10.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F, forceY * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F).color(color).height(18.0F).lifeTime(700);
            }
        }

    }
}