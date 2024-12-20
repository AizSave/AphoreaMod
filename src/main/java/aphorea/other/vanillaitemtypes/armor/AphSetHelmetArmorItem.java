package aphorea.other.vanillaitemtypes.armor;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

abstract public class AphSetHelmetArmorItem extends SetHelmetArmorItem {
    public AphSetHelmetArmorItem(int armorValue, DamageType damageType, int enchantCost, Rarity rarity, String textureName, String setChestStringID, String setBootsStringID, String buffType) {
        super(armorValue, damageType, enchantCost, rarity, textureName, setChestStringID, setBootsStringID, buffType);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
