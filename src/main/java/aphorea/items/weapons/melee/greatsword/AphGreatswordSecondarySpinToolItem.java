package aphorea.items.weapons.melee.greatsword;

import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import aphorea.items.weapons.melee.greatsword.logic.GreatswordSecondarySpinAttackHandler;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

abstract public class AphGreatswordSecondarySpinToolItem extends AphGreatswordToolItem implements ItemInteractAction {
    Color spinAttackColor;
    boolean secondaryAttack = false;

    public AphGreatswordSecondarySpinToolItem(int enchantCost, int attackAnimTime, GreatswordChargeLevel[] chargeLevels, Color spinAttackColor) {
        super(enchantCost, chargeLevels);

        this.attackAnimTime.setBaseValue(attackAnimTime);
        this.spinAttackColor = spinAttackColor;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "spinsecondaryattack"));
        return tooltips;
    }

    @Override
    public float getSwingRotationOffset(InventoryItem item, int dir, float swingAngle) {
        float offset = super.getSwingRotationOffset(item, dir, swingAngle);
        if (secondaryAttack) {
            if (dir == 1 || dir == 3) {
                offset -= 180;
            } else {
                offset -= 90;
            }
        }
        return offset;
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target, 5F);
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return !mob.isPlayer && this.canDash(mob) ? 200 : super.getItemAttackerAttackRange(mob, item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob)) {
            mapContent.setBoolean("chargeUp", true);
            secondaryAttack = true;
            attackerMob.startAttackHandler((new GreatswordSecondarySpinAttackHandler<>(attackerMob, slot, item, this, 2000, spinAttackColor, seed)));
        } else {
            item.getGndData().setBoolean("chargeUp", false);
            secondaryAttack = false;
            if (animAttack == 0) {
                attackerMob.startAttackHandler(new GreatswordAttackHandler(attackerMob, slot, item, this, seed, x, y, this.chargeLevels));
            }
        }
        return item;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return canDash(attackerMob);
    }

    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding() && !attackerMob.buffManager.hasBuff(AphBuffs.SPIN_ATTACK_COOLDOWN);
    }

    @Override
    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0;
    }

    @Override
    public boolean getConstantInteract(InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        mapContent.setBoolean("chargeUp", true);
        secondaryAttack = true;
        attackerMob.startAttackHandler((new GreatswordSecondarySpinAttackHandler<>(attackerMob, slot, item, this, 2000, spinAttackColor, seed)).startFromInteract());
        return item;
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y) {
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    @Override
    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return secondaryAttack ? 360.0F : 150.0F;
    }

    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return secondaryAttack ? 360.0F : 150.0F;
    }
}
