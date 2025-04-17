package aphorea.items.tools.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class AphCutlassSaber extends AphSaberToolItem {

    public AphCutlassSaber() {
        super(1150, true);
        rarity = Rarity.RARE;
        attackDamage.setBaseValue(40)
                .setUpgradedValue(1, 80);
        this.attackRange.setBaseValue(65);
        this.knockback.setBaseValue(80);

        this.attackXOffset = 8;
        this.attackYOffset = 8;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;

        chargeAnimTime.setBaseValue(500);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.GoldAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                400 * powerPercent,
                (int) (500 * powerPercent),
                this.getAttackDamage(item).modDamage(powerPercent * 0.5F),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate("global", "aphorearework"));
        return tooltips;
    }
}
