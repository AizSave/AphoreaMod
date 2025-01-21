package aphorea.items.weapons.range.blowgun;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem;

abstract public class AphBlowgunToolItem extends SeedGunProjectileToolItem {
    public AphBlowgunToolItem(int enchantCost) {
        super();
        this.enchantCost.setBaseValue(enchantCost).setUpgradedValue(1.0F, 2000);
        this.keyWords.add("blowgun");
        this.keyWords.remove("gun");
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "blowgun");
    }
}
