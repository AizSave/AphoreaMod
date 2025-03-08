package aphorea.projectiles.toolitem;

import aphorea.items.healingtools.AphHealingProjectileToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.AphDistances;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpinelWandProjectile extends FollowingProjectile {
    Color color = AphColors.spinel;
    AphHealingProjectileToolItem toolItem;
    InventoryItem item;
    int healing;


    AphAreaList areaList = new AphAreaList(
            new AphArea(150, color)
    );

    public SpinelWandProjectile(int healing, AphHealingProjectileToolItem toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.healing = healing;
        this.toolItem = toolItem;
        this.item = item;

        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
    }

    public SpinelWandProjectile() {
    }

    @Override
    public void init() {
        super.init();

        this.turnSpeed = 0.2F;
        piercing = 0;
        bouncing = 0;
        this.doesImpactDamage = false;
        this.knockback = 0;
        this.canBreakObjects = false;
        this.canHitMobs = true;
        this.givesLight = true;

        this.setWidth(0, 5);

        this.areaList = new AphAreaList(
                new AphArea(100, color).setHealingArea(healing)
        );
    }

    @Override
    public boolean canHit(Mob mob) {
        return AphMagicHealing.canHealMob(this.getOwner(), mob) && this.getOwner() != mob;
    }

    @Override
    public void updateTarget() {
        super.updateTarget();
        if (traveledDistance > 20) {
            this.target = null;
            target = AphDistances.findClosestMob(getLevel(), x, y, this.distance / 2, this::canHit);
        }
    }

    @Override
    public Color getParticleColor() {
        return color;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), color, 26, 500, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null && this.amountHit() < this.piercing) {
            return;
        } else {
            int bouncing = this.bouncing;
            Mob owner = this.getOwner();
            if (owner != null) {
                bouncing += owner.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES);
            }
            if (object != null && this.bounced < bouncing && this.canBounce) {
                return;
            }
        }
        executeArea();
    }

    public void executeArea() {
        if (this.getOwner() != null) {
            areaList.execute(this.getOwner(), x, y, 1, item, toolItem);
        }
    }


    @Override
    public void checkHitCollision(Line2D hitLine) {
        this.customCheckCollisions(this.toHitbox(hitLine));
    }

    protected final void customCheckCollisions(Shape hitbox) {
        Mob ownerMob = this.getOwner();
        if (ownerMob != null && this.isBoomerang && this.returningToOwner && hitbox.intersects(ownerMob.getHitBox())) {
            this.remove();
        }

        if (this.isServer() && this.canBreakObjects) {
            ArrayList<LevelObjectHit> hits = this.getLevel().getCollisions(hitbox, this.getAttackThroughCollisionFilter());

            for (LevelObjectHit hit : hits) {
                if (!hit.invalidPos() && hit.getObject().attackThrough) {
                    this.attackThrough(hit);
                }
            }
        }

        if (this.canHitMobs) {
            List<Mob> targets = this.customStreamTargets(hitbox).filter((m) -> this.canHit(m) && hitbox.intersects(m.getHitBox())).filter((m) -> !this.isSolid || m.canHitThroughCollision() || !this.perpLineCollidesWithLevel(m.x, m.y)).collect(Collectors.toCollection(LinkedList::new));

            for (Mob target : targets) {
                this.onHit(target, null, this.x, this.y, false, null);
            }
        }

    }

    protected Stream<Mob> customStreamTargets(Shape hitBounds) {
        return Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(hitBounds, 1), GameUtils.streamNetworkClients(this.getLevel()).filter((c) -> !c.isDead() && c.hasSpawned()).map((sc) -> sc.playerMob));
    }

}
