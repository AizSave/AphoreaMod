package aphorea.items.armor.Spinel;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SpinelBoots extends AphBootsArmorItem {
    public SpinelBoots() {
        super(14, 1300, Rarity.UNCOMMON, "spinelboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.1F), new ModifierValue<>(BuffModifiers.SPEED, -0.03F));
    }
}
