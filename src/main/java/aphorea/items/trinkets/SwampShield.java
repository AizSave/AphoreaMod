package aphorea.items.trinkets;

import aphorea.items.vanillaitemtypes.AphShieldTrinketItem;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketHitMob;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public class SwampShield extends AphShieldTrinketItem {
    public SwampShield() {
        super(Rarity.COMMON, 2, 0.5F, 6000, 0.2F, 50, 240.0F, 300, true);
        isPerfectBlocker = true;
    }

    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getExtraShieldTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "swampshield"));
        tooltips.add(Localization.translate("itemtooltip", "swampshield2", "healing", AphMagicHealing.getMagicHealingToolTipPercent(perspective, perspective, 0.05F)));
        return tooltips;
    }

    @Override
    public void onPerfectBlock(Mob mob) {
        super.onPerfectBlock(mob);
        if(mob.isServer()) {
            float healing = mob.getMaxHealth() * 0.05F * AphMagicHealing.getMagicHealingMod(mob, mob, null, null);
            AphMagicHealing.healMobExecute(mob, mob, (int) healing);
        }
    }
}
