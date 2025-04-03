package aphorea.items.tools.weapons.range.sling;

import aphorea.items.tools.weapons.range.sling.logic.SlingAttackHandler;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.level.maps.Level;

abstract public class AphSlingToolItem extends ProjectileToolItem {

    public AphSlingToolItem(int enchantCost) {
        super(enchantCost);
        this.setItemCategory("equipment", "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "rangedweapons");
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.attackRange.setBaseValue(500);
        this.enchantCost.setUpgradedValue(1.0F, 2100);

        this.keyWords.add("sling");

        this.itemAttackerProjectileCanHitWidth = 28;
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
        drawOptions.pointRotation(0, 0, -item.getGndData().getFloat("showAngle"));
    }

    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{ItemRegistry.getItem("stone")}, "stone");
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        int ammoAmount = this.getAvailableAmmo(perspective);
        tooltips.add(Localization.translate("itemtooltip", "ammotip", "value", ammoAmount));
        tooltips.add(Localization.translate("itemtooltip", "sling"));
        tooltips.add(Localization.translate("itemtooltip", "sling2"));
        return tooltips;
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob instanceof AmmoUserMob && this.getAvailableAmmo((AmmoUserMob) attackerMob) > 0 ? null : "";
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        attackerMob.startAttackHandler(new SlingAttackHandler(attackerMob, slot, item, this, animTime, seed));
        return item;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    abstract public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item);

    public void doAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        boolean shouldFire;
        if (attackerMob instanceof AmmoUserMob) {
            AmmoConsumed consumed = ((AmmoUserMob)attackerMob).removeAmmo(ItemRegistry.getItem("stone"), 1, "stone");
            shouldFire = consumed.amount >= 1;
        } else {
            shouldFire = true;
        }

        if (shouldFire) {

            Projectile projectile = this.getProjectile(level, x, y, attackerMob, item);
            GameRandom random = new GameRandom(seed);
            projectile.resetUniqueID(random);

            level.entityManager.projectiles.addHidden(projectile);

            if (level.isServer()) {
                level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
            }
        }
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "sling");
    }

}