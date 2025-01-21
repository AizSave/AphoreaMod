package aphorea.items.armor.Witch;

import aphorea.items.vanillaitemtypes.armor.AphChestArmorItem;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class MagicalSuit extends AphChestArmorItem {
    public MagicalSuit() {
        super(2, 300, Rarity.COMMON, "magicalsuit", "magicalsuitarms");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(BuffModifiers.MAGIC_DAMAGE, 0.15F));
    }
}
