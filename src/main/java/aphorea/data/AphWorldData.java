package aphorea.data;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldData.WorldData;

public class AphWorldData extends WorldData {
    public static final String DATA_KEY = "aphoreaworlddata";
    public boolean gelSlimesNulled;

    public AphWorldData() {
    }

    public AphWorldData getData(WorldEntity world) {
        WorldData customData = world.getWorldData(DATA_KEY);
        if (customData instanceof AphWorldData) {
            return (AphWorldData) customData;
        }
        AphWorldData newData = new AphWorldData();
        world.addWorldData(DATA_KEY, newData);
        return newData;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("gelslimesnulled", gelSlimesNulled);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        gelSlimesNulled = save.getBoolean("gelslimesnulled", gelSlimesNulled);
    }

}
