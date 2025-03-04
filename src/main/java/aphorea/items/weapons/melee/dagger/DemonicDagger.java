package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class DemonicDagger extends AphDaggerToolItem {
    public DemonicDagger() {
        super(400);
        this.rarity = Rarity.COMMON;
        this.attackAnimTime.setBaseValue(400);

        this.attackDamage.setBaseValue(27.0F).setUpgradedValue(1.0F, 95.0F);

        this.attackRange.setBaseValue(40);
        this.knockback.setBaseValue(25);
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.DemonicDaggerProjectile(level, player,
                player.x, player.y,
                x, y,
                200 * throwingVelocity, 350,
                getAttackDamage(item),
                getKnockback(item, player),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.demonic;
    }
}
