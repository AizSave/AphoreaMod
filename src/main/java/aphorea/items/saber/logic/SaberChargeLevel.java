package aphorea.items.saber.logic;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.gfx.GameResources;

import java.awt.*;

public class SaberChargeLevel extends GreatswordChargeLevel {

    public SaberChargeLevel(int timeToCharge, float damageModifier, Color particleColors) {
        super(timeToCharge, damageModifier, particleColors);
    }

    @Override
    public void onReachedLevel(GreatswordAttackHandler attackHandler) {
        if (attackHandler.attackerMob.isClient()) {
            if (this.particleColors != null) {
                attackHandler.drawParticleExplosion(3, this.particleColors, 30, 50);
            }

            int totalLevels = attackHandler.chargeLevels.length;
            float currentLevelPercent = (float)(attackHandler.currentChargeLevel + 1) / (float)totalLevels;
            float minPitch = Math.max(0.7F, 1.0F - (float)totalLevels * 0.1F);
            float pitch = GameMath.lerp(currentLevelPercent, 1.0F, minPitch);
            SoundManager.playSound(GameResources.cling, SoundEffect.effect(attackHandler.attackerMob).volume(0.5F).pitch(pitch));
        }

    }
}
