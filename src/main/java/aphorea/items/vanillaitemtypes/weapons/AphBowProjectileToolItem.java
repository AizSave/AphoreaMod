package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;

public class AphBowProjectileToolItem extends BowProjectileToolItem {
    public AphBowProjectileToolItem(int enchantCost) {
        super(enchantCost, BowWeaponsLootTable.bowWeapons);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
