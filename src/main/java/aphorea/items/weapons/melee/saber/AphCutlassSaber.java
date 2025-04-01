package aphorea.items.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class AphCutlassSaber extends AphSaberToolItem {

    public AphCutlassSaber() {
        super(1150, true);
        rarity = Rarity.RARE;
        attackDamage.setBaseValue(30)
                .setUpgradedValue(1, 70);
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
                this.getAttackDamage(item).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }
}