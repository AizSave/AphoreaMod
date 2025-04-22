package aphorea.levelevents;

import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;

import java.awt.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

abstract public class ProjectileHitboxEffectEvent extends HitboxEffectEvent {

    public ProjectileHitboxEffectEvent() {
    }

    public ProjectileHitboxEffectEvent(Mob owner, GameRandom uniqueIDRandom) {
        super(owner, uniqueIDRandom);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleProjectileHits(hitBox);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleProjectileHits(hitBox);
        }
    }

    abstract protected void onProjectileHit(Projectile projectile);

    protected void handleProjectileHits(Shape hitbox) {
        this.handleProjectileHits(Collections.singleton(hitbox));
    }

    protected void handleProjectileHits(Iterable<Shape> hitboxes) {
        if (this.handlingClient != null) {
            this.streamProjectiles(this.getHitboxesBounds(hitboxes))
                    .filter((p) -> canHit(p.getOwner()))
                    .filter((p) -> this.anyHitboxIntersectsProjectile(hitboxes, p))
                    .filter((p) -> {
                        Mob owner = p.getOwner();
                        if (owner == null) return true;
                        if (owner == this.getAttackOwner()) return false;
                        return owner.canBeTargeted(this.getAttackOwner(), this.getAttackOwner().isPlayer ? ((PlayerMob) this.getAttackOwner()).getNetworkClient() : null);
                    })
                    .forEach(this::onProjectileHit);
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
