package aphorea.levelevents;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.regionSystem.RegionPosition;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class AphRuneOfPestWardenEvent extends HitboxEffectEvent implements Attacker {
    private HashMap<Integer, Integer> hits = new HashMap<>();
    int count;

    public AphRuneOfPestWardenEvent() {
    }

    public AphRuneOfPestWardenEvent(Mob owner) {
        super(owner, new GameRandom());
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashMap<>();

        count = 0;
    }

    public void clientTick() {
        super.clientTick();
        count++;
        if (!owner.buffManager.hasBuff("runeofpestwardenactive")) {
            this.over();
        }
    }

    public void serverTick() {
        super.serverTick();
        count++;
        if (!owner.buffManager.hasBuff("runeofpestwardenactive")) {
            this.over();
        }
    }

    public Shape getHitBox() {
        int size = 50;
        return new Rectangle((int) owner.x - size / 2, (int) owner.y - size / 2, size, size);
    }

    public void clientHit(Mob target) {
        this.hits.put(target.getUniqueID(), count);
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || notInCooldown(target)) {
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0F) {
                float damagePercent = 0.2F;
                if (target.isBoss()) {
                    damagePercent /= 50;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5;
                }
                GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, target.getMaxHealth() * damagePercent);
                float knockback = 20.0F / modifier;
                target.isServerHit(damage, target.x - (int) owner.x, target.y - (int) owner.y, knockback, this.owner);
            }

            this.hits.put(target.getUniqueID(), count);
        }
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && notInCooldown(mob);
    }

    public boolean notInCooldown(Mob mob) {
        int lastHit = this.hits.getOrDefault(mob.getUniqueID(), -1);
        return lastHit == -1 || lastHit + 10 <= count;
    }

    public void hitObject(LevelObjectHit hit) {
    }

    public Set<RegionPosition> getRegionPositions() {
        return Collections.singleton(this.getLevel().regionManager.getRegionPosByTile((int) owner.x / 32, (int) owner.y / 32));
    }

}