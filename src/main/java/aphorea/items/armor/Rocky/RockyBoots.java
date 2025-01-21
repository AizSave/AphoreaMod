package aphorea.items.armor.Rocky;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class RockyBoots extends AphBootsArmorItem {
    public RockyBoots() {
        super(6, 250, Rarity.COMMON, "rockyboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(BuffModifiers.SPEED, -0.06F));
    }
}
