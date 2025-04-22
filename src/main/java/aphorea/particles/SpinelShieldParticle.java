package aphorea.particles;

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

public class SpinelShieldParticle extends Particle {
    public static GameTexture texture;
    public float angle;
    public Mob owner;

    public SpinelShieldParticle(Level level, Mob owner, float angle) {
        super(level, owner.x, owner.y, 50);
        this.owner = owner;
        this.angle = angle;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int offsetX = (int) (Math.cos(angle) * 40);
        int offsetY = (int) (Math.sin(angle) * 40);

        int drawX = camera.getDrawX(owner.x) + offsetX;
        int drawY = camera.getDrawY(owner.y) + offsetY;

        final TextureDrawOptions drawOptions = texture.initDraw().alpha(0.5F).rotate((float) Math.toDegrees(angle)).posMiddle(drawX, drawY);

        list.add(new EntityDrawable(this) {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }
}
