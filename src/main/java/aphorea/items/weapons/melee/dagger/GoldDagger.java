package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class GoldDagger extends AphDaggerToolItem {
    public GoldDagger() {
        super(300);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);

        this.attackDamage.setBaseValue(26.0F).setUpgradedValue(1.0F, 94.0F);

        this.attackRange.setBaseValue(38);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, boolean shouldDrop) {
        return new DaggerProjectile.GoldDaggerProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                200, projectileRange(),
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