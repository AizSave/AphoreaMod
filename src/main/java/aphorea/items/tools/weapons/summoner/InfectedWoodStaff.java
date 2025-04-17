package aphorea.items.tools.weapons.summoner;

import aphorea.items.vanillaitemtypes.weapons.AphSummonToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public class InfectedWoodStaff extends AphSummonToolItem {
    public InfectedWoodStaff() {
        super("livingsapling", FollowPosition.WALK_CLOSE, 1.0F, 400);
        this.rarity = Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(5).setUpgradedValue(1.0F, 10);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "livingsapling"));
        return tooltips;
    }
}
