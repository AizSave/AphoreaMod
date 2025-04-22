package aphorea.items.tools.weapons.summoner;

import aphorea.items.vanillaitemtypes.weapons.AphSummonToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.summonToolItem.SkeletonStaffToolItem;
import necesse.level.maps.Level;

public class InfectedWoodStaff extends AphSummonToolItem {
    public InfectedWoodStaff() {
        super("livingsapling", FollowPosition.WALK_CLOSE, 0.5F, 400);
        this.rarity = Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(8).setUpgradedValue(1.0F, 20);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "livingsapling"));
        return tooltips;
    }

    @Override
    public void runServerSummon(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        super.runServerSummon(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        super.runServerSummon(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }
}