package aphorea.buffs.Trinkets.Periapt.Summoner;

import aphorea.buffs.Trinkets.AphSummoningTrinketBuff;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class InfectedPeriaptBuff extends AphSummoningTrinketBuff {

    public InfectedPeriaptBuff() {
        super("unstableperiapt", "livingsapling", 2, new GameDamage(DamageTypeRegistry.SUMMON, 8));
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "infectedperiapt"));
        tooltips.add(Localization.translate("itemtooltip", "livingsapling"));
        return tooltips;
    }
}