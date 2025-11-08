package aphorea.patches;

import aphorea.ui.AphLogoUI;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.MainMenuFormManager;
import net.bytebuddy.asm.Advice;

public class MainMenuPatches {

    @ModMethodPatch(target = MainMenuFormManager.class, name = "setup", arguments = {})
    public static class setup {
        @Advice.OnMethodExit
        static void onExit(@Advice.This MainMenuFormManager mainMenuFormManager) {
            AphLogoUI.setup(mainMenuFormManager);
        }
    }

    @ModMethodPatch(target = MainMenuFormManager.class, name = "onWindowResized", arguments = {GameWindow.class})
    public static class onWindowResized {
        @Advice.OnMethodExit
        static void onExit(@Advice.This MainMenuFormManager mainMenuFormManager) {
            AphLogoUI.onWindowResized(mainMenuFormManager);
        }
    }

}
