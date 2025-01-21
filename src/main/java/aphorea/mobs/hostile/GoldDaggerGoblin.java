package aphorea.mobs.hostile;

import aphorea.mobs.hostile.classes.DaggerGoblin;
import necesse.entity.mobs.Attacker;

import java.util.HashSet;

public class GoldDaggerGoblin extends DaggerGoblin {

    public GoldDaggerGoblin() {
        super("golddagger");
    }


    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
    }
}