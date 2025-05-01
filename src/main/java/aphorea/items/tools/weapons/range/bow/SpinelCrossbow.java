package aphorea.items.tools.weapons.range.bow;

import aphorea.projectiles.toolitem.SpinelArrowProjectile;
import necesse.engine.GameLog;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.level.maps.Level;

public class SpinelCrossbow extends BowProjectileToolItem {
    public SpinelCrossbow() {
        super(1300);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(800);
        this.attackDamage.setBaseValue(65.0F).setUpgradedValue(1.0F, 120.0F);
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(200);
        this.attackXOffset = 12;
        this.attackYOffset = 10;
    }

    public Item getArrowItem(Level level, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        return ItemRegistry.getItem("stonearrow");
    }

    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        Item arrow = this.getArrowItem(level, attackerMob, seed, item);
        map.setShortUnsigned("arrowID", arrow == null ? '\uffff' : arrow.getID());
    }

    @Override
    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{
                ItemRegistry.getItem("stonearrow")
        }, "arrowammo");
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int arrowID = mapContent.getShortUnsigned("arrowID", 65535);
        if (arrowID != 65535) {
            Item arrow = ItemRegistry.getItem(arrowID);
            if (arrow != null && arrow.type == Type.ARROW) {
                GameRandom random = new GameRandom(seed + 5);
                float ammoConsumeChance = ((ArrowItem) arrow).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                boolean dropItem;
                boolean shouldFire;
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob) attackerMob).removeAmmo(arrow, 1, "arrowammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }

                if (shouldFire) {
                    this.fireProjectiles(level, x, y, attackerMob, item, seed, (ArrowItem) arrow, dropItem, mapContent);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (arrow == null ? arrowID : arrow.getStringID()) + " as arrow.");
            }
        }

        return item;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return getProjectile(level, x, y, owner, item);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        return getProjectile(level, x, y, attackerMob, item);
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new SpinelArrowProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, getProjectileVelocity(item, attackerMob), getAttackRange(item), getAttackDamage(item), getKnockback(item, attackerMob));
    }
}
