package aphorea.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AphColors {
    public static Color red = new Color(204, 0, 0);
    public static Color green = new Color(0, 204, 0);
    public static Color black = new Color(16, 16, 16);
    public static Color lighter_gray = new Color(180, 220, 220);

    public static Color normal_rarity = new Color(100, 250, 250);

    public static Color copper = new Color(191, 90, 62);
    public static Color iron = new Color(98, 104, 113);
    public static Color gold = new Color(255, 233, 73);
    public static Color demonic = new Color(121, 100, 186);
    public static Color tungsten = new Color(40, 43, 74);

    public static Color diamond = new Color(200, 200, 255);

    public static Color rock_light = new Color(130, 139, 152);
    public static Color rock = new Color(72, 71, 77);

    public static Color wood = new Color(148, 99, 25);
    public static Color honey = new Color(234, 182, 118);
    public static Color blueberry = new Color(16, 93, 162);
    public static Color infected_wood = new Color(149, 109, 87);

    public static Color stone = new Color(139, 140, 122);
    public static Color leather = new Color(116, 78, 59);
    public static Color ice = new Color(92, 166, 193);
    public static Color blood = new Color(204, 0, 0);
    public static Color fire = new Color(191, 87, 0);
    public static Color lighting = new Color(250, 251, 165);

    public static Color gel = new Color(0, 153, 255);
    public static Color unstableGel = new Color(205, 52, 247);
    public static Color unstableGel_light = new Color(140, 100, 200);
    public static Color unstableGel_very_light = new Color(180, 180, 220);

    public static Color infected_light = new Color(255, 153, 255);
    public static Color infected = new Color(161, 87, 181);
    public static Color infected_dark = new Color(125, 0, 178);

    public static Color spinel_lighter = new Color(255, 143, 245);
    public static Color spinel_light = new Color(224, 92, 213);
    public static Color spinel = new Color(202, 37, 173);
    public static Color spinel_dark = new Color(150, 5, 116);
    public static Color spinel_darker = new Color(117, 12, 75);

    public static Color crimson_kora_light = new Color(255, 98, 98);
    public static Color crimson_kora = new Color(206, 30, 68);
    public static Color crimson_kora_dark = new Color(133, 5, 45);

    public static Color fail_message = new Color(200, 100, 100);

    public static Color pink_witch = new Color(255, 0, 191);
    public static Color pink_witch_dark = new Color(191, 0, 255);
    public static Color dark_magic = new Color(104, 0, 204);
    public static Color darker_magic = new Color(52, 0, 104);

    public static Color[] paletteOcean = new Color[]{new Color(0, 127, 255), new Color(0, 128, 128), new Color(0, 255, 255), new Color(0, 0, 128), new Color(100, 149, 237)};
    public static Color[] paletteBlackHole = new Color[]{new Color(0, 0, 0), new Color(15, 10, 25), new Color(30, 20, 50), new Color(45, 30, 75), new Color(60, 40, 90)};
    public static Color[] paletteDemonic = new Color[]{demonic, red};
    public static Color[] paletteDeepDemonic = new Color[]{demonic, tungsten, red};
    public static Color[] paletteMotherSlime = new Color[]{new Color(221, 161, 49), new Color(177, 121, 31), new Color(152, 100, 30)};

    @NotNull
    @Contract("_, _ -> new")
    public static Color withAlpha(@NotNull Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Color withAlpha(@NotNull Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * alpha));
    }
}
