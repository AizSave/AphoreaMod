package aphorea.items.armor.Gold;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import necesse.engine.registries.DamageTypeRegistry;

public class GoldHat extends AphSetHelmetArmorItem {
    public GoldHat() {
        super(1, DamageTypeRegistry.RANGED, 500, Rarity.COMMON, "goldhat", "goldchestplate", "goldboots", "goldhatsetbonus");
    }
}
