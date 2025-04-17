package aphorea.projectiles.bullet;

import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class ShotgunBulletProjectile extends BulletProjectile {
    public float armorPenPercent;
    public int spriteX;
    public static Color[] trailColors = new Color[]{
            AphColors.iron,
            AphColors.withAlpha(AphColors.red, 128)
    };

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(armorPenPercent);
        writer.putNextByte((byte) spriteX);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        armorPenPercent = reader.getNextFloat();
        spriteX = reader.getNextByte();
    }

    public ShotgunBulletProjectile() {
    }

    public ShotgunBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, float armorPenPercent, int knockback, Mob owner, int spriteX) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
        this.armorPenPercent = armorPenPercent;
        this.spriteX = spriteX;
    }

    public ShotgunBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, float armorPenPercent, int knockback, Mob owner) {
        this(x, y, targetX, targetY, speed, distance, damage, armorPenPercent, knockback, owner, 0);
    }

    public void init() {
        super.init();
        this.setWidth(4.0F);
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0F;
        this.piercing = spriteX == 1 ? 2 : 0;
    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), trailColors[spriteX], 22.0F, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return AphColors.iron;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (removed()) return;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(y);
        TextureDrawOptions options = texture.initDraw().sprite(spriteX, 0, 8, 14)
                .light(light)
                .rotate(getAngle(), texture.getWidth() / 2, 2)
                .pos(drawX, drawY - (int) getHeight());

        list.add(new EntityDrawable(this) {
            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void applyDamage(Mob mob, float x, float y) {
        mob.isServerHit(this.getDamage().setArmorPen(mob.getArmor() * armorPenPercent), mob.x - x * -this.dx * 50.0F, mob.y - y * -this.dy * 50.0F, (float) this.knockback, this);
    }

}
