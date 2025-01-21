package aphorea.data;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class AphPlayerData {
    public final String playerName;
    public boolean runeSelected;

    public AphPlayerData(String playerName) {
        this.playerName = playerName;
    }

    public void loadData(LoadData loadData) {
        this.runeSelected = loadData.getBoolean("runeSelected", false);
    }

    public void saveData(SaveData saveData) {
        saveData.addBoolean("runeSelected", runeSelected);
    }
}
