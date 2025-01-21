package aphorea.levelevents;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.*;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;
import java.awt.geom.Point2D;

public class AphRuneOfCrystalDragonEvent extends MobAbilityLevelEvent {
    protected float endDistance;
    protected float effectNumber;
    protected int knockback;
    protected int aliveTime;
    protected float laserAngle;

    protected float currentDistance;
    protected int ticker;
    protected float expandSpeed = 150.0F;
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns();
    private ParticleBeamHandler beamHandler;

    public AphRuneOfCrystalDragonEvent() {
    }

    public AphRuneOfCrystalDragonEvent(Mob owner, GameRandom uniqueIDRandom, float endDistance, float effectNumber, int knockback, int aliveTime, float laserAngle) {
        super(owner, uniqueIDRandom);
        this.endDistance = endDistance;
        this.effectNumber = effectNumber;
        this.knockback = knockback;
        this.aliveTime = aliveTime;
        this.laserAngle = laserAngle;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.endDistance);
        writer.putNextFloat(this.effectNumber);
        writer.putNextInt(this.knockback);
        writer.putNextFloat(this.currentDistance);
        writer.putNextShortUnsigned(this.ticker);
        writer.putNextInt(this.aliveTime);
        writer.putNextFloat(this.laserAngle);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.endDistance = reader.getNextFloat();
        this.effectNumber = reader.getNextFloat();
        this.knockback = reader.getNextInt();
        this.currentDistance = reader.getNextFloat();
        this.ticker = reader.getNextShortUnsigned();
        this.aliveTime = reader.getNextInt();
        this.laserAngle = reader.getNextFloat();
    }

    public boolean isNetworkImportant() {
        return true;
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.isOver() && owner.isPlayer) {
            if (this.currentDistance < this.endDistance) {
                this.currentDistance = Math.min(this.endDistance, this.currentDistance + this.expandSpeed * delta / 250.0F);
            }

            Point2D.Float dir = GameMath.getAngleDir(laserAngle);
            RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(this.level, this.owner.x, this.owner.y, dir.x, dir.y, this.currentDistance, 0, null);

            for (Ray<LevelObjectHit> levelObjectHitRay : rays) {
                this.handleHits(levelObjectHitRay, this::canHit, null);
            }

            if (this.isClient()) {
                this.updateTrail(rays, this.level.tickManager().getDelta());
            }
        }
    }

    public void clientTick() {
        super.clientTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }

        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }

    }

    public void serverTick() {
        super.serverTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }

        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }

    }

    public boolean canHit(Mob mob) {
        return mob.canBeHit(this) && this.hitCooldowns.canHit(mob);
    }

    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.hitCooldowns.startCooldown(target);
        target.startHitCooldown();
    }

    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        this.hitCooldowns.startCooldown(target);
        float modifier = target.getKnockbackModifier();
        if (modifier != 0.0F) {
            float damagePercent = effectNumber;
            if (target.isBoss()) {
                damagePercent /= 50;
            } else if (target.isPlayer || target.isHuman) {
                damagePercent /= 5;
            }
            target.isServerHit(new GameDamage(target.getMaxHealth() * damagePercent, 1000000), target.x - this.owner.x, target.y - this.owner.y, (float) this.knockback, this.owner);
        }

    }

    private void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta) {
        if (this.beamHandler == null) {
            this.beamHandler = (new ParticleBeamHandler(this.level)).color(new Color(200, 200, 255)).thickness(160, 80).speed(100.0F).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
        }

        this.beamHandler.update(rays, delta);
    }

    public void over() {
        if (this.beamHandler != null) {
            this.beamHandler.dispose();
        }

        super.over();
    }

}
