package aphorea.mobs.runicsummons;

import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;

public class RunicAttackingFollowingMob extends AttackingFollowingMob {
    public float effectNumber = 1F;

    public RunicAttackingFollowingMob(int health) {
        super(health);
    }

    public void updateEffectNumber(float effectNumber) {
        this.effectNumber = effectNumber;
    }
}
