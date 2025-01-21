package aphorea.items.vanillaitemtypes;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

abstract public class AphSimplePotionItem extends SimplePotionItem {
    public AphSimplePotionItem(int stackSize, Rarity rarity, String buffStringID, int buffDurationSeconds, String... tooltipKeys) {
        super(stackSize, rarity, buffStringID, buffDurationSeconds, tooltipKeys);
    }

    public AphSimplePotionItem(int stackSize, Rarity rarity, String buffStringID, int buffDurationSeconds, GameMessage... tooltips) {
        super(stackSize, rarity, buffStringID, buffDurationSeconds, tooltips);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
