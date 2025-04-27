package aphorea.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.network.server.ServerClient;

public class ActivateBabylonTowerJournalChallenge extends SimpleJournalChallenge {

    public void onBabylonTowerActivated(ServerClient serverClient) {
        if (!this.isCompleted(serverClient) && this.isJournalEntryDiscovered(serverClient)) {
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
        }
    }

}
