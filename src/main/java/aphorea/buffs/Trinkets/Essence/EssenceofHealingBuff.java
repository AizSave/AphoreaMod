package aphorea.buffs.Trinkets.Essence;

import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.util.LinkedList;

public class EssenceofHealingBuff extends TrinketBuff {
    public EssenceofHealingBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        new ModifierValue<>(BuffModifiers.LIFE_ESSENCE_DURATION, -0.8F).max(-0.5F).apply(buff);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "essenceofhealing", "percent", 10));
        tooltips.add(Localization.translate("itemtooltip", "essenceofhealing2", "flat", 1));
        tooltips.add(Localization.translate("itemtooltip", "essenceofhealing3"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        updateBuff(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        updateBuff(buff);
    }

    int currentEssences = 0;

    public void updateBuff(ActiveBuff buff) {
        PlayerMob player = (PlayerMob) buff.owner;
        if (player.buffManager.hasBuff(BuffRegistry.LIFE_ESSENCE)) {
            int lifeEssences = player.buffManager.getBuff(BuffRegistry.LIFE_ESSENCE).getStacks() / 15;
            if (lifeEssences != currentEssences) {
                buff.setModifier(AphModifiers.MAGIC_HEALING, lifeEssences * 0.1F);
                if ((lifeEssences / 2) != (currentEssences / 2)) {
                    buff.setModifier(AphModifiers.MAGIC_HEALING_FLAT, lifeEssences / 2);
                }
                currentEssences = lifeEssences;
            }
        }
    }
}
