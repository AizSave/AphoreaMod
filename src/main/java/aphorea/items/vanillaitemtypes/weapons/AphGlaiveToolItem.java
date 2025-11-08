package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.glaiveToolItem.GlaiveToolItem;
import necesse.inventory.lootTable.presets.GlaiveWeaponsLootTable;

abstract public class AphGlaiveToolItem extends GlaiveToolItem {
    public AphGlaiveToolItem(int enchantCost) {
        super(enchantCost, GlaiveWeaponsLootTable.glaiveWeapons);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
