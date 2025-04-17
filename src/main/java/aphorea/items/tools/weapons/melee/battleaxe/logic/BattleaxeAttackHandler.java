package aphorea.items.tools.weapons.melee.battleaxe.logic;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

import java.awt.*;

public class BattleaxeAttackHandler extends GreatswordAttackHandler {
    float speedModifier;

    public BattleaxeAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, float speedModifier, GreatswordChargeLevel... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
        this.speedModifier = speedModifier;
    }

    @Override
    public void onUpdatePacket(PacketReader reader) {
        super.onUpdatePacket(reader);
        this.speedModifier = reader.getNextFloat();
    }

    @Override
    protected void setupClientUpdatePacket(PlayerMob player, PacketWriter writer) {
        super.setupClientUpdatePacket(player, writer);
        writer.putNextFloat(speedModifier);
    }

    @Override
    public long getTimeSinceStart() {
        return (long) (super.getTimeSinceStart() * speedModifier);
    }

    @Override
    public void drawWeaponParticles(InventoryItem showItem, Color color) {
        float chargePercent = showItem.getGndData().getFloat("chargePercent");
        showItem.getGndData().setBoolean("charging", true);
        float angle = this.toolItem.getSwingRotation(showItem, this.attackerMob.getDir(), chargePercent);
        int attackDir = this.attackerMob.getDir();
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
        int range = GameRandom.globalRandom.getIntBetween(0, (int) (this.toolItem.getAttackRange(this.item) * 0.5F));
        this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + (float) offsetX + dx * (float) range + GameRandom.globalRandom.floatGaussian() * 3.0F, this.attackerMob.y + 4.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx, this.attackerMob.dy).color(color).height(20.0F - dy * (float) range - (float) offsetY);
    }
}
