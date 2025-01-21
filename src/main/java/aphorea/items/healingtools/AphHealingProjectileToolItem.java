package aphorea.items.healingtools;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

import java.util.Arrays;

abstract public class AphHealingProjectileToolItem extends AphMagicHealingToolItem {
    protected IntUpgradeValue velocity = new IntUpgradeValue(50, 0.0F);
    public int moveDist;

    public AphHealingProjectileToolItem(int enchantCost) {
        super(enchantCost);
    }

    protected abstract Projectile[] getProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item);

    public int getFlatVelocity(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("velocity") ? gndData.getInt("velocity") : this.velocity.getValue(this.getUpgradeTier(item));
    }

    public int getProjectileVelocity(InventoryItem item, Mob mob) {
        int velocity = this.getFlatVelocity(item);
        return Math.round(this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * (float) velocity * mob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
    }

    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        AphMagicHealing.addMagicHealingTip(this, list, currentItem, lastItem, perspective);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        onHealingToolItemUsed(player, item);

        if (this.getManaCost(item) > 0) {
            this.consumeMana(player, item);
        }

        Projectile[] projectiles = this.getProjectiles(level, x, y, player, item);

        Arrays.stream(projectiles).forEach(projectile -> {
            projectile.getUniqueID(new GameRandom(seed));
            level.entityManager.projectiles.addHidden(projectile);

            if (this.moveDist != 0) {
                projectile.moveDist(this.moveDist);
            }

            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
            }
        });

        return item;
    }
}
