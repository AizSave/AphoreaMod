package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphSimplePotionItem;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

public class LowdsPotion extends AphSimplePotionItem {
    public LowdsPotion() {
        super(100, Rarity.COMMON, "lowdspoison", 300, "lowdspotion");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }
}
