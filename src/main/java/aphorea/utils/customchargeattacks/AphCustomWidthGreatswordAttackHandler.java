package aphorea.utils.customchargeattacks;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

import java.awt.*;

public class AphCustomWidthGreatswordAttackHandler extends GreatswordAttackHandler {
    float width;

    public AphCustomWidthGreatswordAttackHandler(float width, PlayerMob player, PlayerInventorySlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel... chargeLevels) {
        super(player, slot, item, toolItem, seed, startX, startY, chargeLevels);
        this.width = width;
    }

    public void drawWeaponParticles(InventoryItem showItem, Color color) {
        float chargePercent = showItem.getGndData().getFloat("chargePercent");
        showItem.getGndData().setBoolean("charging", true);
        float angle = this.toolItem.getSwingRotation(showItem, this.player.getDir(), chargePercent);
        int attackDir = this.player.getDir();
        int offsetX = 0;
        int offsetY = 0;
        if (attackDir == 0) {
            angle = -angle - 90.0F;
            offsetY = -8;
        } else if (attackDir == 1) {
            angle = -angle + 180.0F + 45.0F;
            offsetX = 8;
        } else if (attackDir == 2) {
            angle = -angle + 90.0F;
            offsetY = 12;
        } else {
            angle = angle + 90.0F + 45.0F;
            offsetX = -8;
        }

        float dx = GameMath.sin(angle);
        float dy = GameMath.cos(angle);
        int range = GameRandom.globalRandom.getIntBetween(0, this.toolItem.getAttackRange(this.item));
        float rangeWidth = GameRandom.globalRandom.getFloatBetween(-width / 2, width / 2);
        this.player.getLevel().entityManager.addParticle(this.player.x + (float) offsetX + dx * (float) range + GameRandom.globalRandom.floatGaussian() * 3.0F + dy * rangeWidth, this.player.y + 4.0F + GameRandom.globalRandom.floatGaussian() * 4 + dy * rangeWidth, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.player.dx, this.player.dy).color(color).height(20.0F - dy * (float) range - (float) offsetY);
    }

}
