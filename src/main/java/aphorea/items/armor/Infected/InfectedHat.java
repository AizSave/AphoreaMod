package aphorea.items.armor.Infected;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class InfectedHat extends AphSetHelmetArmorItem {
    public InfectedHat() {
        super(12, DamageTypeRegistry.MELEE, 1300, Rarity.UNCOMMON, "infectedhat", "infectedchestplate", "infectedboots", "infectedsetbonus");
        this.hairDrawOptions = HairDrawMode.NO_HAIR;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.1F), new ModifierValue<>(BuffModifiers.MAX_SUMMONS, 1));
    }
}
