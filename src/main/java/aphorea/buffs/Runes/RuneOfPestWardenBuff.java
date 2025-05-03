package aphorea.buffs.Runes;

import aphorea.utils.AphColors;
import necesse.engine.GlobalData;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.MainGameCamera;

public class RuneOfPestWardenBuff extends AphBaseRuneActiveBuff implements MovementTickBuff {
    public RuneOfPestWardenBuff(float baseEffectNumber) {
        super(baseEffectNumber, 60000, new ModifierValue<>(BuffModifiers.PARALYZED, true), new ModifierValue<>(BuffModifiers.INCOMING_DAMAGE_MOD, -1000F), new ModifierValue<>(BuffModifiers.SPEED, 4F));
    }

    float angle;
    boolean initAngle = true;

    final float maxDeltaAngle = (float) Math.toRadians(30);
    final float speed = 200;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        buff.owner.setDir(2);
        initAngle = true;
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
    }

    private float normalizeAngle(float angle) {
        while (angle <= -Math.PI) angle += (float) (2 * Math.PI);
        while (angle > Math.PI) angle -= (float) (2 * Math.PI);
        return angle;
    }

    @Override
    public void tickMovement(ActiveBuff activeBuff, float delta) {
        PlayerMob player = (PlayerMob) activeBuff.owner;

        MainGame mainGame = (MainGame) GlobalData.getCurrentState();
        MainGameCamera camera = mainGame.getCamera();

        float mouseLevelX = camera.getMouseLevelPosX();
        float mouseLevelY = camera.getMouseLevelPosY();

        float newAngle = (float) Math.atan2(mouseLevelY - player.y, mouseLevelX - player.x);

        if (initAngle) {
            initAngle = false;
            angle = newAngle;
        } else {
            float deltaAngle = normalizeAngle(newAngle - angle);

            float maxDeltaAngle = this.maxDeltaAngle * delta / 250;
            if (Math.abs(deltaAngle) > maxDeltaAngle) {
                angle += Math.signum(deltaAngle) * maxDeltaAngle;
            } else {
                angle = newAngle;
            }

            angle = normalizeAngle(angle);
        }

        player.dx = speed * (float) Math.cos(angle);
        player.dy = speed * (float) Math.sin(angle);

        float currentSpeed = player.getCurrentSpeed() * delta / 250F;
        float currentSpeedX = currentSpeed * (float) Math.cos(angle) * (delta / 250F);
        float currentSpeedY = currentSpeed * (float) Math.sin(angle) * (delta / 250F);

        player.getLevel().entityManager.addParticle(player.x + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), player.y + GameRandom.globalRandom.nextInt(5) + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(currentSpeedX / 2, currentSpeedY / 2).color(AphColors.green).heightMoves(16, 0, 1F, 0.5F, 0, 0);
    }
}