package aphorea;

import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNextListData;
import necesse.engine.modLoader.annotations.ModEntry;

public class AphDependencies {
    public static String APHOREA_MOD_ID = "aphoreateam.aphoreamod";
    public static String MIGHTY_BANNER_MOD_ID = "daria40k.mightybannermod";

    public static boolean checkOptionalDependency(String modId) {
        for (ModNextListData mod : ModLoader.getAllModsSortedByCurrentList()) {
            if (mod.mod.id.equals(APHOREA_MOD_ID)) {
                return false;
            }
            if (mod.mod.id.equals(modId)) {
                return mod.enabled;
            }
        }
        return false;
    }

    public static boolean checkMightyBanner() {
        return checkOptionalDependency(MIGHTY_BANNER_MOD_ID);
    }
}
