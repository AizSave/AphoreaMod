package aphorea.items.weapons.range.sling;

import aphorea.projectiles.toolitem.FrozenSlingStoneProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class FrozenSling extends AphSlingToolItem {
    public FrozenSling() {
        super(100);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(30.0F).setUpgradedValue(1.0F, 150.0F);
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new FrozenSlingStoneProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                getProjectileVelocity(item, attackerMob),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, attackerMob)
        );
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "frozensling"));
        return tooltips;
    }
}
