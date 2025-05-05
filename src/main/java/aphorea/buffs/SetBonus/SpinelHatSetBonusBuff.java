package aphorea.buffs.SetBonus;

import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
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

public class SpinelHatSetBonusBuff extends SetBonusBuff {
    float savedAmount;

    public SpinelHatSetBonusBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "spinelhatsetbonus", "healing", AphMagicHealing.getMagicHealingToolTipPercent(ab.owner, ab.owner, 0.01F))));
        return tooltips;
    }

    public AphAreaList getAreaList(int healing) {
        return new AphAreaList(
                new AphArea(100, 0.3F, AphColors.green)
                        .setHealingArea(healing)
                        .setDirectExecuteHealing(true)
        );
    }

    @Override
    public void onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event) {
        super.onBeforeAttacked(buff, event);
        if (!event.isPrevented() && event.damage.type == DamageTypeRegistry.MAGIC && event.damage.damage > 0 && event.target != null && event.target.isHostile) {
            float healing = event.damage.damage * 0.01F * AphMagicHealing.getMagicHealingMod(buff.owner, buff.owner, null, null) + savedAmount;
            if (healing < 1) {
                savedAmount = healing;
            } else {
                int realHealing = (int) healing;
                savedAmount = healing - realHealing;
                getAreaList(realHealing).execute(buff.owner, true);
            }
        }
    }
}
