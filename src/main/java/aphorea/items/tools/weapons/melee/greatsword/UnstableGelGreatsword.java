package aphorea.items.tools.weapons.melee.greatsword;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class UnstableGelGreatsword extends AphGreatswordSecondarySpinToolItem {

    public UnstableGelGreatsword() {
        super(400, 300, getThreeChargeLevels(500, 600, 700, AphColors.unstableGel_very_light, AphColors.unstableGel_light, AphColors.unstableGel), AphColors.unstableGel);
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(50)
                .setUpgradedValue(1, 140);
        attackRange.setBaseValue(110);
        knockback.setBaseValue(50);

        width = 26.0F;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff2"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 2000, attacker), true);
    }

}
