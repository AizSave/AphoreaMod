package aphorea.items.armor.Witch;

import aphorea.registry.AphModifiers;
import aphorea.other.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class PinkWitchHat extends AphSetHelmetArmorItem {
    public PinkWitchHat() {
        super(1, DamageTypeRegistry.MAGIC, 200, Rarity.COMMON, "pinkwitchhat", "magicalsuit", "magicalboots", "pinkwitchsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.2F), new ModifierValue<>(BuffModifiers.MAX_MANA_FLAT, 50));
    }

}
