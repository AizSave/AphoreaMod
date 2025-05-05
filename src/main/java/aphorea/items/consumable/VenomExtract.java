package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphSimplePotionItem;
import necesse.inventory.item.ItemCategory;

public class VenomExtract extends AphSimplePotionItem {
    public VenomExtract() {
        super(100, Rarity.COMMON, "venomextractbuff", 300, "venomextract");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}
