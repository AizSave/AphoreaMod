package aphorea.projectiles.arrow;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
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

public class GelArrowProjectile extends Projectile {

    Color color = AphColors.gel;

    AphAreaList areaList = new AphAreaList(
            new AphArea(50, color)
    ).setDamageType(DamageTypeRegistry.RANGED);

    public GelArrowProjectile() {
    }

    public GelArrowProjectile(GameDamage damage, int knockback, float areaDamage, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.setDamage(damage);
        this.knockback = knockback;

        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;

        this.areaList = new AphAreaList(
                new AphArea(50, color).setDamageArea(areaDamage)
        );
    }

    @Override
    public void init() {
        super.init();
        piercing = 0;
        bouncing = 0;
        this.canHitMobs = true;

        this.givesLight = false;
        this.heightBasedOnDistance = true;
        this.setWidth(8);
    }

    @Override
    public void addHit(Mob target) {
        super.addHit(target);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, this), true);
    }

    public void dropItem() {
        if (GameRandom.globalRandom.getChance(0.5F)) {
            this.getLevel().entityManager.pickups.add((new InventoryItem("stonearrow")).getPickupEntity(this.getLevel(), this.x, this.y));
        }
    }

    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.slimesplash, SoundEffect.effect(x, y));
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

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        executeArea();
    }


    public void executeArea() {
        if (this.getOwner() != null) {
            areaList.execute(this.getOwner(), x, y);
        }
    }
}
