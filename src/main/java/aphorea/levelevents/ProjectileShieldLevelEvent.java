package aphorea.levelevents;

import aphorea.buffs.Trinkets.SpinelShieldBuff;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

abstract public class ProjectileShieldLevelEvent extends ProjectileHitboxEffectEvent {

    public float angle;

    public ProjectileShieldLevelEvent() {
    }

    public ProjectileShieldLevelEvent(Mob owner, float angle, GameRandom uniqueIDRandom) {
        super(owner, uniqueIDRandom);
        this.hitsObjects = false;

        this.angle = angle;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(angle);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.angle = reader.getNextFloat();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        angle = getUpdatedAngle(owner, angle);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        angle = getUpdatedAngle(owner, angle);
    }

    abstract public float getMaxDelta();

    public float getUpdatedAngle(Mob mob, float lastAngle) {
        float targetAngle = SpinelShieldBuff.getInitialAngle(mob);

        float delta = normalizeAngle(targetAngle - lastAngle);

        if (delta > getMaxDelta()) delta = getMaxDelta();
        if (delta < -getMaxDelta()) delta = -getMaxDelta();

        return normalizeAngle(lastAngle + delta);
    }

    private static float normalizeAngle(float angle) {
        while (angle <= -Math.PI) angle += (float) (2 * Math.PI);
        while (angle > Math.PI) angle -= (float) (2 * Math.PI);
        return angle;
    }

    public LineHitbox getShieldHitBox(float range, float width, float frontOffset, float rangeOffset) {
        float dirX = (float) Math.cos(angle);
        float dirY = (float) Math.sin(angle);

        Point2D.Float dir = GameMath.getPerpendicularDir(dirX, dirY);

        return new LineHitbox(this.owner.x + dir.x * rangeOffset + dirX * frontOffset, this.owner.y + dir.y * rangeOffset + dirY * frontOffset, dir.x, dir.y, width, range);

    }
}
