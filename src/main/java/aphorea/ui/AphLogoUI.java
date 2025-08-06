package aphorea.ui;

import aphorea.AphResources;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainMenuFormManager;

import java.awt.*;

public class AphLogoUI {
    public static AphLogoForm form;

    public static void setup(MainMenuFormManager mainMenuFormManager) {
        form = mainMenuFormManager.mainForm.main.addComponent(new AphLogoForm(AphResources.modLogo.getWidth(), AphResources.modLogo.getHeight()));
        updatePosition(mainMenuFormManager);
    }

    public static void onWindowResized(MainMenuFormManager mainMenuFormManager) {
        updatePosition(mainMenuFormManager);
    }

    public static void updatePosition(MainMenuFormManager mainMenuFormManager) {
        Form mainForm = mainMenuFormManager.mainForm.mainForm;
        form.setX(mainForm.getX() + (mainForm.getWidth() - form.getWidth()) / 2);
        form.setY(mainForm.getY() - form.getHeight() - 20);
    }

    public static class AphLogoForm extends Form {
        public AphLogoForm(int width, int height) {
            super("aphlogoui", width, height);
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            AphResources.modLogo.initDraw().draw(getX(), getY());
        }
    }

}
