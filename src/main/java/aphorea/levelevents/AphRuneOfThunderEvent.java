package aphorea.levelevents;

import aphorea.particles.AphRuneOfThunderParticle;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.trails.LightningTrail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.regionSystem.RegionPosition;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AphRuneOfThunderEvent extends HitboxEffectEvent implements Attacker {
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet<>();
    public int targetX;
    public int targetY;

    public float effectNumber;

    private boolean showedLightning = false;

    public AphRuneOfThunderEvent() {
    }

    public AphRuneOfThunderEvent(Mob owner, int targetX, int targetY, float effectNumber) {
        super(owner, new GameRandom());
        this.targetX = targetX;
        this.targetY = targetY;
        this.effectNumber = effectNumber;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextFloat(this.effectNumber);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.effectNumber = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet<>();
        if (this.isClient()) {
            this.level.entityManager.addParticle(new AphRuneOfThunderParticle(this.level, targetX, targetY, this.getWorldEntity().getTime()), Particle.GType.CRITICAL);
        }

        showedLightning = false;
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2100) {
            this.over();
        } else if (this.lifeTime >= 2000) {
            if (!showedLightning) {
                showedLightning = true;

                SoundManager.playSound(GameResources.electricExplosion, SoundEffect.effect(targetX, targetY).volume(1.2F).pitch(0.8F));

                float initialMoveX = GameRandom.globalRandom.getIntBetween(-20, 20);
                float initialMoveY = GameRandom.globalRandom.getIntBetween(-20, 20);

                for (int i = 0; i < 6; i++) {
                    float finalMoveX;
                    float finalMoveY;
                    if (i == 0) {
                        finalMoveX = 0;
                        finalMoveY = 0;
                    } else {
                        finalMoveX = GameRandom.globalRandom.getIntBetween(50, 80) * (GameRandom.globalRandom.getChance(0.5F) ? -1 : 1);
                        finalMoveY = GameRandom.globalRandom.getIntBetween(50, 80) * (GameRandom.globalRandom.getChance(0.5F) ? -1 : 1);
                    }

                    float prevX = targetX;
                    float prevY = targetY;

                    LightningTrail trail = new LightningTrail(new TrailVector(prevX, prevY, 0, 0, i == 0 ? 20 : GameRandom.globalRandom.getFloatBetween(10, 15), 0), this.level, this.level.isCave ? AphColors.palettePinkWitch[2] : AphColors.lighting);
                    this.level.entityManager.addTrail(trail);

                    for (int j = i == 0 ? 1 : i + 2; j < 10; j++) {
                        float progression = (float) j / 10;
                        float height = 500 * progression;
                        float newX = targetX + GameRandom.globalRandom.getIntBetween(-5, 5) + finalMoveY * (1 - progression) + initialMoveX * progression;
                        float newY = targetY + GameRandom.globalRandom.getIntBetween(-5, 5) + finalMoveX * (1 - progression) + initialMoveY * progression;
                        trail.addNewPoint(new TrailVector(newX, newY, newX - prevX, newY - prevY, trail.thickness, height));
                        prevX = newX;
                        prevY = newY;
                    }
                }
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
        if (lifeTime >= 2000) {
            int size = 100;
            return new Rectangle(this.targetX - size / 2, this.targetY - size / 2, size, size);
        } else {
            return new Rectangle();
        }
    }

    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    public void clientHit(Mob target) {
        this.hits.add(target.getUniqueID());
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0F) {
                float knockback = 10.0F / modifier;
                float damagePercent = effectNumber;
                if (target.isBoss()) {
                    damagePercent /= 50;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5;
                }
                target.isServerHit(new GameDamage(target.getMaxHealth() * damagePercent, 1000000), target.x - this.owner.x, target.y - this.owner.y, knockback, this.owner);
                target.addBuff(new ActiveBuff(AphBuffs.STUN, target, 2000, this), true);
            }
            this.hits.add(target.getUniqueID());
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }

    public Set<RegionPosition> getRegionPositions() {
        return Collections.singleton(this.getLevel().regionManager.getRegionPosByTile(this.targetX / 32, this.targetY / 32));
    }
}
