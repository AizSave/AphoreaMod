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

    @Override
    public void updatePosition() {
        this.form.setPosition(
                WindowManager.getWindow().getHudWidth() / 2 - this.form.getWidth() / 2,
                WindowManager.getWindow().getHudHeight() / 2 + 6
        );
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
            float timeSinceStart = AphCustomUI.attackTrackManager.chargePercent * AphCustomUI.attackTrackManager.chargeTime;
            float currentProgress = (timeSinceStart + TICK_MS) / AphCustomUI.attackTrackManager.chargeTime;
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
