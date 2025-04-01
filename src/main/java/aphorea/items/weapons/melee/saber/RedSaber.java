package aphorea.items.weapons.melee.saber;

import aphorea.items.weapons.melee.saber.logic.SaberAttackHandler;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

public class RedSaber extends AphSaberToolItem {

    public RedSaber() {
        super(800);
        rarity = Rarity.EPIC;
        attackDamage.setBaseValue(80)
                .setUpgradedValue(1, 80);
        knockback.setBaseValue(200);

        this.attackRange.setBaseValue(80);

        chargeAnimTime.setBaseValue(1000);

        attackXOffset = 10;
        attackYOffset = 10;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.RedAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                300 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(item.getGndData().getFloat("modifyDamage", 1F)).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }

    @Override
    public void superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        float chargePercent = chargePercent(item);

        if(chargePercent >= 0.5F && item.getGndData().getBoolean("doDash")) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();

            if (attackerMob.isServer()) {
                int strength = (int) (200 * chargePercent(item));
                Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
                level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, dir.x, dir.y, (float) strength), level);
            }
        }

        super.superOnAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = (int) (((float) this.getChargeAnimTime(item, attackerMob)) * 0.6F);
        item.getGndData().setBoolean("charging", false);
        item.getGndData().setFloat("modifyDamage", 1F);
        item.getGndData().setBoolean("doDash", false);
        attackerMob.startAttackHandler((new SaberAttackHandler(attackerMob, slot, item, this, animTime, false, seed)));
        return item;

    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = (int) ((float) this.getChargeAnimTime(item, attackerMob));
        item.getGndData().setBoolean("charging", false);
        item.getGndData().setFloat("modifyDamage", 2F);
        item.getGndData().setBoolean("doDash", true);
        attackerMob.startAttackHandler((new SaberAttackHandler(attackerMob, slot, item, this, animTime, false, seed)).startFromInteract());
        return item;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding();
    }

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item).modDamage(item.getGndData().getFloat("modifyDamage", 1F));
    }
}
