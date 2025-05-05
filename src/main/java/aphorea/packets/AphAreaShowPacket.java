package aphorea.packets;

import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.maps.Level;

import java.awt.*;

public class AphAreaShowPacket extends Packet {
    AphAreaList areaList;
    public final float x;
    public final float y;
    public final float rangeModifier;

    public AphAreaShowPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.rangeModifier = reader.getNextFloat();

        areaList = new AphAreaList();
        while (reader.hasNext()) {
            float range = reader.getNextFloat();
            int colorsLength = reader.getNextInt();
            Color[] colors = new Color[colorsLength];
            for (int i = 0; i < colorsLength; i++) {
                int red = reader.getNextInt();
                int green = reader.getNextInt();
                int blue = reader.getNextInt();
                int alpha = reader.getNextInt();
                colors[i] = new Color(red, green, blue, alpha);
            }
            areaList = areaList.addArea(new AphArea(range, colors));
        }
    }

    public AphAreaShowPacket(float x, float y, AphAreaList areaList, float rangeModifier) {
        this.x = x;
        this.y = y;
        this.rangeModifier = rangeModifier;
        this.areaList = areaList;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextFloat(rangeModifier);
        for (AphArea area : areaList.areas) {
            writer.putNextFloat(area.range);
            writer.putNextInt(area.colors.length);
            for (Color color : area.colors) {
                writer.putNextInt(color.getRed());
                writer.putNextInt(color.getGreen());
                writer.putNextInt(color.getBlue());
                writer.putNextInt(color.getAlpha());
            }
        }
    }

    public AphAreaShowPacket(float x, float y, AphAreaList areaList) {
        this(x, y, areaList, 1);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            applyToPlayer(client.getLevel(), x, y, areaList);
        }
    }

    public static void applyToPlayer(Level level, float x, float y, AphAreaList areaList) {
        if (level != null && level.isClient()) {
            areaList.executeClient(level, x, y);
        }
    }

}