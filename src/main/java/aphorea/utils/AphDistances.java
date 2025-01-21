package aphorea.utils;

import necesse.entity.mobs.Mob;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AphDistances {
    static public Mob findClosestMob(Mob mob) {
        return findClosestMob(mob, m -> true);
    }

    static public Mob findClosestMob(Mob mob, int distance) {
        return findClosestMob(mob, m -> true, distance);
    }

    static public Mob findClosestMob(Mob mob, Predicate<Mob> filter) {
        return findClosestMob(mob, filter, 1024);
    }

    static public Mob findClosestMob(Mob mob, Predicate<Mob> filter, int distance) {
        ArrayList<Mob> mobs = new ArrayList<>();
        mob.getLevel().entityManager.streamAreaMobsAndPlayers(mob.x, mob.y, distance).filter(filter).forEach(mobs::add);
        mobs.sort((m1, m2) -> {
            float d1 = m1.getDistance(mob);
            float d2 = m2.getDistance(mob);
            return Float.compare(d1, d2);
        });
        return mobs.stream().findFirst().orElse(null);
    }
}
