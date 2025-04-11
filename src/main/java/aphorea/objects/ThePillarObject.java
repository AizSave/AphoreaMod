package aphorea.objects;

import aphorea.mobs.bosses.ThePillarMob;
import aphorea.utils.AphColors;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThePillarObject extends StaticMultiObject {
    protected int yOffset = -3;

    public ThePillarObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "thepillar");
        this.stackSize = 1;
        this.rarity = Item.Rarity.LEGENDARY;
        this.mapColor = AphColors.spinel;
        this.objectHealth = Integer.MAX_VALUE;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = false;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    public static void registerObject() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(0, 0, 96, 64);
        ids[0] = ObjectRegistry.registerObject("thepillar", new ThePillarObject(0, 0, 3, 2, ids, collision), 0.0F, false);
        ids[1] = ObjectRegistry.registerObject("thepillar2", new ThePillarObject(1, 0, 3, 2, ids, collision), 0.0F, false);
        ids[2] = ObjectRegistry.registerObject("thepillar3", new ThePillarObject(2, 0, 3, 2, ids, collision), 0.0F, false);
        ids[3] = ObjectRegistry.registerObject("thepillar4", new ThePillarObject(0, 1, 3, 2, ids, collision), 0.0F, false);
        ids[4] = ObjectRegistry.registerObject("thepillar5", new ThePillarObject(1, 1, 3, 2, ids, collision), 0.0F, false);
        ids[5] = ObjectRegistry.registerObject("thepillar6", new ThePillarObject(2, 1, 3, 2, ids, collision), 0.0F, false);
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        if (isActive(level, tileX, tileY)) {
            level.lightManager.refreshParticleLightFloat(tileX * 32 - 16, tileY * 32 - 16, AphColors.spinel, 1F, 200);
        }
    }

    public boolean isActive(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX - multiX, tileY - multiY);
        return object != null && object.getCurrentObjectEntity(level, tileX - multiX, tileY - multiY) instanceof ThePillarObjectEntity;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (multiY == 0) {
            float alpha = 1.0F;
            if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 32, tileY * 32 - 128, 96, 128);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 0.5F;
                } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                    alpha = 0.5F;
                }
            }

            GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
            final DrawOptions[] options = this.getMultiTextureDrawOptionsCustom(texture, level, tileX, tileY, camera, alpha);
            for (DrawOptions drawOptions : options) {
                list.add(new LevelSortedDrawable(this, tileX, tileY) {
                    public int getSortY() {
                        return 16;
                    }

                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        }
    }

    protected DrawOptions[] getMultiTextureDrawOptionsCustom(GameTexture texture, Level level, int tileX, int tileY, GameCamera camera, float alpha) {
        return this.getMultiTextureDrawOptionsCustom(new GameSprite(texture), level, tileX, tileY, camera, alpha);
    }

    protected DrawOptions[] getMultiTextureDrawOptionsCustom(GameSprite sprite, Level level, int tileX, int tileY, GameCamera camera, float alpha) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int startX = this.multiX * 32;
        ArrayList<DrawOptions> drawOptions = new ArrayList<>();

        int parts = getParts(level, tileX, tileY);
        int startHeight = drawY - 80 - 20 * parts + 64;
        drawOptions.add(sprite.initDrawSection(startX, startX + 32, 0, 40, false).alpha(alpha).size(32, 40).light(light).pos(drawX, startHeight));
        if (isActive(level, tileX, tileY)) {
            drawOptions.add(sprite.initDrawSection(startX, startX + 32, 100, 140, false).alpha(alpha).size(32, 40).light(light).pos(drawX, startHeight + 40));
        } else {
            drawOptions.add(sprite.initDrawSection(startX, startX + 32, 40, 80, false).alpha(alpha).size(32, 40).light(light).pos(drawX, startHeight + 40));
        }
        for (int i = 0; i < parts; i++) {
            drawOptions.add(sprite.initDrawSection(startX, startX + 32, 160, 180, false).alpha(alpha).size(32, 20).light(light).pos(drawX, startHeight + 80 + 20 * i));
        }
        return drawOptions.toArray(new DrawOptions[0]);
    }

    public int getParts(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX - multiX, tileY - multiY);
        if(objectEntity instanceof ThePillarObjectEntity) {
            ThePillarObjectEntity pillarObjectEntity = (ThePillarObjectEntity) objectEntity;
            return pillarObjectEntity.getMob() == null ? 4 : pillarObjectEntity.getMob().getParts();
        }
        return 4;
    }


    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0F);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        int spriteWidth;
        spriteWidth = (texture.getWidth() - 32) / 2;
        texture.initDraw().alpha(alpha).draw(drawX - spriteWidth, drawY + this.yOffset);

    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return !isActive(level, x, y);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        if (isActive(level, x, y)) {
            return null;
        }
        return Localization.translate("controls", "activatetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        Item item = ItemRegistry.getItem("lifespinel");
        if (!player.isItemOnCooldown(item)) {
            if (player.getInv().removeItems(item, 1, false, false, false, false, "use") > 0) {
                level.entityManager.objectEntities.add(new ThePillarObjectEntity(level, x - multiX, y - multiY));
            }
        }
    }

    @Override
    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return isActive(level, x, y) ? Collections.emptyList() : super.getProjectileCollisions(level, x, y, rotation);
    }

    public static class ThePillarObjectEntity extends ObjectEntity {
        private int bossID = -1;

        public ThePillarObjectEntity(Level level, int x, int y) {
            super(level, "thepillar", x, y);
        }

        public float getMobX() {
            return this.getX() * 32 + 48;
        }

        public float getMobY() {
            return this.getY() * 32 + 32;
        }

        public void setupContentPacket(PacketWriter writer) {
            if (this.bossID == -1) {
                this.generateMobID();
            }

            writer.putNextInt(this.bossID);
        }

        public void applyContentPacket(PacketReader reader) {
            this.bossID = reader.getNextInt();
        }

        public void clientTick() {
            super.clientTick();

            ThePillarMob m = this.getMob();
            if (m != null) {
                m.keepAlive(this);
            }

            checkLeave(false);
        }

        public void serverTick() {
            super.serverTick();
            ThePillarMob m = this.getMob();
            if (m == null) {
                m = this.generateMobID();
                this.markDirty();
            }

            m.keepAlive(this);

            checkLeave(true);
        }

        public void checkLeave(boolean heal) {
            if (getMob() == null || getMob().removed()) {
                return;
            }

            boolean noPlayersNearby = this.getLevel().entityManager.players
                    .streamArea(getMobX(), getMobY(), ThePillarMob.BOSS_AREA_RADIUS)
                    .noneMatch(p -> p.getDistance(getMobX(), getMobY()) < ThePillarMob.BOSS_AREA_RADIUS);

            if (noPlayersNearby) {
                if (getMob().getHealthPercent() == 1) {
                    getMob().remove();
                    remove();
                } else if (heal) {
                    getMob().setHealth((int) (getMob().getHealth() + getMob().getMaxHealth() * 0.004F));
                }
            }
        }

        private ThePillarMob generateMobID() {
            ThePillarMob lastMob = this.getMob();
            if (lastMob != null) {
                lastMob.remove();
            }

            ThePillarMob m = new ThePillarMob();
            this.getLevel().entityManager.addMob(m, getMobX(), getMobY());
            this.bossID = m.getUniqueID();
            return m;
        }

        private ThePillarMob getMob() {
            if (this.bossID == -1) {
                return null;
            } else {
                Mob m = this.getLevel().entityManager.mobs.get(this.bossID, false);
                return m != null ? (ThePillarMob) m : null;
            }
        }

        public void remove() {
            super.remove();
            ThePillarMob m = this.getMob();
            if (m != null) {
                m.remove();
            }

        }
    }

}
