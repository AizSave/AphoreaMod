package aphorea.items.tools.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class IronSaber extends AphSaberToolItem {

    public IronSaber() {
        super(500);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(20)
                .setUpgradedValue(1, 80);
        knockback.setBaseValue(75);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.IronAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                200 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }

}
