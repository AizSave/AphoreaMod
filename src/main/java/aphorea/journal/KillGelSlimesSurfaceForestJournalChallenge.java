package aphorea.journal;

import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.MobsKilledJournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class KillGelSlimesSurfaceForestJournalChallenge extends MobsKilledJournalChallenge {
    public KillGelSlimesSurfaceForestJournalChallenge() {
        super(25, "gelslime");
    }

    public void onMobKilled(ServerClient serverClient, Mob mob) {
        Level level = mob.getLevel();
        if (!level.isCave) {
            if (JournalChallengeUtils.isForestBiome(level.biome)) {
                super.onMobKilled(serverClient, mob);
            }
        }
    }
}
