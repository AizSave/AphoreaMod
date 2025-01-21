package aphorea.objects;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CraftingStationObject;

import java.awt.*;

abstract public class AphCraftingStationObject extends CraftingStationObject {

    public AphCraftingStationObject(Rectangle collision) {
        super(collision);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        addItemToolTips(tooltips, item, perspective);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public void addItemToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective) {
    }
}
