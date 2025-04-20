package aphorea.levelevents.runes;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

import java.awt.*;

public class AphRuneOfSunlightChampionExplosionEvent extends ExplosionEvent implements Attacker {
    private int particleBuffer;
    protected ParticleTypeSwitcher explosionTypeSwitcher;

    public AphRuneOfSunlightChampionExplosionEvent() {
        super(0, 0, 0, new GameDamage(0), false, 0);
    }

    public AphRuneOfSunlightChampionExplosionEvent(float x, float y, int range, int toolTier, Mob owner) {
        super(x, y, range, new GameDamage(0), false, toolTier, owner);
        this.explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
        this.targetRangeMod = 0.0F;
        this.hitsOwner = false;
    }

    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0F);
    }

    protected void playExplosionEffects() {
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0F, 3.0F, true);
    }

    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 1.5F;
    }

    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (this.particleBuffer < 10) {
            ++this.particleBuffer;
        } else {
            this.particleBuffer = 0;
            if (range <= (float) Math.max(this.range - 125, 25)) {
                float dx = dirX * (float) GameRandom.globalRandom.getIntBetween(140, 150);
                float dy = dirY * (float) GameRandom.globalRandom.getIntBetween(130, 140) * 0.8F;
                this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 100).givesLight(53.0F, 1.0F).movesFriction(dx * 0.05F, dy * 0.05F, 0.8F).color((options, lifeTime1, timeAlive, lifePercent) -> {
                    float clampedLifePercent = Math.max(0.0F, Math.min(1.0F, lifePercent));
                    options.color(new Color((int) (255.0F - 55.0F * clampedLifePercent), (int) (225.0F - 200.0F * clampedLifePercent), (int) (155.0F - 125.0F * clampedLifePercent)));
                }).heightMoves(0.0F, 10.0F).lifeTime(lifeTime * 3);
            }
        }

    }

    @Override
    protected void onMobWasHit(Mob mob, float distance) {
        float mod = this.getDistanceMod(distance);
        float damagePercent = 2F;
        if (mob.isBoss()) {
            damagePercent /= 50;
        } else if (mob.isPlayer || mob.isHuman) {
            damagePercent /= 5;
        }
        GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, mob.getMaxHealth() * damagePercent * mod);
        float knockback = (float) this.knockback * mod;
        mob.isServerHit(damage, (float) mob.getX() - this.x, (float) mob.getY() - this.y, knockback, this);
    }

}
