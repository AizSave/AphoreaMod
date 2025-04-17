package aphorea.items.tools.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class DemonicDagger extends AphDaggerToolItem {
    public DemonicDagger() {
        super(400);
        this.rarity = Rarity.COMMON;
        this.attackAnimTime.setBaseValue(400);

        this.attackDamage.setBaseValue(27.0F).setUpgradedValue(1.0F, 86.0F);

        this.attackRange.setBaseValue(40);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.DemonicDaggerProjectile(level, attackerMob,
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
        return 350;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.demonic;
    }
}
