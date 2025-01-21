package aphorea.items.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
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

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
    }

    public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, boolean shouldDrop) {
        return new DaggerProjectile.CopperDaggerProjectile(level, player,
                player.x, player.y,
                x, y,
                100, 300,
                getAttackDamage(item),
                getKnockback(item, player),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.copper;
    }
}
