package aphorea.items.tools.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class TungstenDagger extends AphDaggerToolItem {
    public TungstenDagger() {
        super(1300);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(300);

        this.attackDamage.setBaseValue(40.0F).setUpgradedValue(1.0F, 80.0F);

        this.attackRange.setBaseValue(45);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.TungstenDaggerProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                200 * throwingVelocity, projectileRange(),
                getAttackDamage(item),
                getKnockback(item, attackerMob),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public int projectileRange() {
        return 400;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.tungsten;
    }
}