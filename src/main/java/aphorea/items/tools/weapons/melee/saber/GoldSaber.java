package aphorea.items.tools.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.GoldSwordToolItem;
import necesse.level.maps.Level;

public class GoldSaber extends AphSaberToolItem {

    public GoldSaber() {
        super(350);
        rarity = Rarity.NORMAL;
        attackDamage.setBaseValue(22)
                .setUpgradedValue(1, 90);
        knockback.setBaseValue(75);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.GoldAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                200 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }

}
