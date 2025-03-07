package aphorea.items.weapons.summoner;

import aphorea.items.vanillaitemtypes.weapons.AphSummonToolItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class VolatileGelStaff extends AphSummonToolItem {
    public VolatileGelStaff() {
        super("volatilegelslime", FollowPosition.WALK_CLOSE, 1.0F, 400);
        this.summonType = "summonedmobtemp";
        this.rarity = Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(80.0F).setUpgradedValue(1.0F, 160.0F);
        this.manaCost.setBaseValue(5).setUpgradedValue(1, 5);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob player, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, mapContent);
        consumeMana(player, item);
        return item;
    }
}
