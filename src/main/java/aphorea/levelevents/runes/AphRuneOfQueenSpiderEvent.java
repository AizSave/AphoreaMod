package aphorea.levelevents.runes;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.WebWeaverWebParticle;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class AphRuneOfQueenSpiderEvent extends GroundEffectEvent {
    protected int tickCounter;
    protected MobHitCooldowns hitCooldowns;
    protected WebWeaverWebParticle particle;
    protected int duration;

    public AphRuneOfQueenSpiderEvent() {
    }

    public AphRuneOfQueenSpiderEvent(Mob owner, int x, int y, int duration, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
        this.duration = duration;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(duration);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        duration = reader.getNextInt();
    }

    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient()) {
            this.level.entityManager.addParticle(this.particle = new WebWeaverWebParticle(this.level, (float) this.x, (float) this.y, duration, 0), true, Particle.GType.CRITICAL);
        }
    }

    public Shape getHitBox() {
        int width = 180;
        int height = 136;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob mob) {

    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !target.buffManager.hasBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW)) {
            target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, target, 1000, this), true);
        }

    }

    public void hitObject(LevelObjectHit hit) {
    }

    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > duration / 50) {
            this.over();
        } else {
            super.clientTick();
        }

    }

    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > duration / 50) {
            this.over();
        } else {
            super.serverTick();
        }

    }

    public void over() {
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }

    }
}