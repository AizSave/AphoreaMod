package aphorea.patches;

import aphorea.ui.AphCustomUI;
import aphorea.ui.AphCustomUIList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.window.GameWindow;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = GameWindow.class, name = "updateSceneSize", arguments = {})
public class UpdateSceneSize {
    @Advice.OnMethodExit
    static void onExit(@Advice.This GameWindow gameWindow) {
        for (AphCustomUI manager : AphCustomUIList.list.values()) {
            if (manager.form != null) {
                manager.onUpdateSceneSize();
            }
        }
    }
}
