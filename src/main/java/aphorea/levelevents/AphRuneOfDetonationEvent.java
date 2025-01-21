package aphorea.levelevents;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;

public class AphRuneOfDetonationEvent extends ExplosionEvent {
    public float effectNumber;

    public AphRuneOfDetonationEvent() {
        super(0, 0, 0, new GameDamage(0), false, 0);
    }

    public AphRuneOfDetonationEvent(PlayerMob owner, float x, float y, float effectNumber) {
        super(x, y, 300, new GameDamage(0), false, 0, owner);
        this.effectNumber = effectNumber;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.effectNumber);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.effectNumber = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();

        hitsOwner = false;
    }

    @Override
    protected void onMobWasHit(Mob mob, float distance) {
        float mod = this.getDistanceMod(distance);
        float damagePercent = effectNumber;
        if (mob.isBoss()) {
            damagePercent /= 50;
        } else if (mob.isPlayer || mob.isHuman) {
            damagePercent /= 5;
        }
        GameDamage damage = new GameDamage(mob.getMaxHealth() * damagePercent * mod, 1000000);
        float knockback = (float) this.knockback * mod;
        mob.isServerHit(damage, (float) mob.getX() - this.x, (float) mob.getY() - this.y, knockback, this);
    }

}