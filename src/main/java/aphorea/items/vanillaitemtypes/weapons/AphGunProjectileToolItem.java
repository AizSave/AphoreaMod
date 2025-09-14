package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;

import java.util.LinkedHashSet;

public class AphGunProjectileToolItem extends GunProjectileToolItem {
    public AphGunProjectileToolItem(String ammoStringID, int enchantCost) {
        super(ammoStringID, enchantCost);
    }

    public AphGunProjectileToolItem(LinkedHashSet<String> ammoTypes, int enchantCost) {
        super(ammoTypes, enchantCost);
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
