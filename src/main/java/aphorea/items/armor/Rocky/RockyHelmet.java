package aphorea.items.armor.Rocky;

import aphorea.other.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;

public class RockyHelmet extends AphSetHelmetArmorItem {
    public RockyHelmet() {
        super(7, DamageTypeRegistry.MELEE, 250, Rarity.COMMON, "rockyhelmet", "rockychestplate", "rockyboots", "rockysetbonus");
        this.hairDrawOptions = HairDrawMode.NO_HEAD;
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(BuffModifiers.SPEED, -0.07F));
    }
}
