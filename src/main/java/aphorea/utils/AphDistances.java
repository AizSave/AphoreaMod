package aphorea.utils;

import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AphDistances {
    static public Mob findClosestMob(@NotNull Mob mob, int distance) {
        return findClosestMob(mob.getLevel(), mob.x, mob.y, distance);
    }

    static public Mob findClosestMob(@NotNull Mob mob, int distance, Predicate<Mob> filter) {
        return findClosestMob(mob.getLevel(), mob.x, mob.y, distance, filter);
    }

    static public Mob findClosestMob(Level level, float x, float y, int distance) {
        return findClosestMob(level, x, y, distance, m -> true);
    }

    static public Mob findClosestMob(@NotNull Level level, float x, float y, int distance, Predicate<Mob> filter) {
        ArrayList<Mob> mobs = new ArrayList<>();
        level.entityManager.streamAreaMobsAndPlayers(x, y, distance).filter(filter).forEach(mobs::add);
        mobs.sort((m1, m2) -> {
            float d1 = m1.getDistance(x, y);
            float d2 = m2.getDistance(x, y);
            return Float.compare(d1, d2);
        });
        return mobs.stream().findFirst().orElse(null);
    }

}
