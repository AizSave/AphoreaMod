package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class CopperDagger extends AphDaggerToolItem {
    public CopperDagger() {
        super(300);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);

        this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 90.0F);

        this.attackRange.setBaseValue(36);
        this.knockback.setBaseValue(25);
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.CopperDaggerProjectile(level, player,
                player.x, player.y,
                x, y,
                100 * throwingVelocity, 300,
                getAttackDamage(item),
                getKnockback(item, player),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }
}
