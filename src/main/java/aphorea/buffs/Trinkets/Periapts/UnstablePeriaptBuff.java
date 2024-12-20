package aphorea.buffs.Trinkets.Periapts;

import aphorea.other.buffs.trinkets.AphSummoningTrinketBuff;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class UnstablePeriaptBuff extends AphSummoningTrinketBuff {

    public UnstablePeriaptBuff() {
        super("unstableperiapt", "babyunstablegelslime", 2, new GameDamage(DamageTypeRegistry.SUMMON, 6));
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);

        if (buff.owner.buffManager.hasBuff(AphBuffs.STICKY)) {
            buff.owner.buffManager.removeBuff(AphBuffs.STICKY, true);
        }
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "unstableperiapt"));
        tooltips.add(Localization.translate("itemtooltip", "unstableperiapt2"));
        return tooltips;
    }
}