package aphorea.projectiles.toolitem;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class UnstableGelvelineProjectile extends Projectile {

    Color color = AphColors.unstableGel;
    ToolItem toolItem;
    InventoryItem item;

    AphAreaList areaList = new AphAreaList(
            new AphArea(100, color)
    );

    public UnstableGelvelineProjectile() {
    }

    public UnstableGelvelineProjectile(FloatUpgradeValue attackDamage, int knockback, ToolItem toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.knockback = knockback;

        this.setDamage(toolItem.getAttackDamage(item));
        this.knockback = knockback;

        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;

        areaList = new AphAreaList(
                new AphArea(100, color).setDamageArea((float) toolItem.getAttackDamageValue(item, owner) * 0.5F)
        );
    }

    @Override
    public void init() {
        super.init();

        piercing = 0;
        bouncing = 0;
        this.canHitMobs = true;

        this.givesLight = false;
        this.setWidth(0, 20);
    }

    public Color getParticleColor() {
        return color;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), color, 26, 500, getHeight());
    }


    @Override
    public void addHit(Mob target) {
        super.addHit(target);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 2000, this), true);
    }

    @Override
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
            if(isServer()) {
                areaList.executeAreas(this.getOwner(), 1, (int) x, (int) y, false, item, toolItem);
            }
            if(isClient()) {
                areaList.showAllAreaParticles(this.getLevel(), x, y);
            }
        }
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
}
