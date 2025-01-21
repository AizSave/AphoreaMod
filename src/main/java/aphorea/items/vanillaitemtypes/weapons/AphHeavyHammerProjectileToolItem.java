package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.HeavyHammerProjectileToolItem;

abstract public class AphHeavyHammerProjectileToolItem extends HeavyHammerProjectileToolItem {
    public AphHeavyHammerProjectileToolItem(int enchantCost) {
        super();
        this.enchantCost.setBaseValue(enchantCost).setUpgradedValue(1.0F, 2000);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
