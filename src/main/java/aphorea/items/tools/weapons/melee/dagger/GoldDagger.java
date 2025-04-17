package aphorea.items.tools.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class GoldDagger extends AphDaggerToolItem {
    public GoldDagger() {
        super(350);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(450);

        this.attackDamage.setBaseValue(26.0F).setUpgradedValue(1.0F, 88.0F);

        this.attackRange.setBaseValue(40);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.GoldDaggerProjectile(level, attackerMob,
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
        return 300;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.gold;
    }
}