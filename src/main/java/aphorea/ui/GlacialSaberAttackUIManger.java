package aphorea.ui;

import aphorea.AphResources;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;

import java.awt.*;

public class GlacialSaberAttackUIManger extends AphCustomUI {
    public float chargePercent;
    public int chargeTime;

    public GlacialSaberAttackUIManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = mainGameFormManager.addComponent(new GlacialSaberAttackUIManger.AttackTrackForm(this.formId, AphResources.saberAttackTexture.getWidth() + 20, AphResources.saberAttackTexture.getHeight() + 20));
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
            AphResources.glacialSaberAttackTrackTexture.initDraw().pos(this.getX() + 10, this.getY() + 10).draw();
            int width = this.getWidth() - 20;
            float timeSinceStart = GlacialSaberAttackUIManger.this.chargePercent * GlacialSaberAttackUIManger.this.chargeTime;
            float currentProgress = (timeSinceStart + TICK_MS) / GlacialSaberAttackUIManger.this.chargeTime;
            AphResources.glacialSaberAttackThumbTexture.initDraw().pos(
                    (int) ((width - 2) * (barPercent(currentProgress) + 1)) / 2 + this.getX() + 10 - AphResources.glacialSaberAttackThumbTexture.getWidth() / 2,
                    24 + this.getY() - AphResources.glacialSaberAttackThumbTexture.getHeight() / 2
            ).draw();
        }
    }

    public static float barPercent(float chargePercent) {
        float cycleLength = 2F;
        float radians = (chargePercent / cycleLength) * (float)Math.PI * 2F;
        return (float)(Math.cos(radians));
    }

}
