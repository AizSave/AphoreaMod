package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GoldDagger extends AphDaggerToolItem {
    public GoldDagger() {
        super(300);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);

        this.attackDamage.setBaseValue(26.0F).setUpgradedValue(1.0F, 94.0F);

        this.attackRange.setBaseValue(38);
        this.knockback.setBaseValue(25);
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.GoldDaggerProjectile(level, player,
                player.x, player.y,
                x, y,
                200 * throwingVelocity, 300,
                getAttackDamage(item),
                getKnockback(item, player),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }
}