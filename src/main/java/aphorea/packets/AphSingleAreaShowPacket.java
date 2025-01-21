package aphorea.packets;

import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

import java.awt.*;

public class AphSingleAreaShowPacket extends Packet {
    public final float x;
    public final float y;
    public final float range;
    public final int r;
    public final int g;
    public final int b;
    public final int alpha;

    public AphSingleAreaShowPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.range = reader.getNextFloat();
        this.r = reader.getNextByteUnsigned();
        this.g = reader.getNextByteUnsigned();
        this.b = reader.getNextByteUnsigned();
        this.alpha = reader.getNextByteUnsigned();
    }

    public AphSingleAreaShowPacket(float x, float y, float range, Color color, float alpha) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.alpha = (int) (255 * alpha);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextFloat(range);
        writer.putNextByteUnsigned(this.r);
        writer.putNextByteUnsigned(this.g);
        writer.putNextByteUnsigned(this.b);
        writer.putNextByteUnsigned(this.alpha);
    }


    public AphSingleAreaShowPacket(float x, float y, float range, Color color, int alpha) {
        this(x, y, range, color, (float) alpha / 255);
    }

    public AphSingleAreaShowPacket(float x, float y, float range, Color color) {
        this(x, y, range, color, color.getAlpha());
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            applyToPlayer(client.getLevel(), x, y, range, new Color(r, g, b, alpha));
        }
    }

    public static void applyToPlayer(Level level, float x, float y, float range, Color color, float alpha) {

        AphSingleAreaShowPacket.applyToPlayer(level, x, y, range, new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * alpha)));

    }

    public static void applyToPlayer(Level level, PlayerMob player, float range, Color color) {

        AphSingleAreaShowPacket.applyToPlayer(level, player.x, player.y, range, color);

    }

    public static void applyToPlayer(Level level, float x, float y, float range, Color color) {
        if (level != null && level.isClient()) {
            new AphAreaList(new AphArea(range, color)).showAllAreaParticles(level, x, y);
        }
    }

}