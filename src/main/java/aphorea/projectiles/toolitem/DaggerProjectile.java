package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

abstract public class DaggerProjectile extends Projectile {

    abstract Color getColor();

    abstract GameTexture getTexture();

    public DaggerProjectile() {
    }

    boolean shouldDrop;
    String stringItemID;
    GNDItemMap gndData;

    public DaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
        this.shouldDrop = shouldDrop;
        this.stringItemID = stringItemID;
        this.gndData = gndData;
    }

    public void init() {
        super.init();
        this.height = 14.0F;
        this.heightBasedOnDistance = false;
        this.setWidth(8.0F);
        this.canBounce = false;
    }

    @Override
    public Color getParticleColor() {
        return null;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), getColor(), 12, 100, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;

        GameTexture texture = getTexture();

        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - texture.getHeight() / 2;
        TextureDrawOptions options = texture.initDraw()
                .light(light)
                .rotate(getAngle() + 45, texture.getWidth() / 2, texture.getHeight() / 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && shouldDrop && stringItemID != null && gndData != null) {
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
            shouldDrop = false;
            InventoryItem inventoryItem = new InventoryItem(ItemRegistry.getItem(stringItemID));
            inventoryItem.setGndData(gndData);
            getLevel().entityManager.pickups.add(new ItemPickupEntity(getLevel(), inventoryItem, x, y, 0, 0));
        }
    }

    public static class CopperDaggerProjectile extends DaggerProjectile {

        public static GameTexture texture;

        GameTexture getTexture() {
            return texture;
        }

        Color getColor() {
            return AphColors.copper;
        }

        public CopperDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public CopperDaggerProjectile() {
        }
    }

    public static class IronDaggerProjectile extends DaggerProjectile {

        public static GameTexture texture;

        GameTexture getTexture() {
            return texture;
        }

        Color getColor() {
            return AphColors.iron;
        }

        public IronDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public IronDaggerProjectile() {
        }
    }

    public static class GoldDaggerProjectile extends DaggerProjectile {

        public static GameTexture texture;

        GameTexture getTexture() {
            return texture;
        }

        Color getColor() {
            return AphColors.gold;
        }

        public GoldDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public GoldDaggerProjectile() {
        }
    }

    public static class DemonicDaggerProjectile extends DaggerProjectile {

        public static GameTexture texture;

        GameTexture getTexture() {
            return texture;
        }

        Color getColor() {
            return AphColors.demonic;
        }

        public DemonicDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public DemonicDaggerProjectile() {
        }
    }

    public static class TungstenDaggerProjectile extends DaggerProjectile {

        public static GameTexture texture;

        GameTexture getTexture() {
            return texture;
        }

        Color getColor() {
            return AphColors.tungsten;
        }

        public TungstenDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public TungstenDaggerProjectile() {
        }
    }

    public static class LostUmbrellaDaggerProjectile extends DaggerProjectile {

        public static GameTexture texture;

        GameTexture getTexture() {
            return texture;
        }

        Color getColor() {
            return AphColors.pink_witch;
        }

        public LostUmbrellaDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public LostUmbrellaDaggerProjectile() {
        }

        @Override
        public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
            if (mob != null) {
                if (this.isServer()) {
                    Level level = getLevel();
                    float angle = (float) Math.toRadians(this.getAngle() - 90);
                    float newTargetX = (float) (x + 100 * Math.cos(angle));
                    float newTargetY = (float) (y + 100 * Math.sin(angle));
                    Projectile projectile = new OpenLostUmbrellaProjectile(level, getOwner(), x, y, newTargetX, newTargetY, speed / 2, distance / 4, getDamage(), knockback);
                    projectile.resetUniqueID(GameRandom.globalRandom);
                    if (mob instanceof ItemAttackerMob) {
                        ((ItemAttackerMob) mob).addAndSendAttackerProjectile(projectile, 0);
                    }

                    if (shouldDrop && stringItemID != null && gndData != null) {
                        if (this.amountHit() < this.piercing) {
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
                        shouldDrop = false;
                        InventoryItem inventoryItem = new InventoryItem(ItemRegistry.getItem(stringItemID));
                        inventoryItem.setGndData(gndData);
                        getLevel().entityManager.pickups.add(new ItemPickupEntity(getLevel(), inventoryItem, x, y, 0, 0));
                    }
                }
                this.remove();
            }
            super.doHitLogic(mob, object, x, y);
        }
    }
}
