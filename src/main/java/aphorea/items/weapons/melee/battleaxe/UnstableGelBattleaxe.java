package aphorea.items.weapons.melee.battleaxe;

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

public class UnstableGelBattleaxe extends AphBattleaxeToolItem {

    public UnstableGelBattleaxe() {
        super(500, getChargeLevel(2000, AphColors.unstableGel), getChargeLevel(1400, AphColors.unstableGel));
        rarity = Rarity.COMMON;
        attackDamage.setBaseValue(120)
                .setUpgradedValue(1, 300);
        attackRange.setBaseValue(90);
        knockback.setBaseValue(150);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff3"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 3000, null), true);
    }
}
