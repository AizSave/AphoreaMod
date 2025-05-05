package aphorea.buffs.SetBonus;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class SpinelHelmetSetBonusBuff extends SetBonusBuff {
    float savedAmount;

    public SpinelHelmetSetBonusBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "spinelhelmetsetbonus", "healing", AphMagicHealing.getMagicHealingToolTipPercent(ab.owner, ab.owner, 0.01F))));
        return tooltips;
    }

    @Override
    public void onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event) {
        super.onBeforeAttacked(buff, event);
        if (!event.isPrevented() && event.damage.type == DamageTypeRegistry.MELEE && event.damage.damage > 0 && event.target != null && event.target.isHostile) {
            float healing = event.damage.damage * 0.01F * AphMagicHealing.getMagicHealingMod(buff.owner, buff.owner, null, null) + savedAmount;
            if (healing < 1) {
                savedAmount = healing;
            } else {
                int realHealing = (int) healing;
                savedAmount = healing - realHealing;
                AphMagicHealing.healMobExecute(buff.owner, buff.owner, realHealing);
            }
        }
    }
}
