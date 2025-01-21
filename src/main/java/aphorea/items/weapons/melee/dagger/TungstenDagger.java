package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class TungstenDagger extends AphDaggerToolItem {
    public TungstenDagger() {
        super(400);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(300);

        this.attackDamage.setBaseValue(40.0F).setUpgradedValue(1.0F, 100.0F);

        this.attackRange.setBaseValue(42);
        this.knockback.setBaseValue(25);
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, boolean shouldDrop) {
        return new DaggerProjectile.TungstenDaggerProjectile(level, player,
                player.x, player.y,
                x, y,
                200, 400,
                getAttackDamage(item),
                getKnockback(item, player),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.tungsten;
    }
}