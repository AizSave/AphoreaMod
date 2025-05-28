package aphorea.buffs.Trinkets.Shield;

import aphorea.levelevents.AphSpinelShieldEvent;
import aphorea.utils.AphColors;
import necesse.engine.Settings;
import necesse.engine.input.Control;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;

public class SpinelShieldBuff extends TrinketBuff implements ActiveBuffAbility {
    public static int msToDeplete = 6000;

    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(
            Particle.GType.CRITICAL,
            Particle.GType.IMPORTANT_COSMETIC,
            Particle.GType.COSMETIC
    );

    public SpinelShieldBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public Packet getStartAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return this.getRunningAbilityContent(player, buff);
    }

    public Packet getRunningAbilityContent(PlayerMob player, ActiveBuff buff) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        StaminaBuff.writeStaminaData(player, writer);
        return content;
    }

    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        if (buff.owner.isRiding() || buff.owner.buffManager.hasBuff("spinelshieldactive")) {
            return false;
        } else {
            return player.isServer() && Settings.giveClientsPower || StaminaBuff.canStartStaminaUsage(buff.owner);
        }
    }

    public int clientTicks = 0;
    public int serverTicks = 0;

    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        PacketReader reader = new PacketReader(content);
        if (!player.isServer() || Settings.giveClientsPower) {
            StaminaBuff.readStaminaData(player, reader);
        }
        if (player.isServer()) {
            serverTicks = 0;
        }
        if (player.isClient()) {
            clientTicks = 0;
        }
    }

    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        if (Control.TRINKET_ABILITY.isDown()) {
            ActiveBuff shieldBuff = player.buffManager.getBuff(BuffRegistry.getBuff("spinelshieldactive"));
            if (shieldBuff != null) {
                if (shieldBuff.getDurationLeft() < 200) {
                    shieldBuff.setDurationLeftSeconds(0.2F);
                }
            } else {
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff("spinelshieldactive"), player, 1.0F, null), false);
            }
        }

        if (player.isServer()) {
            if (serverTicks < 10) {
                serverTicks++;
                if (player.isServer() && serverTicks == 10) {
                    buff.owner.getLevel().entityManager.addLevelEvent(new AphSpinelShieldEvent(buff.owner, getInitialAngle(buff.owner)));
                }
            }
        }

        if (player.isClient()) {
            if (clientTicks < 6) {
                clientTicks++;
                for (int i = 0; i < 2; i++) {
                    int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                    float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    player.getLevel().entityManager.addParticle(player.moveX * 3 + player.x - dx, player.moveY * 3 + player.y - dy, particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel)).heightMoves(10.0F, 20.0F, 5F, 0F, 10F, 0F).lifeTime(250);
                }
            }
        }


        float usage = 50.0F / msToDeplete;
        if (!StaminaBuff.useStaminaAndGetValid(player, usage)) {
            return false;
        }

        return !isRunningClient || Control.TRINKET_ABILITY.isDown() || player.buffManager.hasBuff("spinelshieldactive");
    }

    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        player.buffManager.removeBuff(BuffRegistry.getBuff("spinelshieldactive"), false);
    }

    public static float getInitialAngle(Mob mob) {
        if (mob.moveX == 0 && mob.moveY == 0) {
            switch (mob.getDir()) {
                case 0:
                    return (float) (-Math.PI / 2);
                case 1:
                    return 0f;
                case 2:
                    return (float) (Math.PI / 2);
                case 3:
                    return (float) Math.PI;
                default:
                    return 0f;
            }
        } else {
            return (float) Math.atan2(mob.moveY, mob.moveX);
        }
    }

}
