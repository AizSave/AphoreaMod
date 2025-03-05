package aphorea.objects;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

import java.awt.*;
import java.util.List;

public class SpinelClusterRObject extends GameObject {
    protected int counterID;
    private final String textureName;
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public SpinelClusterRObject(String textureName, Color mapColor, float glowHue) {
        super(new Rectangle(0, 14, 18, 10));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.drawRandom = new GameRandom();
        this.isLightTransparent = true;
        this.canPlaceOnLiquid = false;
        this.lightLevel = 150;
        this.lightSat = 0.3F;
        this.lightHue = glowHue;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay(this, "objects/" + this.textureName);
    }

    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(1, 0, 2, 1, false, new int[]{this.counterID, this.getID()});
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int sprite;
        synchronized (this.drawRandom) {
            sprite = this.drawRandom.seeded(getTileSeed(tileX - 1, tileY)).nextInt(texture.getWidth() / 64);
        }

        final TextureDrawOptions options = texture.initDraw().sprite(sprite * 2 + 1, 0, 32, texture.getHeight()).light(light.minLevelCopy(150.0F)).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0F);
        int sprite;
        synchronized (this.drawRandom) {
            sprite = this.drawRandom.seeded(getTileSeed(tileX - 1, tileY)).nextInt(texture.getWidth() / 64);
        }

        texture.initDraw().sprite(sprite * 2 + 1, 0, 32, texture.getHeight()).light(light.minLevelCopy(150.0F)).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.crystalHit1, SoundEffect.effect((float) (x * 32 + 16), (float) (y * 32 + 16)).volume(2.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
    }
}
