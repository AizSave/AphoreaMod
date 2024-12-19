package aphorea.other.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;

abstract public class AphGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
    public AphGreatbowProjectileToolItem(int enchantCost) {
        super(enchantCost);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
