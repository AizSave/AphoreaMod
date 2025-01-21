package aphorea.items.armor.Swamp;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SwampBoots extends AphBootsArmorItem {
    public SwampBoots() {
        super(3, 400, Rarity.COMMON, "swampboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.05F), new ModifierValue<>(BuffModifiers.SPEED, 0.1F));
    }
}
