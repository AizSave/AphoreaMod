package aphorea.particles;

import aphorea.levelevents.AphNarcissistEvent;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Mob;
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

public class NarcissistParticle extends Particle {
    public static GameTexture texture;
    public float startX;
    public float startY;
    public float startAngle;
    public Mob owner;

    public NarcissistParticle(Level level, Mob owner, float startX, float startY, float startAngle) {
        super(level, startX, startY, 2000);
        this.owner = owner;
        this.startX = startX;
        this.startY = startY;
        this.startAngle = startAngle;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getDrawX(getPosX());
        int drawY = camera.getDrawY(getPosY());
        float angleDeg = (float) Math.toDegrees(getStartAngle());

        final TextureDrawOptions drawOptions = texture.initDraw()
                .alpha(getAlpha())
                .rotate(angleDeg + 135)
                .posMiddle(drawX, drawY);

        list.add(new EntityDrawable(this) {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    public float getAlpha() {
        return 1 - AphNarcissistEvent.easeOutCirc(getLifeCyclePercent()) * 0.25F;
    }

    public float getPosX() {
        return AphNarcissistEvent.getX(startX, startAngle, getLifeCyclePercent());
    }

    public float getPosY() {
        return AphNarcissistEvent.getY(startY, startAngle, getLifeCyclePercent());
    }

    public float getStartAngle() {
        return AphNarcissistEvent.getAngle(startAngle, getLifeCyclePercent());
    }

}
