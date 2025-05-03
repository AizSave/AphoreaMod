package aphorea.items.armor.Spinel;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class SpinelHelmet extends AphSetHelmetArmorItem {
    public SpinelHelmet() {
        super(24, DamageTypeRegistry.MELEE, 1300, Rarity.UNCOMMON, "spinelhelmet", "spinelchestplate", "spinelboots", "spinelhelmetsetbonus");
        this.hairDrawOptions = HairDrawMode.NO_HEAD;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.3F), new ModifierValue<>(BuffModifiers.SPEED, -0.05F), new ModifierValue<>(BuffModifiers.ALL_DAMAGE, -0.1F), new ModifierValue<>(BuffModifiers.BLINDNESS, 0.4F));
    }
}
