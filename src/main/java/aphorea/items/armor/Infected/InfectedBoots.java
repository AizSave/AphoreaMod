package aphorea.items.armor.Infected;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class InfectedBoots extends AphBootsArmorItem {
    public InfectedBoots() {
        super(12, 1300, Rarity.UNCOMMON, "infectedboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.1F), new ModifierValue<>(BuffModifiers.SPEED, 0.1F));
    }
}
