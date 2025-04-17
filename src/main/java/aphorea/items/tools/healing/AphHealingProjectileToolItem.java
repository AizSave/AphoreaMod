package aphorea.items.tools.healing;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

abstract public class AphHealingProjectileToolItem extends AphMagicHealingToolItem {
    protected IntUpgradeValue velocity = new IntUpgradeValue(50, 0.0F);
    public int moveDist;

    public AphHealingProjectileToolItem(int enchantCost) {
        super(enchantCost);
    }

    protected abstract Projectile[] getProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item);

    public int getFlatVelocity(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("velocity") ? gndData.getInt("velocity") : this.velocity.getValue(this.getUpgradeTier(item));
    }

    public int getProjectileVelocity(InventoryItem item, Mob mob) {
        int velocity = this.getFlatVelocity(item);
        return Math.round(this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * (float) velocity * mob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        AphMagicHealing.addMagicHealingTip(this, list, currentItem, lastItem, perspective);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        onHealingToolItemUsed(attackerMob, item);

        if (this.getManaCost(item) > 0) {
            this.consumeMana(attackerMob, item);
        }

        Projectile[] projectiles = this.getProjectiles(level, x, y, attackerMob, item);

        Arrays.stream(projectiles).forEach(projectile -> {
            projectile.getUniqueID(new GameRandom(seed));
            level.entityManager.projectiles.addHidden(projectile);

            if (this.moveDist != 0) {
                projectile.moveDist(this.moveDist);
            }

            if (level.isServer()) {
                level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
            }
        });

        return item;
    }

    protected Point controlledRangePosition(GameRandom random, Mob mob, int targetX, int targetY, InventoryItem item, int controlledMinRange, int controlledInaccuracy) {
        return controlledRangePosition(random, mob.getX(), mob.getY(), targetX, targetY, this.getAttackRange(item), controlledMinRange, controlledInaccuracy);
    }

    public static Point controlledRangePosition(GameRandom random, int startX, int startY, int targetX, int targetY, int attackRange, int controlledMinRange, int controlledInaccuracy) {
        float fX = (float) targetX;
        float fY = (float) targetY;
        float range = (float) (new Point(startX, startY)).distance((double) fX, (double) fY);
        Point2D.Float norm = GameMath.normalize(fX - (float) startX, fY - (float) startY);
        if (range > (float) attackRange) {
            fX = (float) ((int) ((float) startX + norm.x * (float) attackRange));
            fY = (float) ((int) ((float) startY + norm.y * (float) attackRange));
        } else if (range < (float) controlledMinRange) {
            fX = (float) ((int) ((float) startX + norm.x * (float) controlledMinRange));
            fY = (float) ((int) ((float) startY + norm.y * (float) controlledMinRange));
        }

        float prcRange = (float) (new Point(startX, startY)).distance((double) fX, (double) fY) / (float) attackRange;
        if (controlledInaccuracy > 0) {
            fX += (random.nextFloat() * 2.0F - 1.0F) * (float) controlledInaccuracy * prcRange;
            fY += (random.nextFloat() * 2.0F - 1.0F) * (float) controlledInaccuracy * prcRange;
        }

        return new Point((int) fX, (int) fY);
    }

    @Override
    public AINode<ItemAttackerMob> getItemAttackerWeaponChaserAI(ItemAttackerChaserAINode<? extends ItemAttackerMob> node, ItemAttackerMob mob, InventoryItem item, ItemAttackSlot slot) {
        return super.getItemAttackerWeaponChaserAI(node, mob, item, slot);
    }
}