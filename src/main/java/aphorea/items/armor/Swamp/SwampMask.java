package aphorea.items.armor.Swamp;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SwampMask extends AphSetHelmetArmorItem {
    public SwampMask() {
        super(4, DamageTypeRegistry.MELEE, 400, Rarity.COMMON, "swampmask", "swampchestplate", "swampboots", "swampmasksetbonus");
        this.hairDrawOptions = HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.05F), new ModifierValue<>(BuffModifiers.MAX_RESILIENCE_FLAT, 3));
    }
}
