package aphorea.ui;

import aphorea.AphResources;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = MainGameFormManager.class, name = "setup", arguments = {})
public class OnDisplayForm {
    @Advice.OnMethodExit
    static void onExit(@Advice.This MainGameFormManager mainGameFormManager) {
        String formName = "attacktrack";
        AttackTrackManger attackTrackManger = new AttackTrackManger();
        attackTrackManger.form = mainGameFormManager.addComponent(new AttackTrackManger.AttackTrackForm(formName, AphResources.attackTrackTexture.getWidth() + 20, AphResources.attackTrackTexture.getHeight() + 20));
        attackTrackManger.form.setHidden(true);
        AphCustomUI.attackTrackManager = attackTrackManger;
        AphCustomUI.list.put(formName, attackTrackManger);

        for (AphCustomUI manager : AphCustomUI.list.values()) {
            manager.mainGameFormManager = mainGameFormManager;
            manager.updatePosition();
        }
    }
}