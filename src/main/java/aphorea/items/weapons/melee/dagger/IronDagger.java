package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class IronDagger extends AphDaggerToolItem {
    public IronDagger() {
        super(300);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);

        this.attackDamage.setBaseValue(24.0F).setUpgradedValue(1.0F, 93.0F);

        this.attackRange.setBaseValue(36);
        this.knockback.setBaseValue(25);
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.IronDaggerProjectile(level, player,
                player.x, player.y,
                x, y,
                150 * throwingVelocity, 300,
                getAttackDamage(item),
                getKnockback(item, player),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.iron;
    }
}
