package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.logic.SaberAttackHandler;
import aphorea.items.tools.weapons.melee.saber.logic.SaberDashAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import aphorea.registry.AphBuffs;
import aphorea.ui.AphCustomUIList;
import aphorea.ui.SaberAttackUIManger;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;


abstract public class AphSaberToolItem extends AphSwordToolItem implements ItemInteractAction {

    public IntUpgradeValue dashRange;
    public boolean isAuto;

    protected IntUpgradeValue chargeAnimTime = new IntUpgradeValue(true, 600, 0.0F);

    public AphSaberToolItem(int enchantCost, boolean isAuto) {
        super(enchantCost);

        this.isAuto = isAuto;
        this.enchantCost.setUpgradedValue(1.0F, 500);
        this.attackAnimTime.setBaseValue(200);
        this.dashRange = new IntUpgradeValue(200, 0.0F);
        this.dashRange.setBaseValue(200);
        this.attackRange.setBaseValue(45);
        attackXOffset = 6;
        attackYOffset = 6;

        this.keyWords.add("saber");
    }

    public AphSaberToolItem(int enchantCost) {
        this(enchantCost, false);
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return isAuto;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate("itemtooltip", "saber"));
        tooltips.add(Localization.translate("itemtooltip", "saberdash"));
        return tooltips;
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target,
                this.canDash(attackerMob) || target.getDistance(attackerMob) < this.getAttackRange(item) * 0.8F ?
                        5F : 36F
        );
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return !mob.isPlayer && this.canDash(mob) ? (int) ((float) this.dashRange.getValue(this.getUpgradeTier(item)) * 0.8F) : super.getItemAttackerAttackRange(mob, item) * 6;
    }

    public void superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        shotProjectile(level, x, y, attackerMob, item, seed);
        super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    public void shotProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        float powerPercent = chargePercent(item);
        if (powerPercent >= 0.92F) {
            powerPercent = 1F;
        }
        if (powerPercent >= 0.5F) {
            Projectile[] projectiles = this.getProjectiles(level, attackerMob.getX(), attackerMob.getY(), x, y, attackerMob, item, (powerPercent - 0.375F) * 1.6F, seed);
            for (Projectile projectile : projectiles) {
                projectile.resetUniqueID(new GameRandom(seed));
                attackerMob.addAndSendAttackerProjectile(projectile, 0);
            }
        }
    }

    public int getChargeAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("chargeAnimTime") ? gndData.getInt("chargeAnimTime") : this.chargeAnimTime.getValue(this.getUpgradeTier(item));
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob)) {
            int animTime = (int) ((float) this.getChargeAnimTime(item, attackerMob));
            mapContent.setBoolean("charging", true);
            attackerMob.startAttackHandler((new SaberDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed)));
        } else {
            int animTime = (int) ((float) this.getChargeAnimTime(item, attackerMob));
            item.getGndData().setBoolean("charging", false);
            attackerMob.startAttackHandler((new SaberAttackHandler(attackerMob, slot, item, this, animTime, isAuto, seed)));

        }
        return item;
    }

    public void superShowAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("charged")) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
        if (level.isClient() && level.getClient().getPlayer().getUniqueID() == attackerMob.getUniqueID()) {
            AphCustomUIList.saberAttack.chargePercent = item.getGndData().getFloat("chargePercent");
        }
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "saber");
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canDash(attackerMob);
    }

    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding() && !attackerMob.buffManager.hasBuff(AphBuffs.SABER_DASH_COOLDOWN);
    }

    public float getDashDamageMultiplier(InventoryItem item) {
        return 1;
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
        int animTime = (int) ((float) this.getChargeAnimTime(item, attackerMob));
        mapContent.setBoolean("charging", true);
        attackerMob.startAttackHandler((new SaberDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed)).startFromInteract());
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

    public Projectile[] getProjectiles(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        return new Projectile[]{getProjectile(level, x, y, targetX, targetY, attackerMob, item, powerPercent, seed)};
    }

    abstract public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed);

    public float chargePercent(InventoryItem item) {
        return SaberAttackUIManger.barPercent(item.getGndData().getFloat("attackPercent", 0F));
    }

    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item).modDamage(modifiedAttackDamage(item));
    }

    public float modifiedAttackDamage(InventoryItem item) {
        float powerPercent = chargePercent(item) * 0.5F + 0.5F;
        if (powerPercent >= 0.92F) {
            powerPercent = 1F;
        }
        return powerPercent;
    }
}
