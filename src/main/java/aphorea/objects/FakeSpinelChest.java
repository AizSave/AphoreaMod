package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FakeSpinelChest extends GameObject {

    ObjectDamagedTextureArray texture;

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay(this, "objects/spinelchest");
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
        ObjectDamagedTextureArray usedTexture = this.texture;

        GameTexture texture = usedTexture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
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
        GameTexture texture = this.texture.getDamagedTexture(0.0F);
        rotation %= texture.getWidth() / 32;
        texture.initDraw().sprite(rotation, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
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
        turnIntoMimic(level, x, y);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        turnIntoMimic(level, x, y);
    }

    @Override
    public void doExplosionDamage(Level level, int layerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        if (!level.settlementLayer.isActive()) {
            super.doExplosionDamage(level, layerID, tileX, tileY, damage, toolTier, attacker, client);
        }
    }

    public void turnIntoMimic(Level level, int tileX, int tileY) {
        if (level.isServer()) {
            Mob mob = MobRegistry.getMob("spinelmimic", level);
            mob.setDir(level.objectLayer.getObjectRotation(tileX, tileY));
            level.entityManager.addMob(mob, tileX * 32 + 16, tileY * 32 + 16);
        }
        if (level.isClient()) {
            level.entityManager.addParticle(new SmokePuffParticle(level, tileX * 32 + 16, tileY * 32 + 32, AphColors.spinel), Particle.GType.CRITICAL);
        }
        level.objectLayer.setObject(tileX, tileY, 0);
    }
}
