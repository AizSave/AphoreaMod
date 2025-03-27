package aphorea.buffs;

import aphorea.utils.AphColors;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class LowdsPoisonBuff extends Buff {
    public LowdsPoisonBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.isVisible = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.MAX_RESILIENCE, -100F);
        buff.addModifier(BuffModifiers.RESILIENCE_REGEN, -100F);
        buff.addModifier(BuffModifiers.HEALTH_REGEN, -100F);
        buff.addModifier(BuffModifiers.COMBAT_HEALTH_REGEN, -100F);
        buff.addModifier(BuffModifiers.BLINDNESS, 0.25F);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.getHealth() > 1) {
            buff.owner.setHealth(1);
            if (buff.owner.isPlayer) {
                ServerClient serverClient = ((PlayerMob) buff.owner).getServerClient();
                buff.owner.getServer().network.sendToClientsAtEntireLevel(new LowdsPoisonBuffPacket(serverClient.slot), buff.owner.getLevel());
            }
        }
    }

    public static class LowdsPoisonBuffPacket extends Packet {
        public final int slot;

        public LowdsPoisonBuffPacket(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            this.slot = reader.getNextByteUnsigned();
        }

        public LowdsPoisonBuffPacket(int slot) {
            this.slot = slot;
            PacketWriter writer = new PacketWriter(this);
            writer.putNextByteUnsigned(slot);
        }

        public void processClient(NetworkPacket packet, Client client) {
            if (client.getLevel() != null) {
                ClientClient target = client.getClient(this.slot);
                if (target != null && target.isSamePlace(client.getLevel())) {
                    applyToPlayer(target.playerMob.getLevel(), target.playerMob);
                } else {
                    client.network.sendPacket(new PacketRequestPlayerData(this.slot));
                }

            }
        }

        public static void applyToPlayer(Level level, Mob mob) {
            mob.setHealth(1);
            if (level != null && level.isClient()) {
                for (int i = 0; i < 40; i++) {
                    int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                    float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    mob.getLevel().entityManager.addParticle(mob, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(GameRandom.globalRandom.getOneOf(AphColors.nature, AphColors.blood)).heightMoves(20.0F, 0.0F).lifeTime(500);
                }
            }

        }
    }

}
