package aphorea.items.trinkets;

import aphorea.utils.magichealing.AphMagicHealing;
import aphorea.items.vanillaitemtypes.AphShieldTrinketItem;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public class SwampShield extends AphShieldTrinketItem {
    public SwampShield() {
        super(Rarity.COMMON, 3, 0.5F, 6000, 0.2F, 50, 210.0F, 300);
    }

    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getExtraShieldTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "swampshield", "healing", AphMagicHealing.getMagicHealingToolTipPercent(perspective, perspective, 30, 0)));
        return tooltips;
    }

    public void onShieldHit(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        super.onShieldHit(item, mob, hitEvent);
        if (mob.isServer() && !hitEvent.wasPrevented) {
            Mob attackOwner = hitEvent.attacker != null ? hitEvent.attacker.getAttackOwner() : null;
            boolean hasOwnerInChain = hitEvent.attacker != null && hitEvent.attacker.isInAttackOwnerChain(mob);
            if (attackOwner != null && !hasOwnerInChain) {
                float finalDamageMultiplier = this.getShieldFinalDamageMultiplier(item, mob);
                if (finalDamageMultiplier > 0.0F) {
                    float healing = (hitEvent.damage / finalDamageMultiplier) * 0.3F;
                    AphMagicHealing.healMob(mob, mob, (int) healing, null, null);
                }
            }
        }

    }

}
