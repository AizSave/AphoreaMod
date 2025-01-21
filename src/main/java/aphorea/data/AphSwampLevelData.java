package aphorea.data;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;

public class AphSwampLevelData extends LevelData {
    public static final String DATA_KEY = "aphoreaswampleveldata";
    public boolean witchesnulled;

    public AphSwampLevelData() {
    }

    public AphSwampLevelData getData(Level level) {
        LevelData customData = level.getLevelData(DATA_KEY);
        if (customData instanceof AphSwampLevelData) {
            return (AphSwampLevelData) customData;
        }
        AphSwampLevelData newData = new AphSwampLevelData();
        level.addLevelData(DATA_KEY, newData);
        return newData;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("pinkwitchesnulled", witchesnulled);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        witchesnulled = save.getBoolean("pinkwitchesnulled", witchesnulled);
    }

}
