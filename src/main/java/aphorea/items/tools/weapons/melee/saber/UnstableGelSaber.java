package aphorea.items.tools.weapons.melee.saber;

import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class UnstableGelSaber extends AphSaberToolItem {

    public UnstableGelSaber() {
        super(800, true);
        rarity = Rarity.EPIC;
        attackDamage.setBaseValue(26)
                .setUpgradedValue(1, 80);
        attackRange.setBaseValue(80);
        attackRange.setBaseValue(80);
        knockback.setBaseValue(75);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff1"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, attacker), true);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.UnstableGelAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                300 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }
}
