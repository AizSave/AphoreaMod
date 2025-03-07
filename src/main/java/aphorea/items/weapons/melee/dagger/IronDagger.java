package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
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

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, boolean shouldDrop) {
        return new DaggerProjectile.IronDaggerProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                150, projectileRange(),
                getAttackDamage(item),
                getKnockback(item, attackerMob),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public int projectileRange() {
        return 300;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.iron;
    }
}
