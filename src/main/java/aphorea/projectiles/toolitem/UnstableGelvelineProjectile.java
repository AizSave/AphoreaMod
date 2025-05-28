package aphorea.projectiles.toolitem;

import aphorea.items.tools.weapons.throwable.UnstableGelveline;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
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
import java.util.List;

public class UnstableGelvelineProjectile extends Projectile {
    UnstableGelveline toolItem;
    InventoryItem item;

    Color color = AphColors.unstableGel;

    AphAreaList areaList = new AphAreaList(
            new AphArea(100, color)
    );

    public UnstableGelvelineProjectile() {
    }

    public UnstableGelvelineProjectile(GameDamage attackDamage, int knockback, UnstableGelveline toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.knockback = knockback;

        this.setDamage(attackDamage);
        this.knockback = knockback;

        this.toolItem = toolItem;
        this.item = item;

        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;

        areaList = new AphAreaList(
                new AphArea(100, color).setDamageArea(attackDamage.modDamage(0.5F))
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

    @Override
    public Color getParticleColor() {
        return color;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), color, 26, 500, getHeight());
    }


    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            mob.addBuff(new ActiveBuff(AphBuffs.STICKY, mob, 2000, this), true);
        }
    }

    @Override
    public void remove() {
        areaList.execute(getOwner(), x, y, 1F, item, toolItem, false);
        super.remove();
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
