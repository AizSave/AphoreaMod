package aphorea.projectiles.toolitem;

import aphorea.items.healingtools.AphHealingProjectileToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.magichealing.AphMagicHealing;
import aphorea.utils.AphDistances;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WoodenWandProjectile extends FollowingProjectile {

    Color color = AphColors.nature;
    AphHealingProjectileToolItem toolItem;
    InventoryItem item;
    int healing;

    public WoodenWandProjectile(int healing, AphHealingProjectileToolItem toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
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

    public WoodenWandProjectile() {
    }

    @Override
    public void init() {
        super.init();

        piercing = 0;
        bouncing = 0;
        this.doesImpactDamage = false;
        this.knockback = 0;
        this.canBreakObjects = false;
        this.canHitMobs = true;
        this.givesLight = true;

        this.turnSpeed = 0.05F;

        this.setWidth(0, 5);
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
            target = AphDistances.findClosestMob(this.getOwner(), this::canHit, this.distance / 2);
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
        if (removed()) return;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(y);
        TextureDrawOptions options = texture.initDraw()
                .light(light)
                .rotate(getAngle(), texture.getWidth() / 2, 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });

        addShadowDrawables(tileList, drawX, drawY, light, getAngle(), texture.getWidth() / 2, 2);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        if (isServer() && this.getOwner() != null && mob != null) {
            if (toolItem != null) {
                toolItem.healMob((PlayerMob) this.getOwner(), mob, item);
            } else if (healing != 0) {
                AphMagicHealing.healMob(this.getOwner(), mob, healing, item, toolItem);
            }
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

        Iterator var4;
        if (this.isServer() && this.canBreakObjects) {
            ArrayList<LevelObjectHit> hits = this.getLevel().getCollisions(hitbox, this.getAttackThroughCollisionFilter());
            var4 = hits.iterator();

            while (var4.hasNext()) {
                LevelObjectHit hit = (LevelObjectHit) var4.next();
                if (!hit.invalidPos() && hit.getObject().attackThrough) {
                    this.attackThrough(hit);
                }
            }
        }

        if (this.canHitMobs) {
            List<Mob> targets = (List) this.customStreamTargets(hitbox).filter((m) -> {
                return this.canHit(m) && hitbox.intersects(m.getHitBox());
            }).filter((m) -> {
                return !this.isSolid || m.canHitThroughCollision() || !this.perpLineCollidesWithLevel(m.x, m.y);
            }).collect(Collectors.toCollection(LinkedList::new));
            var4 = targets.iterator();

            while (var4.hasNext()) {
                Mob target = (Mob) var4.next();
                this.onHit(target, (LevelObjectHit) null, this.x, this.y, false, (ServerClient) null);
            }
        }

    }

    protected Stream<Mob> customStreamTargets(Shape hitBounds) {
        return Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(hitBounds, 1), GameUtils.streamNetworkClients(this.getLevel()).filter((c) -> {
            return !c.isDead() && c.hasSpawned();
        }).map((sc) -> {
            return sc.playerMob;
        }));
    }
}
