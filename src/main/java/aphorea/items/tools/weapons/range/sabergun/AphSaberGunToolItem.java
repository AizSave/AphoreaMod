package aphorea.items.tools.weapons.range.sabergun;

import aphorea.items.tools.weapons.range.sabergun.logic.SaberGunAttackHandler;
import aphorea.items.tools.weapons.range.sabergun.logic.SaberGunDashAttackHandler;
import aphorea.registry.AphBuffs;
import aphorea.ui.AphCustomUIList;
import aphorea.ui.GunAttackUIManger;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.*;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

abstract public class AphSaberGunToolItem extends ProjectileToolItem implements ItemInteractAction {
    public IntUpgradeValue dashRange;

    public AphSaberGunToolItem(int enchantCost) {
        super(enchantCost);
        this.setItemCategory("equipment", "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "rangedweapons");
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.attackRange.setBaseValue(500);
        this.enchantCost.setUpgradedValue(1.0F, 2100);
        this.attackAnimTime.setBaseValue(500);
        this.velocity.setBaseValue(400);

        this.dashRange = new IntUpgradeValue(200, 0.0F);
        this.dashRange.setBaseValue(200);

        this.keyWords.add("saber");
        this.keyWords.add("sabergun");

        this.itemAttackerProjectileCanHitWidth = 28;

        attackXOffset = 16;
        attackYOffset = 10;
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int ammoAmount = this.getAvailableAmmo(perspective);
            if (ammoAmount > 999) {
                ammoAmount = 999;
            }

            String amountString = String.valueOf(ammoAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString((float) (x + 28 - width), (float) (y + 16), amountString, tipFontOptions);
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY, 45);
    }

    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{ItemRegistry.getItem("simplebullet")}, "bulletammo");
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        int ammoAmount = this.getAvailableAmmo(perspective);
        tooltips.add(Localization.translate("itemtooltip", "ammotip", "value", ammoAmount));
        addLeftClickTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "saberdash"));
        return tooltips;
    }

    public void addLeftClickTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {};

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        if(this.getBaseArmorPenPercent() != 0) {
            this.addAttackArmorPenTip(list, currentItem, lastItem, perspective);
        }
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    public void addAttackArmorPenTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob attackerMob) {
        DoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "attackarmorpentip", "value", getBaseArmorPenPercent() * 100.0F, 0);
        if (lastItem != null) {
            int lastMaxSpeed = Math.max(this.getAttackAnimTime(lastItem, attackerMob), this.getAttackCooldownTime(lastItem, attackerMob));
            tip.setCompareValue(this.toAttacksPerSecond(lastMaxSpeed));
        }

        list.add(200, tip);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob instanceof AmmoUserMob && this.getAvailableAmmo((AmmoUserMob) attackerMob) > 0 ? null : "";
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        attackerMob.startAttackHandler(new SaberGunAttackHandler(attackerMob, slot, item, this, animTime, seed));
        return item;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    abstract public float getBaseArmorPenPercent();

    public float getArmorPenPercent(Level level, ItemAttackerMob attackerMob, InventoryItem item) {
        return getBaseArmorPenPercent();
    }

    abstract public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item);

    abstract public int getProjectilesNumber(InventoryItem item);

    abstract public float getProjectilesMaxSpread(InventoryItem item);

    abstract public float getDashDamageMultiplier(InventoryItem item);

    abstract public void doAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed);

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "sabergun");
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canDash(attackerMob);
    }

    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding() && !attackerMob.buffManager.hasBuff(AphBuffs.SABER_DASH_COOLDOWN);
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
        int animTime = (int) ((float) this.getAttackAnimTime(item, attackerMob));
        mapContent.setBoolean("charging", true);
        attackerMob.startAttackHandler((new SaberGunDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed)).startFromInteract());
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
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if(level.isClient() && level.getClient().getPlayer().getUniqueID() == attackerMob.getUniqueID()) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
            AphCustomUIList.gunAttack.chargePercent = item.getGndData().getFloat("chargePercent");
        }
    }


    public static float spreadPercent(float chargePercent) {
        return Math.abs(GunAttackUIManger.barPercent(chargePercent));
    }
}