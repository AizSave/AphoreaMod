package aphorea.registry;

import aphorea.buffs.VenomExtractBuff;
import aphorea.items.tools.weapons.melee.sword.TheNarcissist;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.packets.*;
import aphorea.particles.SpinelCureParticle;
import necesse.engine.registries.PacketRegistry;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class AphPackets {

    public static void registerCore() {
        PacketRegistry.registerPacket(AphCustomPushPacket.class);
        PacketRegistry.registerPacket(AphRunesInjectorAbilityPacket.class);
        PacketRegistry.registerPacket(AphRuneOfUnstableGelSlimePacket.class);

        clientOnly();
        particlePackets();
    }

    public static void clientOnly() {
        PacketRegistry.registerPacket(AphAreaShowPacket.class);
        PacketRegistry.registerPacket(VenomExtractBuff.VenomExtractBuffPacket.class);
        PacketRegistry.registerPacket(WildPhosphorSlime.PhosphorSlimeParticlesPacket.class);
        PacketRegistry.registerPacket(AphRemoveObjectEntity.class);
        PacketRegistry.registerPacket(TheNarcissist.NarcissistHitMob.class);
    }


    public static class PARTICLES {
        public static class SpinelCureParticlePacket extends AphParticlePacket<SpinelCureParticle> {
            public SpinelCureParticlePacket(byte[] data) {
                super(data);
            }

            public SpinelCureParticlePacket(float x, float y, long lifeTime, Particle.GType gType) {
                super(x, y, lifeTime, gType);
            }

            @Override
            public SpinelCureParticle getParticle(Level level) {
                return new SpinelCureParticle(level, x, y, lifeTime);
            }
        }
    }

    public static void particlePackets() {
        PacketRegistry.registerPacket(PARTICLES.SpinelCureParticlePacket.class);
    }
}
