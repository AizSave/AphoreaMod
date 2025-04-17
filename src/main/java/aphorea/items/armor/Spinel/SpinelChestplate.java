package aphorea.items.armor.Spinel;

import aphorea.items.vanillaitemtypes.armor.AphChestArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SpinelChestplate extends AphChestArmorItem {
    public SpinelChestplate() {
        super(20, 1300, Rarity.UNCOMMON, "spinelchest", "spinelarms");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.2F), new ModifierValue<>(BuffModifiers.SPEED, -0.03F));
    }
}
