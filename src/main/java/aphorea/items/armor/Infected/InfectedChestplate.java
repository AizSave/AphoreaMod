package aphorea.items.armor.Infected;

import aphorea.items.vanillaitemtypes.armor.AphChestArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class InfectedChestplate extends AphChestArmorItem {
    public InfectedChestplate() {
        super(18, 1300, Rarity.UNCOMMON, "infectedchest", "infectedarms");
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING_RECEIVED, 0.1F), new ModifierValue<>(BuffModifiers.SUMMONS_SPEED, 0.05F), new ModifierValue<>(BuffModifiers.SUMMON_ATTACK_SPEED, 0.05F));
    }
}
