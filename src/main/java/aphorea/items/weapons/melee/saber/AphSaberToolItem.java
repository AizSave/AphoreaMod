package aphorea.items.weapons.melee.saber;

import aphorea.items.weapons.melee.saber.logic.SaberAttackHandler;
import aphorea.items.weapons.melee.saber.logic.SaberChargeLevel;
import aphorea.items.weapons.melee.saber.logic.SaberDashAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
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


abstract public class AphSaberToolItem extends AphGreatswordToolItem implements ItemInteractAction {

    public IntUpgradeValue dashRange;
    public SaberChargeLevel[] nonPlayerChargeLevels;

    public AphSaberToolItem(int enchantCost) {
        super(enchantCost, getChargeLevels());

        this.nonPlayerChargeLevels = getNonPlayerChargeLevels();

        this.enchantCost.setUpgradedValue(1.0F, 500);

        this.dashRange = new IntUpgradeValue(200, 0.0F);
        this.dashRange.setBaseValue(200);

        this.attackRange.setBaseValue(60);

        this.keyWords.add("saber");
    }

    public AphSaberToolItem(int enchantCost, SaberChargeLevel[] chargeLevels, SaberChargeLevel[] nonPlayerChargeLevels) {
        this(enchantCost);
    }

    public static SaberChargeLevel[] getChargeLevels() {
        return new SaberChargeLevel[]{
                new SaberChargeLevel(80, 1.0F, new Color(255, 255, 255)),
                new SaberChargeLevel(90, 1.25F, new Color(255, 255, 0)),
                new SaberChargeLevel(100, 1.5F, new Color(255, 128, 0)),
                new SaberChargeLevel(120, 1.75F, new Color(255, 0, 0)),
                new SaberChargeLevel(120, 2F, new Color(47, 0, 0)),
                new SaberChargeLevel(120, 1.75F, new Color(255, 0, 0)),
                new SaberChargeLevel(100, 1.5F, new Color(255, 128, 0)),
                new SaberChargeLevel(90, 1.25F, new Color(255, 255, 0)),
                new SaberChargeLevel(80, 1.0F, new Color(255, 255, 255))
        };
    }

    public static SaberChargeLevel[] getNonPlayerChargeLevels() {
        return new SaberChargeLevel[]{
                new SaberChargeLevel(80, 1.0F, new Color(255, 255, 255)),
                new SaberChargeLevel(90, 1.25F, new Color(255, 255, 0)),
                new SaberChargeLevel(100, 1.5F, new Color(255, 128, 0)),
                new SaberChargeLevel(120, 1.75F, new Color(255, 0, 0)),
                new SaberChargeLevel(120, 2F, new Color(47, 0, 0))
        };
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate("itemtooltip", "saber"));
        tooltips.add(Localization.translate("itemtooltip", "saber2"));
        tooltips.add(Localization.translate("itemtooltip", "saber3"));
        return tooltips;
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target,
                this.canDash(attackerMob) || target.getDistance(attackerMob) < this.getAttackRange(item) * 0.8F ?
                        5F : 70F
                );
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return !mob.isPlayer && this.canDash(mob) ? (int)((float)this.dashRange.getValue(this.getUpgradeTier(item)) * 0.8F) : super.getItemAttackerAttackRange(mob, item) * 6;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob)) {
            int animTime = (int) ((float) this.getAttackAnimTime(item, attackerMob));
            mapContent.setBoolean("chargeUp", true);
            attackerMob.startAttackHandler((new SaberDashAttackHandler(attackerMob, slot, item, this, animTime, new Color(190, 220, 220), seed)));
        } else {
            item.getGndData().setBoolean("chargeUp", false);
            if (animAttack == 0) {
                attackerMob.startAttackHandler(new SaberAttackHandler(attackerMob, slot, item, this, seed, x, y, attackerMob.isPlayer ? this.chargeLevels : this.nonPlayerChargeLevels));
            }
        }
        return item;
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
        mapContent.setBoolean("chargeUp", true);
        attackerMob.startAttackHandler((new SaberDashAttackHandler(attackerMob, slot, item, this, animTime, new Color(190, 220, 220), seed)).startFromInteract());
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

    abstract public Projectile getProjectile(Level level, ItemAttackerMob attackerMob, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback);
}
