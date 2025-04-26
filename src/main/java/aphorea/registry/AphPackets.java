package aphorea.registry;

import aphorea.buffs.VenomExtractBuff;
import aphorea.items.tools.weapons.melee.sword.TheNarcissist;
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
        PacketRegistry.registerPacket(VenomExtractBuff.VenomExtractBuffPacket.class);
        PacketRegistry.registerPacket(WildPhosphorSlime.PhosphorSlimeParticlesPacket.class);
        PacketRegistry.registerPacket(AphRemoveObjectEntity.class);
        PacketRegistry.registerPacket(TheNarcissist.NarcissistHitMob.class);
    }
}
