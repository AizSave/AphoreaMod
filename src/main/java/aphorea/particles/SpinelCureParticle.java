package aphorea.particles;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

import java.util.List;

public class SpinelCureParticle extends Particle {
    public static GameTexture texture;

    public SpinelCureParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y + 160, lifeTime);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y) - 160 - 32;

        final TextureDrawOptions drawOptions = texture.initDraw()
                .size(getSize(), getSize())
                .alpha(getAlpha())
                .posMiddle(drawX, drawY);

        list.add(new EntityDrawable(this) {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    public float getAlpha() {
        return 1 - getLifeCyclePercent();
    }

    public int getSize() {
        return (int) (32 + getLifeCyclePercent() * 2048);
    }

}
