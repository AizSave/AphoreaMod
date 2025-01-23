package aphorea.buffs.SetBonus;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.inventory.item.ItemStatTip;

import java.util.LinkedList;

public class RockySetBonusBuff extends SetBonusBuff {
    public RockySetBonusBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SPEED, -0.1F);
        buff.addModifier(BuffModifiers.ARMOR_FLAT, 10);
        buff.addModifier(BuffModifiers.MAX_RESILIENCE_FLAT, 10);
        buff.addModifier(BuffModifiers.RESILIENCE_DECAY, -1F);
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}