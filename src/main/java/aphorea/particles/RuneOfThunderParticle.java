package aphorea.particles;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.util.List;

public class RuneOfThunderParticle extends Particle {
    private final long spawnTime;
    private final boolean mirror;

    public RuneOfThunderParticle(Level level, float x, float y, long spawnTime) {
        super(level, x, y, 2000);
        this.spawnTime = spawnTime;
        this.mirror = GameRandom.globalRandom.nextBoolean();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, 0.5F);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        long eventTime = this.getWorldEntity().getTime() - this.spawnTime;

        float sizeMod = 1.0F + (float) (Math.sin((double) eventTime / 80.0) / 10.0);
        float rotation = (float) ((double) eventTime / 4.0);

        TextureDrawOptions shadowOptions = MobRegistry.Textures.evilsProtectorBomb_shadow.initDraw().sprite(0, 0, 128, 192).mirror(this.mirror, false).rotate(rotation, (int) (64.0F * sizeMod), (int) (96.0F * sizeMod)).size((int) (128.0F * sizeMod), (int) (192.0F * sizeMod)).light(light).posMiddle(drawX, drawY);
        tileList.add((tm) -> shadowOptions.draw());

    }
}
