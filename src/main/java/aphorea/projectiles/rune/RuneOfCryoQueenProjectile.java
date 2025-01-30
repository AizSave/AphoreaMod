package aphorea.projectiles.rune;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillarList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.CryoQueenMob;
import necesse.entity.projectile.pathProjectile.PositionedCirclingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

import java.awt.geom.Point2D;
import java.util.List;

public class RuneOfCryoQueenProjectile extends PositionedCirclingProjectile {
    private double distCounter;
    private double distBuffer;
    private final GroundPillarList<CryoQueenMob.CryoPillar> pillars = new GroundPillarList<>();
    protected float radius;
    protected boolean clockwise;

    public RuneOfCryoQueenProjectile() {
    }

    public RuneOfCryoQueenProjectile(float centerX, float centerY, float startRadius, float startAngle, boolean clockwise, float speed, int distance, int knockback, Mob owner) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = startRadius;
        this.currentAngle = startAngle;
        this.clockwise = clockwise;
        this.speed = speed;
        this.setDistance(distance);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    public void init() {
        super.init();
        this.maxMovePerTick = 12;
        this.height = 0.0F;
        this.piercing = 1000;
        this.setWidth(24.0F);
        if (this.isClient()) {
            this.getLevel().entityManager.addPillarHandler(new GroundPillarHandler<CryoQueenMob.CryoPillar>(this.pillars) {
                protected boolean canRemove() {
                    return RuneOfCryoQueenProjectile.this.removed();
                }

                public double getCurrentDistanceMoved() {
                    return RuneOfCryoQueenProjectile.this.distCounter;
                }
            });
        }

    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.radius);
        writer.putNextBoolean(this.clockwise);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.radius = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();
    }

    public float getRadius() {
        return this.radius;
    }

    public boolean rotatesClockwise() {
        return this.clockwise;
    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.radius = (float) ((double) this.radius + movedDist * 1.2);
        this.distCounter += movedDist;
        this.distBuffer += movedDist;

        while (this.distBuffer > 8.0) {
            this.distBuffer -= 8.0;
            synchronized (this.pillars) {
                this.pillars.add(new CryoQueenMob.CryoPillar((int) (this.x + GameRandom.globalRandom.floatGaussian() * 6.0F), (int) (this.y + GameRandom.globalRandom.floatGaussian() * 4.0F), this.distCounter, this.getWorldEntity().getLocalTime()));
            }
        }

    }

    public Trail getTrail() {
        return null;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        if (mob != null && !mob.isBoss() && !mob.isPlayer) {
            float damagePercent = 2;
            if (mob.isHuman) {
                damagePercent /= 5;
            }
            this.setDamage(new GameDamage(DamageTypeRegistry.TRUE, mob.getMaxHealth() * damagePercent));
        }
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
    }
}
