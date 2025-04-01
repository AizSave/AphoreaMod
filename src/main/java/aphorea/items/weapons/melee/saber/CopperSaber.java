package aphorea.items.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class CopperSaber extends AphSaberToolItem {

    public CopperSaber() {
        super(500);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(14)
                .setUpgradedValue(1, 75);
        knockback.setBaseValue(150);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.CopperAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                200 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }
}
