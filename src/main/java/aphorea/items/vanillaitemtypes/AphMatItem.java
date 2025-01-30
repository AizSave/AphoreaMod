package aphorea.items.vanillaitemtypes;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class AphMatItem extends MatItem {
    public AphMatItem(int stackSize, Rarity rarity) {
        super(stackSize, rarity);
    }

    public AphMatItem(int stackSize, String... globalIngredients) {
        super(stackSize, globalIngredients);
    }

    public AphMatItem(int stackSize, Rarity rarity, String... globalIngredients) {
        super(stackSize, rarity, globalIngredients);
    }

    public AphMatItem(int stackSize, Rarity rarity, String tooltipKey) {
        super(stackSize, rarity, tooltipKey);
    }

    public AphMatItem(int stackSize, Rarity rarity, String tooltipKey, String... globalIngredients) {
        super(stackSize, rarity, tooltipKey, globalIngredients);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
