package aphorea.mobs.bosses.minions;

import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class HearthCrystalMob extends HostileMob {

    protected int centerX;
    protected int centerY;
    protected float angleOffset;
    protected float radius;
    protected float constantTime;
    protected boolean clockwise;

    public HearthCrystalMob() {
        super(200);
        this.setArmor(10);
        this.setSpeed(0.0F);
        this.setFriction(1000.0F);
        this.setKnockbackModifier(0.0F);
        this.collision = new Rectangle(-8, -8, 16, 16);
        this.hitBox = new Rectangle(-16, -16 - 16, 32, 32 + 16);
        this.selectBox = new Rectangle(-18, -18, 34, 34);
        this.shouldSave = false;
    }

    public void setCircularMovement(int centerX, int centerY, float angleOffset, float radius, float constantTime, boolean clockwise) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angleOffset = angleOffset;
        this.radius = radius;
        this.constantTime = constantTime;
        this.clockwise = clockwise;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.centerX = reader.getNextInt();
        this.centerY = reader.getNextInt();
        this.angleOffset = reader.getNextFloat();
        this.radius = reader.getNextFloat();
        this.constantTime = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(centerX);
        writer.putNextInt(centerY);
        writer.putNextFloat(angleOffset);
        writer.putNextFloat(radius);
        writer.putNextFloat(constantTime);
        writer.putNextBoolean(clockwise);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (notBabylonTowerClose()) {
            this.remove();
        } else {
            long time = this.getTime();
            this.setPos(getXPosition(time), getYPosition(time), false);
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, AphColors.spinel, 1F, 50);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (notBabylonTowerClose()) {
            this.remove();
        } else {
            long time = this.getTime();
            this.setPos(getXPosition(time), getYPosition(time), false);
        }
    }

    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.crystalHit1, SoundEffect.effect(x, y).volume(1.5F));
    }

    public boolean canBePushed(Mob other) {
        return false;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 32;

        DrawOptions drawOptions = ItemRegistry.getItem("lifespinel")
                .getItemSprite(null, null).initDraw()
                .light(light)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!this.isWaterWalking()) addShadowDrawables(tileList, level, x, y, light, camera);

    }

    public boolean canTakeDamage() {
        return true;
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 8;
        int drawY = y - 8;
        BabylonTowerMob.icon.initDraw().sprite(0, 0, 32).size(16, 16).draw(drawX, drawY);
    }

    public boolean canPushMob(Mob other) {
        return false;
    }

    public boolean notBabylonTowerClose() {
        return getLevel().entityManager.mobs.stream().noneMatch(m -> Objects.equals(m.getStringID(), "babylontower") && m.getDistance(this) < BabylonTowerMob.BOSS_AREA_RADIUS);
    }

    @Override
    public void init() {
        super.init();

        SoundManager.playSound(GameResources.crystalHit1, SoundEffect.effect(x, y).volume(1.5F));

        long time = this.getTime();
        this.setPos(getXPosition(time), getYPosition(time), true);
    }

    public float getAngularSpeed() {
        return 0.000001F * (float) (2 * Math.PI * radius / constantTime) * (clockwise ? -1 : 1);
    }

    public float getCurrentAngle(long time) {
        return angleOffset + getAngularSpeed() * time;
    }

    public float getXPosition(long time) {
        return centerX + radius * (float) Math.cos(getCurrentAngle(time));
    }

    public float getYPosition(long time) {
        return centerY + radius * (float) Math.sin(getCurrentAngle(time));
    }


}
