package aphorea.items.tools.weapons.melee.glaive;

import aphorea.items.vanillaitemtypes.weapons.AphGlaiveToolItem;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class WoodenRod extends AphGlaiveToolItem {

    int attackCount = 0;

    public WoodenRod() {
        super(200);
        rarity = Rarity.NORMAL;
        attackAnimTime.setBaseValue(500);
        attackDamage.setBaseValue(5)
                .setUpgradedValue(1, 60);
        attackRange.setBaseValue(220);
        knockback.setBaseValue(40);

        attackXOffset = 86;
        attackYOffset = 86;
        width = 14.0F;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "woodenrod", "healing", AphMagicHealing.getMagicHealing(perspective, perspective, 3)));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);

        attackCount++;
        if (attackCount >= 10) {
            attackCount = 0;
            AphMagicHealing.healMob(attacker, attacker, 3, item, this);
        }
    }
}
