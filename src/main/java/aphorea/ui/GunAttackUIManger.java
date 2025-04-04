package aphorea.ui;

import aphorea.AphResources;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;

import java.awt.*;

public class GunAttackUIManger extends AphCustomUI {
    public float chargePercent;
    public int chargeTime;

    public static int getBorderAdded() {
        return (int) (10 * getZoom());
    }

    public static int getLoweredY() {
        return (int) (34 * getZoom());
    }

    public GunAttackUIManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = mainGameFormManager.addComponent(new GunAttackUIManger.AttackTrackForm(this.formId, getWidth(), getHeight()));
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
        return (int) (AphResources.gunAttackTrackTexture.getWidth() * getZoom()) + getBorderAdded() * 2;
    }

    @Override
    public int getHeight() {
        return (int) (AphResources.gunAttackTrackTexture.getHeight() * getZoom()) + getBorderAdded() * 2;
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
            int trackWidth = (int) (AphResources.gunAttackTrackTexture.getWidth() * getZoom());
            int trackHeight = (int) (AphResources.gunAttackTrackTexture.getHeight() * getZoom());

            getResizedTexture("gunattacktrack", AphResources.gunAttackTrackTexture, trackWidth, trackHeight)
                    .initDraw()
                    .pos(
                            this.getX() + getBorderAdded(),
                            this.getY() + getBorderAdded()
                    ).draw();

            int thumbWidth = (int) (AphResources.gunAttackThumbTexture.getWidth() * getZoom());
            int thumbHeight = (int) (AphResources.gunAttackThumbTexture.getHeight() * getZoom());

            int midX = getWidth() / 2 + this.getX() - thumbWidth / 2;
            int midY = getHeight() / 2 + this.getY() - thumbHeight / 2;

            float progressX = barPercent(showProgress(GunAttackUIManger.this.chargePercent, GunAttackUIManger.this.chargeTime));
            float progressY = barY(showProgress(GunAttackUIManger.this.chargePercent, GunAttackUIManger.this.chargeTime));

            getResizedTexture("gunattackthumb", AphResources.gunAttackThumbTexture, thumbWidth, thumbHeight)
                    .initDraw()
                    .pos(
                            midX + (int) (trackWidth * progressX * 0.5F),
                            midY + (int) (trackHeight * (progressY - 1) * 0.5F)
                    ).draw();
        }
    }

    public static float barPercent(float chargePercent) {
        float cycleLength = 2F;
        float radians = (chargePercent / cycleLength) * (float) Math.PI * 2F;
        return (float) (Math.cos(radians));
    }

    public static float barY(float chargePercent) {
        float cycleLength = 2F;
        float radians = (chargePercent / cycleLength) * (float) Math.PI * 2F;
        return Math.abs((float) Math.sin(radians));
    }

}
