package aphorea.items.armor.Swamp;

import aphorea.other.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SwampHood extends AphSetHelmetArmorItem {
    public SwampHood() {
        super(2, DamageTypeRegistry.MAGIC, 400, Rarity.COMMON, "swamphood", "swampchestplate", "swampboots", "swamphoodsetbonus");
        this.hairDrawOptions = HairDrawMode.NO_HAIR;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.1F));
    }
}
