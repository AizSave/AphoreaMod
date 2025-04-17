package aphorea.methodpatches;

import aphorea.ui.AphCustomUI;
import aphorea.ui.AphCustomUIList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = MainGameFormManager.class, name = "setup", arguments = {})
public class SetupForm {
    @Advice.OnMethodExit
    static void onExit(@Advice.This MainGameFormManager mainGameFormManager) {
        for (AphCustomUI manager : AphCustomUIList.list.values()) {
            manager.mainGameFormManager = mainGameFormManager;
            manager.startForm();
            if (manager.form != null) {
                manager.setupForm();
            }
        }
    }
}