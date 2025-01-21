package aphorea.journal;

import necesse.engine.journal.MobsKilledJournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class KillUnstableGelSlimeJournalChallenge extends MobsKilledJournalChallenge {
    public KillUnstableGelSlimeJournalChallenge() {
        super(1, "unstablegelslime");
    }

    public void onMobKilled(ServerClient serverClient, Mob mob) {
        super.onMobKilled(serverClient, mob);
    }
}
