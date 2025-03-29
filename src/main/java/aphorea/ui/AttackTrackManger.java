package aphorea.ui;

import aphorea.AphResources;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;

import java.awt.*;

public class AttackTrackManger extends AphCustomUI {
    public static final int TICK_MS = 25;

    public float chargePercent;
    public int chargeTime;

    public AttackTrackManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = mainGameFormManager.addComponent(new AttackTrackManger.AttackTrackForm(this.formId, AphResources.attackTrackTexture.getWidth() + 20, AphResources.attackTrackTexture.getHeight() + 20));
    }

    @Override
    public void updatePosition() {
        this.form.setPosition(
                WindowManager.getWindow().getHudWidth() / 2 - this.form.getWidth() / 2,
                WindowManager.getWindow().getHudHeight() / 2 + 6
        );
    }

    @Override
    public void setupForm() {
        this.form.setHidden(true);
        super.setupForm();
    }

    public static class AttackTrackForm extends Form {
        public AttackTrackForm(String name, int width, int height) {
            super(name, width, height);
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            AphResources.attackTrackTexture.initDraw().pos(this.getX() + 10, this.getY() + 10).draw();
            int width = this.getWidth() - 20;
            int height = this.getWidth() / 2 - 20;
            float timeSinceStart = AphCustomUIList.attackTrackManager.chargePercent * AphCustomUIList.attackTrackManager.chargeTime;
            float currentProgress = (timeSinceStart + TICK_MS) / AphCustomUIList.attackTrackManager.chargeTime;
            AphResources.attackThumbTexture.initDraw().pos(
                    (int) ((width - 2) * (barX(currentProgress) + 1)) / 2 + this.getX() + 10 - AphResources.attackThumbTexture.getWidth() / 2,
                    (int) (height * Math.abs(barY(currentProgress)) * 0.7F) + this.getY() + 10 - AphResources.attackThumbTexture.getHeight() / 2
            ).draw();
        }
    }

    public static float barX(float chargePercent) {
        float cycleLength = 2F;
        float radians = (chargePercent / cycleLength) * (float)Math.PI * 2F;
        return (float)(Math.cos(radians));
    }

    public static float barY(float chargePercent) {
        float cycleLength = 2F;
        float radians = (chargePercent / cycleLength) * (float)Math.PI * 2F;
        return (float)(Math.sin(radians));
    }

}
