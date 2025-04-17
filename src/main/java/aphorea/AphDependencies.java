package aphorea;

import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNextListData;

public class AphDependencies {
    public static String APHOREA_MOD_ID = "aphoreateam.aphoreamod";
    public static String MIGHTY_BANNER_MOD_ID = "daria40k.mightybannermod";
    public static String SUMMONER_EXPANSION_MOD_ID = "gagadoliano.summonerexpansion";

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

    public static boolean checkSummonerExpansion() {
        return checkOptionalDependency(SUMMONER_EXPANSION_MOD_ID);
    }
}
