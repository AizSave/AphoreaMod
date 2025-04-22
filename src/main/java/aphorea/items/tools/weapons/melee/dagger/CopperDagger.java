package aphorea.items.tools.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;

public class CopperDagger extends AphDaggerToolItem {
    public CopperDagger() {
        super(200);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);

        this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 90.0F);

        this.attackRange.setBaseValue(40);
        this.knockback.setBaseValue(25);
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.CopperDaggerProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                100 * throwingVelocity, projectileRange(),
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
        return AphColors.copper;
    }
}
