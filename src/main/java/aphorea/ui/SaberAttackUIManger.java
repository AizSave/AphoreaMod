package aphorea.ui;

import aphorea.AphResources;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;

import java.awt.*;

public class SaberAttackUIManger extends AphCustomUI {
    public float chargePercent;
    public int chargeTime;

    public static int baseWidth = 66;
    public static int baseHeight = 24;

    public static int getLoweredY() {
        return (int) (34 * getZoom());
    }

    public SaberAttackUIManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = mainGameFormManager.addComponent(new SaberAttackUIManger.AttackTrackForm(this.formId, getWidth(), getHeight()));
    }

    @Override
    public void updatePosition() {
        this.form.setPosition(
                WindowManager.getWindow().getHudWidth() / 2 - this.form.getWidth() / 2,
                WindowManager.getWindow().getHudHeight() / 2 - this.form.getHeight() / 2 + getLoweredY()
        );
    }

    @Override
    public int getWidth() {
        return (int) (AphResources.glacialSaberAttackTrackTexture.getWidth() * getZoom());
    }

    @Override
    public int getHeight() {
        return (int) (AphResources.glacialSaberAttackTrackTexture.getHeight() * getZoom());
    }

    @Override
    public void setupForm() {
        this.form.setHidden(true);
        super.setupForm();
    }

    @Override
    public void onWindowResized() {
        updatePosition();
        updateSize();
    }

    public class AttackTrackForm extends Form {
        public AttackTrackForm(String name, int width, int height) {
            super(name, width, height);
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            int width = (int) (baseWidth * getZoom());
            int height = (int) (baseHeight * getZoom());

            int realHeight = (int) (baseHeight * 31 * getZoom());

            float progressX = barPercent(showProgress(SaberAttackUIManger.this.chargePercent, SaberAttackUIManger.this.chargeTime));

            getResizedTexture("saberattack", AphResources.saberAttackTexture, width, realHeight)
                    .initDraw()
                    .sprite(0, (int) (30 - 30 * progressX), width, height)
                    .pos(this.getX(), this.getY())
                    .draw();

        }
    }

    public static float barPercent(float chargePercent) {
        if (chargePercent < 0 || chargePercent > 2) {
            return 0;
        }

        return 1 - (float) Math.sin(chargePercent * (Math.PI / 2) + (Math.PI / 2) * (chargePercent <= 1 ? 1 : -1));
    }

}
