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

    public static int width = 66;
    public static int height = 24;

    public SaberAttackUIManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = mainGameFormManager.addComponent(new SaberAttackUIManger.AttackTrackForm(this.formId, width, height));
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

    public class AttackTrackForm extends Form {
        public AttackTrackForm(String name, int width, int height) {
            super(name, width, height);
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            float timeSinceStart = SaberAttackUIManger.this.chargePercent * SaberAttackUIManger.this.chargeTime;
            float currentProgress = (timeSinceStart + TICK_MS) / SaberAttackUIManger.this.chargeTime;
            AphResources.saberAttackTexture.initDraw()
                    .sprite(0, 30 - Math.round(barPercent(currentProgress) * 30), width, height)
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
