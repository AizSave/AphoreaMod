package aphorea.registry;

import aphorea.buffs.LowdsPoisonBuff;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.packets.*;
import necesse.engine.registries.PacketRegistry;

public class AphPackets {
    public static void registerCore() {
        PacketRegistry.registerPacket(AphCustomPushPacket.class);
        PacketRegistry.registerPacket(AphRunesInjectorAbilityPacket.class);
        PacketRegistry.registerPacket(AphRuneOfUnstableGelSlimePacket.class);

        clientOnly();
    }

    public static void clientOnly() {
        PacketRegistry.registerPacket(AphSingleAreaShowPacket.class);
        PacketRegistry.registerPacket(LowdsPoisonBuff.LowdsPoisonBuffPacket.class);
        PacketRegistry.registerPacket(WildPhosphorSlime.PhosphorSlimeParticlesPacket.class);
        PacketRegistry.registerPacket(AphRemoveObjectEntity.class);
    }
}
