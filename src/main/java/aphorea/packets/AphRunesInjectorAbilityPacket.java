package aphorea.packets;

import aphorea.other.buffs.trinkets.AphBaseRuneTrinketBuff;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class AphRunesInjectorAbilityPacket extends Packet {
    public final int slot;
    public final int buffID;

    public AphRunesInjectorAbilityPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.buffID = reader.getNextShortUnsigned();
    }

    public AphRunesInjectorAbilityPacket(int slot, Buff buff) {
        this.slot = slot;
        this.buffID = buff.getID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextShortUnsigned(this.buffID);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            ClientClient target = client.getClient(this.slot);
            if (target != null && target.isSamePlace(client.getLevel())) {

                ActiveBuff buff = target.playerMob.buffManager.getBuff(this.buffID);
                if (buff != null && buff.buff instanceof AphBaseRuneTrinketBuff) {
                    AphBaseRuneTrinketBuff buffAbility = (AphBaseRuneTrinketBuff)buff.buff;
                    buffAbility.runClient(client, target.playerMob);
                }
            } else {
                client.network.sendPacket(new PacketRequestPlayerData(this.slot));
            }

        }
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            if (!client.checkHasRequestedSelf() || client.isDead()) {
                return;
            }

            ActiveBuff buff = client.playerMob.buffManager.getBuff(this.buffID);
            if (buff != null && buff.buff instanceof AphBaseRuneTrinketBuff) {
                AphBaseRuneTrinketBuff buffAbility = (AphBaseRuneTrinketBuff)buff.buff;
                String error = buffAbility.canRun(client.playerMob);
                if(error != null) {
                    if(!error.isEmpty()) {
                        client.sendChatMessage(new LocalMessage("message", error));
                    }
                } else {
                    buffAbility.runServer(server, client.playerMob);
                    server.network.sendToAllClients(new AphRunesInjectorAbilityPacket(this.slot, buff.buff));
                }
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run active trinket buff ability from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }

    }
}