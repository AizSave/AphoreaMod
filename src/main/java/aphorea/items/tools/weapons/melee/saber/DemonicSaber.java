package aphorea.items.tools.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;


public class DemonicSaber extends AphSaberToolItem {

    public DemonicSaber() {
        super(400);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(30)
                .setUpgradedValue(1, 85);
        knockback.setBaseValue(80);

        this.attackRange.setBaseValue(60);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.DemonicAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                200 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }
}
