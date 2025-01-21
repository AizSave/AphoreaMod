package aphorea.items.weapons.summoner;

import aphorea.items.vanillaitemtypes.weapons.AphSummonToolItem;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.FollowPosition;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
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
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
        consumeMana(player, item);
        return item;
    }
}
