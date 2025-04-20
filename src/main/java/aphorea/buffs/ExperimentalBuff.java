package aphorea.buffs;

import aphorea.levelevents.AphExperimentalLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ExperimentalBuff extends Buff {
    float angle = 0;
    int ticks = 0;

    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
        angle = getInitialAngle(activeBuff.owner);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        angle = getUpdatedAngle(buff.owner, angle);

        ticks++;
        if(ticks >= 10) {
            ticks = 0;
            buff.owner.getLevel().entityManager.addLevelEvent(new AphExperimentalLevelEvent(buff.owner, angle));
        }
    }

    static public float maxDelta = (float)Math.toRadians(10);

    public static float getInitialAngle(Mob mob) {
        if (mob.moveX == 0 && mob.moveY == 0) {
            switch (mob.getDir()) {
                case 0: return (float)(-Math.PI / 2);
                case 1: return 0f;
                case 2: return (float)(Math.PI / 2);
                case 3: return (float)Math.PI;
                default: return 0f;
            }
        } else {
            return (float)Math.atan2(mob.moveY, mob.moveX);
        }
    }

    public static float getUpdatedAngle(Mob mob, float lastAngle) {
        float targetAngle = getInitialAngle(mob);

        float delta = normalizeAngle(targetAngle - lastAngle);

        if (delta > maxDelta) delta = maxDelta;
        if (delta < -maxDelta) delta = -maxDelta;

        return normalizeAngle(lastAngle + delta);
    }

    private static float normalizeAngle(float angle) {
        while (angle <= -Math.PI) angle += (float) (2 * Math.PI);
        while (angle > Math.PI) angle -= (float) (2 * Math.PI);
        return angle;
    }

}
