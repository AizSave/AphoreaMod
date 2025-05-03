package aphorea.levelevents;

import aphorea.particles.NarcissistParticle;
import aphorea.utils.AphColors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AphNarcissistEvent extends HitboxEffectEvent implements Attacker {
    public static int maxLifeTime = 5000;
    private int lifeTime = 0;
    public float startX;
    public float startY;
    public float moveX;
    public float moveY;
    public float startAngle;
    public GameDamage damage;
    private HashMap<Integer, Long> mobHits;

    public NarcissistParticle particle;

    public AphNarcissistEvent() {
    }

    public AphNarcissistEvent(Mob owner, float startAngle, float attackHeight, GameDamage damage) {
        super(owner, new GameRandom());
        this.startX = owner.x;
        this.startY = owner.y - attackHeight;
        this.moveX = 0;
        this.moveY = 0;
        this.startAngle = startAngle;
        this.damage = damage;

        this.hitsObjects = true;
    }

    @Override
    public void init() {
        super.init();
        mobHits = new HashMap<>();

        if (isClient()) {
            getLevel().entityManager.addParticle(particle = new NarcissistParticle(getLevel(), owner, startX, startY, startAngle), Particle.GType.CRITICAL);
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(startX);
        writer.putNextFloat(startY);
        writer.putNextFloat(moveX);
        writer.putNextFloat(moveY);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextFloat(startAngle);
        writer.putNextInt(DamageTypeRegistry.getDamageTypeID(damage.type.getStringID()));
        writer.putNextFloat(damage.damage);
        writer.putNextFloat(damage.armorPen);

        writer.putNextShortUnsigned(this.mobHits.size());

        for (Map.Entry<Integer, Long> integerLongEntry : this.mobHits.entrySet()) {
            writer.putNextInt((Integer) ((Map.Entry<?, ?>) integerLongEntry).getKey());
            writer.putNextLong((Long) ((Map.Entry<?, ?>) integerLongEntry).getValue());
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextFloat();
        this.startY = reader.getNextFloat();
        this.moveX = reader.getNextFloat();
        this.moveY = reader.getNextFloat();
        this.lifeTime = reader.getNextShortUnsigned();
        this.startAngle = reader.getNextFloat();
        this.damage = new GameDamage(DamageTypeRegistry.getDamageType(reader.getNextInt()), reader.getNextFloat(), reader.getNextFloat());

        int size = reader.getNextShortUnsigned();
        this.mobHits = new HashMap<>(size);

        for (int i = 0; i < size; ++i) {
            int uniqueID = reader.getNextInt();
            long time = reader.getNextLong();
            this.mobHits.put(uniqueID, time);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;

        if (this.lifeTime >= maxLifeTime) {
            // Handle weapon end and generate final explosion
            this.over();
            for (int i = 0; i < 20; i++) {
                SoundManager.playSound(GameResources.cling, SoundEffect.effect(getX(), getY()).volume(0.25F));
                int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                float dx = (float) Math.sin(Math.toRadians(angle)) * GameRandom.globalRandom.getIntBetween(40, 80);
                float dy = (float) Math.cos(Math.toRadians(angle)) * GameRandom.globalRandom.getIntBetween(40, 80);
                owner.getLevel().entityManager.addParticle(
                                getX(), getY(),
                                new ParticleTypeSwitcher(
                                        Particle.GType.CRITICAL,
                                        Particle.GType.IMPORTANT_COSMETIC,
                                        Particle.GType.COSMETIC
                                ).next()
                        )
                        .movesFriction(dx, dy, 0.8F)
                        .color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel))
                        .heightMoves(10.0F, 20.0F, 2F, 0.5F, 0F, 0F)
                        .lifeTime(500);
            }

        } else {
            // Particles while the weapon is still spinning
            // Current angular speed (rad/s)
            float angularSpeed = getAngularSpeed(getLifePercent(), getDir(startAngle));
            // Number of particles proportional to the angular speed
            int particleCount = (int) (Math.pow(Math.abs(angularSpeed) - 20, 1.5) / 15);

            // Manually calculated hitbox values
            float angle = getAngle();
            float dirX = (float) Math.cos(angle);
            float dirY = (float) Math.sin(angle);
            float length = 100f;
            float startX = getX() - dirX * length / 2f;
            float startY = getY() - dirY * length / 2f;
            // Center of the line for angular inertia
            float centerX = startX + dirX * length / 2f;
            float centerY = startY + dirY * length / 2f;

            for (int i = 0; i < particleCount; i++) {
                // Random point along the hitbox
                float t = GameRandom.globalRandom.nextFloat();
                float x = startX + dirX * length * t;
                float y = startY + dirY * length * t;

                // Distance to center (normalized 0..1)
                float dist = (float) Math.hypot(x - centerX, y - centerY);
                float distFactor = dist / (length / 2f);

                // Base speed scaled by distance and random variation
                float speed = angularSpeed * distFactor * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F);

                float dx = -dirY * speed;
                float dy = dirX * speed;

                // Spawn the particle
                owner.getLevel().entityManager.addParticle(
                                x, y,
                                new ParticleTypeSwitcher(
                                        Particle.GType.CRITICAL,
                                        Particle.GType.IMPORTANT_COSMETIC,
                                        Particle.GType.COSMETIC
                                ).next()
                        )
                        .movesFriction(dx, dy, 0.9F)
                        .color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel))
                        .heightMoves(14F, -4F, 2F, 0.5F, 0F, 0F)
                        .lifeTime(500);
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= maxLifeTime) {
            this.over();
        }
    }

    @Override
    public void tickMovement(float delta) {
        float percent = easeInSine(getLifePercent());

        // Track closest
        Mob closestMob = GameUtils.streamTargetsRange(owner, (int) getX(), (int) getY(), (int) (500 * percent))
                .min(Comparator.comparingDouble(m -> m.getDistance(getX(), getY())))
                .orElse(null);

        // Move to the closest mob if exists
        if (closestMob != null) {
            float angle = (float) Math.atan2(closestMob.y - getY(), closestMob.x - getX());
            moveX += (float) (Math.cos(angle) * 50 * percent * delta / 250);
            moveY += (float) (Math.sin(angle) * 50 * percent * delta / 250);
            if (isClient()) {
                particle.moveX = moveX;
                particle.moveY = moveY;
            }
        }
    }

    @Override
    public boolean canHit(Mob mob) {
        if (!super.canHit(mob)) return false;
        if (!this.mobHits.containsKey(mob.getHitCooldownUniqueID())) {
            return true;
        } else {
            return this.mobHits.get(mob.getHitCooldownUniqueID()) + 200 < this.getTime();
        }
    }

    protected void startCooldown(Mob target) {
        this.mobHits.put(target.getHitCooldownUniqueID(), target.getTime());
        target.startHitCooldown();
    }

    @Override
    public void clientHit(Mob mob) {
        this.startCooldown(mob);
        for (int i = 0; i < 5; i++) {
            int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
            float dx = (float) Math.sin(Math.toRadians(angle)) * GameRandom.globalRandom.getIntBetween(30, 50);
            float dy = (float) Math.cos(Math.toRadians(angle)) * GameRandom.globalRandom.getIntBetween(30, 50);
            owner.getLevel().entityManager.addParticle(
                            mob.x, mob.y,
                            new ParticleTypeSwitcher(
                                    Particle.GType.CRITICAL,
                                    Particle.GType.IMPORTANT_COSMETIC,
                                    Particle.GType.COSMETIC
                            ).next()
                    )
                    .movesFriction(dx, dy, 0.8F)
                    .color(GameRandom.globalRandom.getOneOf(AphColors.spinel_light, AphColors.spinel))
                    .heightMoves(10.0F, 20.0F, 2F, 0.5F, 0F, 0F)
                    .lifeTime(500);
        }
    }

    @Override
    public void serverHit(Mob mob, boolean clientSubmitted) {
        this.startCooldown(mob);
        mob.isServerHit(damage, mob.x - getX(), mob.y - getY(), 50, this.owner);
    }

    public float getX() {
        return getX(startX, startAngle, getLifePercent()) + moveX;
    }

    public float getY() {
        return getY(startY, startAngle, getLifePercent()) + moveY;
    }

    public float getAngle() {
        return getAngle(startAngle, getLifePercent());
    }

    public float getLifePercent() {
        return (float) lifeTime / maxLifeTime;
    }

    @Override
    public Shape getHitBox() {
        float angle = getAngle();
        float dirX = (float) Math.cos(angle);
        float dirY = (float) Math.sin(angle);

        float length = 100;
        float width = 20;

        float startX = getX() - dirX * length / 2;
        float startY = getY() - dirY * length / 2;

        return new LineHitbox(startX, startY, dirX, dirY, length, width);
    }

    @Override
    public void hitObject(LevelObjectHit levelObjectHit) {
    }

    public static float getX(float startX, float angle, float lifePercent) {
        return startX + (float) Math.cos(angle) * distanceTraveled(lifePercent);
    }

    public static float getY(float startY, float angle, float lifePercent) {
        return startY + (float) Math.sin(angle) * distanceTraveled(lifePercent);
    }

    public static float angleOffSet(int startDir) {
        switch (startDir) {
            case 0:
                return 180;
            case 1:
                return -45;
            case 2:
                return 0;
            case 3:
                return -90;
            default:
                return 0;
        }
    }

    public static float getAngle(float startAngle, float lifePercent) {
        int dir = getDir(startAngle);
        return angleOffSet(dir) + easeInSine(lifePercent) * (float) Math.toRadians(3600) * (dir == 3 ? -1 : 1);
    }

    public static float distanceTraveled(float lifePercent) {
        return easeOutCirc(lifePercent) * 100;
    }

    public static float easeOutCirc(float x) {
        return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public static float easeInSine(float x) {
        return (float) (1 - Math.cos((x * Math.PI) / 2));
    }

    public static float getAngularSpeed(float lifePercent, int startAngle) {
        float dirFactor = (getDir(startAngle) == 3 ? -1 : 1);
        float constant = (float) Math.toRadians(360 * 4);
        float derivativeEase = (float) (Math.sin((lifePercent * Math.PI) / 2) * (Math.PI / 2));
        return derivativeEase * constant * dirFactor;
    }

    public static int getDir(float startAngle) {
        float twoPi = (float) (2 * Math.PI);
        switch (Math.round(((startAngle % twoPi + twoPi) % twoPi) / (float) (Math.PI / 2)) % 4) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 0;
            default:
                return -1;
        }
    }

}
