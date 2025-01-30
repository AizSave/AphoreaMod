package aphorea.methodpatches;

import necesse.engine.Settings;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameMath;
import necesse.level.maps.light.LightManager;
import net.bytebuddy.asm.Advice;

import java.util.HashMap;
import java.util.Map;

@ModMethodPatch(target = LightManager.class, name = "updateAmbientLight", arguments = {})
public class LevelAmbientLight {
    public static final Map<String, Float> modLightLevel = new HashMap<>();

    static {
        modLightLevel.put("infectedfieldssurface", 0.3F);
    }

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This LightManager lightManager) {
        float lightLevelMod = modLightLevel.getOrDefault(lightManager.level.getStringID(), 1F);
        if (lightManager.ambientLightOverride != null) {
            lightManager.ambientLight = lightManager.ambientLightOverride;
        } else if (Settings.alwaysLight) {
            lightManager.ambientLight = lightManager.newLight(150.0F * lightLevelMod);
        } else {
            if (lightManager.level.isCave) {
                lightManager.ambientLight = lightManager.newLight(0.0F, 0.0F, 0.0F);
            } else {
                float ambientLight = lightManager.level.getWorldEntity().getAmbientLight() * lightLevelMod;
                float minLightMod = 1.0F;
                if (Settings.brightness > 0.7F) {
                    minLightMod *= GameMath.lerp((float) Math.pow(Settings.brightness - 0.7F, 0.30000001192092896), 1.0F, 0.4F);
                }

                float minLight = 150.0F / (10.0F * minLightMod);
                if (ambientLight < minLight) {
                    ambientLight = minLight;
                }

                float ambientFloat = Math.abs(ambientLight / 150.0F - 1.0F) * lightLevelMod;
                ambientFloat = (float) Math.pow(ambientFloat, 2.0);
                lightManager.ambientLight = lightManager.newLight(240.0F, ambientFloat * 0.85F, ambientLight);
            }

        }
        return true;
    }
}
