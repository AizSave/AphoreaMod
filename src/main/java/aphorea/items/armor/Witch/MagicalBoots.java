package aphorea.items.armor.Witch;

import aphorea.items.vanillaitemtypes.armor.AphBootsArmorItem;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class MagicalBoots extends AphBootsArmorItem {
    public MagicalBoots() {
        super(1, 250, Rarity.COMMON, "magicalboots");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(BuffModifiers.MAGIC_DAMAGE, 0.05F));
    }
}
