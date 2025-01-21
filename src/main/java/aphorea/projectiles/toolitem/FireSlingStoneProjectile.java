package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.util.List;

public class FireSlingStoneProjectile extends SlingStoneProjectile {

    int count = 0;

    public static GameTexture texture_2;

    public FireSlingStoneProjectile() {
    }

    public void init() {
        super.init();
        givesLight = true;
        height = 28;
        setWidth(20, true);
    }


    public FireSlingStoneProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.fire, 26, 100, getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;
        GameTexture actualTexture = count >= 10 ? texture : texture_2;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - actualTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y);

        count++;
        if (count >= 20) {
            count = 0;
        }
        TextureDrawOptions options = actualTexture.initDraw()
                .light(light)
                .rotate(getAngle(), actualTexture.getWidth() / 2, 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void addHit(Mob target) {
        super.addHit(target);

        target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE, target, 10000, this), true);
    }

}
