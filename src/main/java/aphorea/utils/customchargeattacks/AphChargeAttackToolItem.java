package aphorea.utils.customchargeattacks;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

abstract public class AphChargeAttackToolItem extends AphSwordToolItem {
    public AphCustomChargeLevel<AphChargeAttackToolItem>[] chargeLevels;
    public boolean isCharging;

    public AphChargeAttackToolItem(int enchantCost, AphCustomChargeLevel<AphChargeAttackToolItem>[] chargeLevels) {
        super(enchantCost);
        this.chargeLevels = chargeLevels;
        if (chargeLevels.length == 0) {
            throw new IllegalArgumentException("Must have at least one charge level for charged items");
        } else {
            this.attackAnimTime.setBaseValue(200);
            this.attackXOffset = 16;
            this.attackYOffset = 16;
            this.resilienceGain.setBaseValue(4.0F);
        }
    }

    @Override
    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    @Override
    public float getSwingRotation(InventoryItem item, int dir, float attackProgress) {
        if (!item.getGndData().getBoolean("shouldFire")) {
            float chargePercent = item.getGndData().getFloat("chargePercent");
            chargePercent = GameMath.limit(chargePercent, 0.0F, 1.0F);
            attackProgress = 0.2F - chargePercent * 0.2F;
        }

        return super.getSwingRotation(item, dir, attackProgress);
    }

    @Override
    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
        if (item.getGndData().getBoolean("shouldFire")) {
            super.showAttack(level, x, y, mob, attackHeight, item, seed, contentReader);
        }
    }

    @Override
    public String canAttack(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        if (player.isAttacking || this.isCharging) {
            return "Already attacking";
        }
        return super.canAttack(level, x, y, player, item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.isAttacking = true;
        isCharging = true;

        AphCustomChargeLevel<AphChargeAttackToolItem>[] charge = this.chargeLevels;
        player.startAttackHandler(new AphCustomChargeAttackHandler<>(player, slot, item, this, seed, x, y, charge));

        return item;
    }

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        float damageMultiplier = item.getGndData().getFloat("chargeDamageMultiplier", 1.0F);
        return super.getAttackDamage(item).modDamage(damageMultiplier);
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    @Override
    public boolean shouldRunOnAttackedBuffEvent(Level level, int x, int y, PlayerMob player, InventoryItem item, PlayerInventorySlot slot, int animTime, int seed, PacketReader contentReader) {
        return false;
    }

    public void endChargeAttack(InventoryItem item, PlayerMob player, Point2D.Float dir, int charge) {
    }

    public void superOnAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
    }
}
