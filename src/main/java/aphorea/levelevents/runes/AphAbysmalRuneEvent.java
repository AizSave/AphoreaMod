package aphorea.levelevents.runes;

import aphorea.utils.AphColors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.regionSystem.RegionPosition;

import java.awt.*;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class AphAbysmalRuneEvent extends HitboxEffectEvent implements Attacker {
    private int lifeTime = 0;
    public int targetX;
    public int targetY;

    public AphAbysmalRuneEvent() {
    }

    public AphAbysmalRuneEvent(Mob owner, int targetX, int targetY) {
        super(owner, new GameRandom());
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        if (this.owner != null) {
            SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(targetX, targetY).volume(1.0F).pitch(0.8F));
        }

    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2000) {
            this.over();
        } else {
            float maxDist = 128.0F;
            int lifeTime = 1000;
            int minHeight = 0;
            int maxHeight = 30;
            int particles = 10;
            for (int i = 0; i < particles; ++i) {
                float height = (float) minHeight + (float) (maxHeight - minHeight) * (float) i / (float) particles;
                AtomicReference<Float> currentAngle = new AtomicReference<>(GameRandom.globalRandom.nextFloat() * 360.0F);
                float outDistance = GameRandom.globalRandom.getFloatBetween(60.0F, maxDist + 32.0F);
                boolean counterclockwise = GameRandom.globalRandom.nextBoolean();
                this.owner.getLevel().entityManager.addParticle(
                                this.targetX + GameRandom.globalRandom.getFloatBetween(0.0F, GameMath.sin(currentAngle.get()) * maxDist),
                                this.targetY + GameRandom.globalRandom.getFloatBetween(0.0F, GameMath.cos(currentAngle.get()) * maxDist * 0.75F),
                                Particle.GType.CRITICAL)
                        .color(GameRandom.globalRandom.getOneOf(AphColors.paletteBlackHole)).height(height).moves((pos, delta, cLifeTime, timeAlive, lifePercent) -> {
                            float angle = currentAngle.accumulateAndGet(delta * 150.0F / 250.0F, Float::sum);
                            if (counterclockwise) {
                                angle = -angle;
                            }

                            float linearDown = GameMath.lerpExp(lifePercent, 0.525F, 0.0F, 1.0F);
                            pos.x = this.targetX + outDistance * GameMath.sin(angle) * (1.0F - linearDown);
                            pos.y = this.targetY + outDistance * GameMath.cos(angle) * (1.0F - linearDown) * 0.75F;
                        }).lifeTime(lifeTime).sizeFades(14, 18);
            }

        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2000) {
            this.over();
        }
    }

    public Shape getHitBox() {
        int size = 150;
        return new Rectangle(this.targetX - size / 2, this.targetY - size / 2, size, size);
    }

    public void clientHit(Mob target) {
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        float modifier = target.getKnockbackModifier();
        if (modifier != 0.0F) {
            float knockback = 5.0F / modifier;
            target.isServerHit(new GameDamage(0.0F), this.targetX - target.x, this.targetY - target.y, knockback, this.owner);
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }

    public Set<RegionPosition> getRegionPositions() {
        return Collections.singleton(this.getLevel().regionManager.getRegionPosByTile(this.targetX / 32, this.targetY / 32));
    }

}