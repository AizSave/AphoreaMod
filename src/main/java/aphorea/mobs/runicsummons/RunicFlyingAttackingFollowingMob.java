package aphorea.mobs.runicsummons;

import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;

public class RunicFlyingAttackingFollowingMob extends FlyingAttackingFollowingMob {
    public float effectNumber = 1F;

    public RunicFlyingAttackingFollowingMob(int health) {
        super(health);
    }

    public void updateEffectNumber(float effectNumber) {
        this.effectNumber = effectNumber;
    }
}
