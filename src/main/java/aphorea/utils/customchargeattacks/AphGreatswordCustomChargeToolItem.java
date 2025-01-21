package aphorea.utils.customchargeattacks;

import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

public class AphGreatswordCustomChargeToolItem extends AphGreatswordToolItem {
    public AphGreatswordCustomChargeToolItem(int enchantCost, GreatswordChargeLevel... chargeLevels) {
        super(enchantCost, chargeLevels);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.startAttackHandler(new AphCustomWidthGreatswordAttackHandler(this.width, player, slot, item, this, seed, x, y, this.chargeLevels));
        return item;
    }

    public void endChargeAttack(PlayerMob player, Point2D.Float dir, int charge) {
    }
}
