package aphorea.items.armor.Swamp;

import aphorea.items.vanillaitemtypes.armor.AphChestArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SwampChestplate extends AphChestArmorItem {
    public SwampChestplate() {
        super(5, 400, Rarity.COMMON, "swampchest", "swamparms");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.1F), new ModifierValue<>(BuffModifiers.MAX_RESILIENCE_FLAT, 2));
    }
}
