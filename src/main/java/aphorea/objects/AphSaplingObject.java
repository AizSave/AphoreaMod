package aphorea.objects;

import necesse.level.gameObject.SaplingObject;

public class AphSaplingObject extends SaplingObject {
    public AphSaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient, String... validTiles) {
        super(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient, validTiles);
    }

    public AphSaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient, int lightLevel, float lightHue, float lightSat, String... validTiles) {
        super(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient, validTiles);
        this.lightLevel = lightLevel;
        this.lightHue = lightHue;
        this.lightSat = lightSat;
    }
}
