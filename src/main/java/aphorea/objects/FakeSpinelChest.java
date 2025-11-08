package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FakeSpinelChest extends GameObject {

    public GameTexture texture;

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/spinelchest");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 3, y * 32 + 6, 26, 20) : new Rectangle(x * 32 + 6, y * 32 + 4, 20, 24);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);

        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture).addObjectDamageOverlay(this, level, tileX, tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % (texture.getWidth() / 32);
        boolean treasureHunter = perspective != null && perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
        draws.addSprite(rotation, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);

        rotation %= texture.getWidth() / 32;
        texture.initDraw().sprite(rotation, 0, 32, texture.getHeight()).addObjectDamageOverlay(this, level, tileX, tileY).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        turnIntoMimic(level, 0, x, y);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        turnIntoMimic(level, layerID, x, y);
    }

    public void turnIntoMimic(Level level, int layerID, int tileX, int tileY) {
        if (level.isServer()) {
            Mob mob = MobRegistry.getMob("spinelmimic", level);
            mob.setDir(level.objectLayer.getObjectRotation(layerID, tileX, tileY));
            level.entityManager.addMob(mob, tileX * 32 + 16, tileY * 32 + 16);
        }
        level.objectLayer.setObject(layerID, tileX, tileY, 0);
    }

    @Override
    public Color getMapColor(Level level, int tileX, int tileY) {
        return AphColors.spinel;
    }

    @Override
    public GameTooltips getMapTooltips(Level level, int x, int y) {
        return ObjectRegistry.getObject("spinelchest").getMapTooltips(level, x, y);
    }
}
