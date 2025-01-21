package aphorea.projectiles.toolitem;

import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;

import java.awt.*;

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
    public void addHit(Mob target) {
        super.addHit(target);

        ActiveBuff buff2 = new ActiveBuff(BuffRegistry.Debuffs.FREEZING, target, 10000, this);
        target.addBuff(buff2, true);
    }
}
