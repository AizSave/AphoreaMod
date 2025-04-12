package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class FrozenSlingStoneProjectile extends SlingStoneProjectile {

    public FrozenSlingStoneProjectile() {
    }

    public FrozenSlingStoneProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, getLevel(), AphColors.ice, 26, 100, getHeight());
    }


    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FREEZING, mob, 10000, this), true);
            }
        }
    }

}
