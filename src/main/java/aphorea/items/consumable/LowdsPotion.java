package aphorea.items.consumable;

import aphorea.other.vanillaitemtypes.AphSimplePotionItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

import java.awt.*;

public class LowdsPotion extends AphSimplePotionItem {
    public LowdsPotion() {
        super(100, Rarity.COMMON, "lowdspoison", 300, "lowdspotion");
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "buffpotions");
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        super.onPlace(level, x, y, player, item, contentReader);
        UniqueFloatText text = new UniqueFloatText(player.getX(), player.getY() - 20, Localization.translate("message", "itoldyou"), (new FontOptions(16)).outline().color(new Color(200, 100, 100)), "mountfail") {
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
