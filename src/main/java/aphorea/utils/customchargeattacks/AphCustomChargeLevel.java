package aphorea.utils.customchargeattacks;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;

import java.awt.*;

public class AphCustomChargeLevel<T extends AphChargeAttackToolItem> {

    public int timeToCharge;
    public float damageModifier;
    public Color particleColors;

    public AphCustomChargeLevel(int timeToCharge, float damageModifier, Color particleColors) {
        this.timeToCharge = timeToCharge;
        this.damageModifier = damageModifier;
        this.particleColors = particleColors;
    }

    public void setupAttackItem(InventoryItem attackItem) {
        attackItem.getGndData().setFloat("chargeDamageMultiplier", this.damageModifier);
    }

    public void onReachedLevel(AphCustomChargeAttackHandler<T> attackHandler) {
        if (attackHandler.player.isClient()) {
            if (this.particleColors != null) {
                attackHandler.drawParticleExplosion(30, this.particleColors, 30, 50);
            }

            int totalLevels = attackHandler.chargeLevels.length;
            float currentLevelPercent = (float) (attackHandler.currentChargeLevel + 1) / (float) totalLevels;
            float minPitch = Math.max(0.7F, 1.0F - (float) totalLevels * 0.1F);
            float pitch = GameMath.lerp(currentLevelPercent, 1.0F, minPitch);
            SoundManager.playSound(GameResources.cling, SoundEffect.effect(attackHandler.player).volume(0.5F).pitch(pitch));
        }

    }

    public void updateAtLevel(AphCustomChargeAttackHandler<T> attackHandler, InventoryItem showItem) {
        if (attackHandler.player.isClient() && this.particleColors != null) {
            attackHandler.drawWeaponParticles(showItem, this.particleColors);
        }

    }
}