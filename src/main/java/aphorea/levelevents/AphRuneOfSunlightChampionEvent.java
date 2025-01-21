package aphorea.levelevents;

import aphorea.utils.AphColors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

import java.util.concurrent.atomic.AtomicReference;

public class AphRuneOfSunlightChampionEvent extends MobAbilityLevelEvent implements Attacker {
    private int lifeTime = 0;
    private int range;

    public AphRuneOfSunlightChampionEvent() {
    }

    public AphRuneOfSunlightChampionEvent(int range, Mob owner) {
        super(owner, new GameRandom());
        this.range = range;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.range);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.range = reader.getNextInt();
    }

    public void init() {
        super.init();
        if (isClient()) {
            getClient().startCameraShake(null, 2500, 60, 3.0F, 3.0F, false);
        }
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 3000) {
            SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(owner.x, owner.y).volume(2.5F).pitch(1.5F));
            owner.getLevel().entityManager.addLevelEvent(new AphRuneOfSunlightChampionExplosionEvent(owner.x, owner.y, range, 0, owner));
            this.over();
        } else if (this.lifeTime <= 2000) {
            GameRandom random = GameRandom.globalRandom;
            AtomicReference<Float> currentAngle = new AtomicReference<>(random.nextFloat() * 360.0F);
            float distance = 5F + 70.0F * (1 - (float) this.lifeTime / 2000);

            for (int i = 0; i < 4; ++i) {
                owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin(currentAngle.get()) * distance + (float) random.getIntBetween(-5, 5), owner.y + GameMath.cos(currentAngle.get()) * distance + (float) random.getIntBetween(-5, 5) * 0.85F, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0F).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(delta * 30.0F / 250.0F, Float::sum);
                    float distY = (distance - 20.0F) * 0.85F;
                    pos.x = owner.x + GameMath.sin(angle) * (distance - distance / 2.0F * lifePercent);
                    pos.y = owner.y + GameMath.cos(angle) * distY - 20.0F * lifePercent;
                }).color((options, lifeTime, timeAlive, lifePercent) -> {
                    options.color(AphColors.fire);
                    if (lifePercent > 0.5F) {
                        options.alpha(2.0F * (1.0F - lifePercent));
                    }

                }).size((options, lifeTime, timeAlive, lifePercent) -> {
                    options.size(22, 22);
                }).lifeTime(1000);
            }
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 3000) {
            owner.getLevel().entityManager.addLevelEvent(new AphRuneOfSunlightChampionExplosionEvent(owner.x, owner.y, range, 0, owner));
            this.over();
        }

    }

}
