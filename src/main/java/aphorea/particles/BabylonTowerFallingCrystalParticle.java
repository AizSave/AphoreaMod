package aphorea.particles;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.util.List;

public class BabylonTowerFallingCrystalParticle extends Particle {
    protected static final int[] frameTimes = new int[]{30, 30, 30, 30, 30, 60, 60, 60, 60, 60, 60, 120, 120, 120, 120};
    private final long spawnTime;
    private final long delay;
    private final boolean mirror;

    public static GameTexture projectileTexture;
    public static GameTexture shadowTexture;

    public BabylonTowerFallingCrystalParticle(Level level, float x, float y, long spawnTime, long delay) {
        super(level, x, y, delay + 1500L);
        this.spawnTime = spawnTime;
        this.delay = delay;
        this.mirror = GameRandom.globalRandom.nextBoolean();
    }

    public void clientTick() {
        super.clientTick();
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            int frame = GameUtils.getAnim(frameTime, frameTimes);
            if (frame == -1) {
                this.remove();
            } else if (frame < 10) {
                this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, 0.5F);
            }
        }

    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;
        int frame;
        if (eventTime >= this.delay) {
            long frameTime = eventTime - this.delay;
            frame = GameUtils.getAnim(frameTime, frameTimes);
            if (frame == -1) {
                return;
            }

            int bombDrawY = drawY;
            if (frame < 5) {
                bombDrawY -= 32 - frame * 7;
            }

            int spriteX = frame % 5;
            int spriteY = frame / 5;
            final TextureDrawOptions drawOptions = projectileTexture.initDraw().sprite(spriteX, spriteY, 128, 192).mirror(this.mirror, false).light(light).posMiddle(drawX, bombDrawY);
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        } else {
            frame = 0;
        }

        frame = Math.max(frame - 5, 0);
        if (frame < 5) {
            float rotation = 0.0F;
            float sizeMod = 1.0F;
            if (frame == 0) {
                rotation = (float)((double)eventTime / 4.0);
                sizeMod += (float)(Math.sin((double)eventTime / 80.0) / 10.0);
            }

            TextureDrawOptions shadowOptions = shadowTexture.initDraw().sprite(frame, 0, 128, 192).mirror(this.mirror, false).rotate(rotation, (int)(64.0F * sizeMod), (int)(96.0F * sizeMod)).size((int)(128.0F * sizeMod), (int)(192.0F * sizeMod)).light(light).posMiddle(drawX, drawY);
            tileList.add((tm) -> {
                shadowOptions.draw();
            });
        }
    }
}
