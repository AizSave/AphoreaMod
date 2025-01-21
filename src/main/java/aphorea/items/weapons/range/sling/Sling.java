package aphorea.items.weapons.range.sling;

import aphorea.projectiles.toolitem.SlingStoneProjectile;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class Sling extends AphSlingToolItem {
    public Sling() {
        super(100, "stoneprojectile");
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(15.0F).setUpgradedValue(1.0F, 135.0F);
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return new SlingStoneProjectile(level, player,
                player.x, player.y,
                x, y,
                getProjectileVelocity(item, player),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, player)
        );
    }
}
