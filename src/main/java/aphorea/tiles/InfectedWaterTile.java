package aphorea.tiles;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LavaTile;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfectedWaterTile extends LiquidTile {
    public GameTextureSection deepTexture;
    public GameTextureSection shallowTexture;
    protected final GameRandom drawRandom = new GameRandom();
    private static final Map<Integer, Long> lastHit = new HashMap<>();
    private static final Map<Integer, Integer> consecutiveHits = new HashMap<>();

    public InfectedWaterTile() {
        super(AphColors.infected_light, "infected_freshwater_shallow", "infected_freshwater_deep", "infected_saltwater_shallow", "infected_saltwater_deep");
        this.lightLevel = 150;
        this.lightHue = 0.0F;
        this.lightSat = 0.6F;
    }

    protected void loadTextures() {
        super.loadTextures();
        this.deepTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/waterdeep"));
        this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/watershallow"));
    }

    public void tick(Mob mob, Level level, int x, int y) {
        if (!mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY())) {
            mob.buffManager.removeBuff("onfire", false);

            if (level.isServer() && mob.isPlayer && (level.isCave || !(mob.getMount() != null && mob.getMount().getStringID().contains("boat")))) {
                float damageMultiplier = 0F;
                long lastHitTime = lastHit.getOrDefault(mob.getID(), 0L);
                long currentTime = level.getTime();
                if (lastHitTime + (level.isCave ? 200 : 3_000) < currentTime) {
                    int consecutiveHitsCount = consecutiveHits.getOrDefault(mob.getID(), 0);
                    if (lastHitTime + (level.isCave ? 300 : 4_000) > currentTime) {
                        consecutiveHitsCount++;
                    } else {
                        consecutiveHitsCount = 0;
                    }
                    damageMultiplier = consecutiveHitsCount;
                    lastHit.put(mob.getID(), currentTime);
                    consecutiveHits.put(mob.getID(), consecutiveHitsCount);
                }

                if (damageMultiplier != 0) {
                    float damage = (level.isCave ? 10F : 5F) * damageMultiplier;
                    mob.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage), 0.0F, 0.0F, 0.0F, null);
                }
            }
        }
    }

    public void tickValid(Level level, int x, int y, boolean underGeneration) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i != 0 || j != 0) {
                    GameTile t = level.getTile(x + i, y + j);
                    if (t.isLiquid && (t instanceof LavaTile)) {
                        if (!underGeneration && level.isClient()) {
                            for (int k = 0; k < 10; ++k) {
                                BombProjectile.spawnFuseParticle(level, (float) (x * 32 + GameRandom.globalRandom.nextInt(33)), (float) (y * 32 + GameRandom.globalRandom.nextInt(33)), 1.0F);
                            }

                            level.lightManager.refreshParticleLight(x, y, 0.0F, 0.3F);
                            SoundManager.playSound(GameResources.fizz, SoundEffect.effect((float) (x * 32 + 16), (float) (y * 32 + 16)).volume(0.5F));
                        }

                        level.setTile(x, y, TileRegistry.getTileID("rocktile"));
                    }
                }
            }
        }

    }

    public TextureIndexes getTextureIndexes(Level level) {
        return new LiquidTile.TextureIndexes(0, 1, 2, 3);
    }

    public Color getLiquidColor(Level level, int x, int y) {
        return AphColors.infected_light;
    }

    public Color getNewSplattingLiquidColor(Level level, int x, int y) {
        return AphColors.infected;
    }

    public Color getMapColor(Level level, int tileX, int tileY) {
        return this.getLiquidColor(level, tileX, tileY);
    }

    protected void addLiquidTopDrawables(LevelTileTerrainDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        boolean addBobbing;
        synchronized (this.drawRandom) {
            addBobbing = this.drawRandom.seeded(getTileSeed(tileX, tileY)).getChance(0.15F);
        }

        if (addBobbing) {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int offset = this.getLiquidBobbing(level, tileX, tileY);
            int xOffset;
            int yOffset;
            GameTextureSection bobbingTexture;
            if (level.liquidManager.getHeight(tileX, tileY) <= -10) {
                xOffset = 0;
                yOffset = offset;
                bobbingTexture = this.deepTexture;
            } else {
                xOffset = offset;
                yOffset = 0;
                bobbingTexture = this.shallowTexture;
            }

            int tile;
            synchronized (this.drawRandom) {
                tile = this.drawRandom.seeded(getTileSeed(tileX, tileY)).nextInt(bobbingTexture.getHeight() / 32);
            }

            list.add(bobbingTexture.sprite(0, tile, 32)).color(this.getLiquidColor(level, tileX, tileY).brighter()).pos(drawX + xOffset, drawY + yOffset - 2);
        }

    }
}