package aphorea.items.tools.weapons.range.sling;

import aphorea.projectiles.toolitem.SlingStoneProjectile;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class Sling extends AphSlingToolItem {
    public Sling() {
        super(100);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 100.0F);
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new SlingStoneProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                getProjectileVelocity(item, attackerMob),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, attackerMob)
        );
    }
}
