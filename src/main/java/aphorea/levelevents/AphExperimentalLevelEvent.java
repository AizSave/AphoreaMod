package aphorea.levelevents;

import aphorea.buffs.ExperimentalBuff;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.regionSystem.RegionPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class AphExperimentalLevelEvent extends HitboxEffectEvent implements Attacker {
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet<>();

    public float angle;

    public AphExperimentalLevelEvent() {
    }

    public AphExperimentalLevelEvent(Mob owner, float angle) {
        super(owner, new GameRandom());
        this.angle = angle;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextFloat(angle);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.angle = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet<>();
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 500) {
            this.over();
        }

        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleProjectileHits(hitBox);
        }

        angle = ExperimentalBuff.getUpdatedAngle(owner, angle);
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 500) {
            this.over();
        }

        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleProjectileHits(hitBox);
        }

        angle = ExperimentalBuff.getUpdatedAngle(owner, angle);
    }

    @Override
    public Shape getHitBox() {
        float width = 20.0F;
        float frontOffset = 20.0F;
        float range = 80.0F;
        float rangeOffset = -40.0F;

        float dirX = (float) Math.cos(angle);
        float dirY = (float) Math.sin(angle);

        Point2D.Float dir = GameMath.getPerpendicularDir(dirX, dirY);


        return new LineHitbox(this.owner.x + dir.x * rangeOffset + dirX * frontOffset, this.owner.y + dir.y * rangeOffset + dirY * frontOffset, dir.x, dir.y, range, width);
    }



    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    public void clientHit(Mob target) {
        this.hits.add(target.getUniqueID());
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, target, 0.2F, this), true);
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0F) {
                float knockback = 100.0F / modifier;
                target.isServerHit(new GameDamage(0.0F), this.owner.x - target.x, this.owner.y - target.y, knockback, this.owner);
            }

            this.hits.add(target.getUniqueID());
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }

    public Set<RegionPosition> getRegionPositions() {
        return this.owner.getRegionPositions();
    }

    protected void handleProjectileHits(Shape hitbox) {
        this.handleProjectileHits(Collections.singleton(hitbox));
    }

    protected void handleProjectileHits(Iterable<Shape> hitboxes) {
        if (this.handlingClient != null) {
            this.streamProjectiles(this.getHitboxesBounds(hitboxes))
                    .filter((p) -> canHit(p.getOwner()))
                    .filter((p) -> this.anyHitboxIntersectsProjectile(hitboxes, p))
                    .forEach(Projectile::remove);
        }
    }

    protected Stream<Projectile> streamProjectiles(Shape hitbox) {
        return this.level.entityManager.projectiles.streamInRegionsShape(hitbox, 1);
    }

    protected boolean anyHitboxIntersectsProjectile(Iterable<Shape> hitBoxes, Projectile projectile) {
        Rectangle targetHitbox = projectile.getHitbox();
        Iterator<Shape> var4 = hitBoxes.iterator();

        Shape hitBox;
        do {
            if (!var4.hasNext()) {
                return false;
            }

            hitBox = var4.next();
        } while (!hitBox.intersects(targetHitbox));

        return true;
    }
}