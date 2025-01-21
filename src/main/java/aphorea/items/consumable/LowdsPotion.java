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

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        super.onPlace(level, x, y, player, item, contentReader);
        UniqueFloatText text = new UniqueFloatText(player.getX(), player.getY() - 20, Localization.translate("message", "lowdspotion"), (new FontOptions(16)).outline().color(AphColors.fail_message), "lowdspotion") {
            public int getAnchorX() {
                return player.getX();
            }

            public int getAnchorY() {
                return player.getY() - 20;
            }
        };
        player.getLevel().hudManager.addElement(text);
        return item;
    }
}
