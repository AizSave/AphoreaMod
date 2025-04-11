package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphSimplePotionItem;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

public class VenomExtract extends AphSimplePotionItem {
    public VenomExtract() {
        super(100, Rarity.COMMON, "venomextract", 300, "venomextract");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}
