package aphorea.levelevents;

import aphorea.particles.SpinelShieldParticle;
import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class AphSpinelShieldEvent extends ProjectileShieldLevelEvent implements Attacker {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(
            Particle.GType.CRITICAL,
            Particle.GType.IMPORTANT_COSMETIC,
            Particle.GType.COSMETIC
    );

    static public float maxDelta = (float) Math.toRadians(15);

    public AphSpinelShieldEvent() {
    }

    public AphSpinelShieldEvent(Mob owner, float angle) {
        super(owner, angle, new GameRandom());
    }

    @Override
    public void init() {
        super.init();

        if (isClient()) {
            SoundManager.playSound(GameResources.cling, SoundEffect.effect(owner));
            for (int i = 0; i < 20; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                owner.getLevel().entityManager.addParticle(owner, particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel)).heightMoves(10.0F, 20.0F, 2F, 0.5F, 0F, 0F).lifeTime(500);
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!owner.buffManager.hasBuff("spinelshieldactive")) {
            this.over();
        }

        getLevel().entityManager.addParticle(new SpinelShieldParticle(getLevel(), owner, angle), Particle.GType.CRITICAL);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!owner.buffManager.hasBuff("spinelshieldactive")) {
            this.over();
        }
    }

    @Override
    public Shape getHitBox() {
        return getShieldHitBox(20.0F, 80.0F, 40.0F, -40.0F);
    }

    @Override
    public boolean canHit(Mob mob) {
        return mob == null || (super.canHit(mob) && mob.isHostile);
    }

    @Override
    public void clientHit(Mob mob) {
        float modifier = mob.getKnockbackModifier();
        if (modifier != 0.0F) {
            StaminaBuff.useStaminaAndGetValid(owner, 0.01F);
            SoundManager.playSound(GameResources.cling, SoundEffect.effect(mob.x, mob.y).volume(0.25F));
            for (int i = 0; i < 5; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                mob.getLevel().entityManager.addParticle(mob.x, mob.y, particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel)).heightMoves(10.0F, 20.0F, 2F, 0.5F, 0F, 0F).lifeTime(500);
            }
        }
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted) {
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, target, 0.2F, this), true);
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0F) {
                StaminaBuff.useStaminaAndGetValid(owner, 0.01F);
                float knockback = 50.0F / modifier;
                target.isServerHit(new GameDamage(0.0F), target.x - this.owner.x, target.y - this.owner.y, knockback, this.owner);
            }
        }
    }

    @Override
    protected void onProjectileHit(Projectile projectile) {
        StaminaBuff.useStaminaAndGetValid(owner, 0.05F);
        if (projectile.isClient()) {
            SoundManager.playSound(GameResources.cling, SoundEffect.effect(projectile.x, projectile.y).volume(0.5F));
            for (int i = 0; i < 10; i++) {
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                projectile.getLevel().entityManager.addParticle(projectile.x, projectile.y, particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel)).heightMoves(10.0F, 20.0F, 2F, 0.5F, 0F, 0F).lifeTime(500);
            }
        }
        projectile.remove();
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public float getMaxDelta() {
        return maxDelta;
    }
}