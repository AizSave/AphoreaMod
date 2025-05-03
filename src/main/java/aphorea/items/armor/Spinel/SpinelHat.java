package aphorea.items.armor.Spinel;

import aphorea.items.vanillaitemtypes.armor.AphSetHelmetArmorItem;
import aphorea.registry.AphModifiers;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.logging.Level;

public class SpinelHat extends AphSetHelmetArmorItem {
    public SpinelHat() {
        super(4, DamageTypeRegistry.MAGIC, 1300, Rarity.UNCOMMON, "spinelhat", "spinelchestplate", "spinelboots", "spinelhatsetbonus");
        this.facialFeatureDrawOptions = FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
    }

    @Override
    public DrawOptions getArmorDrawOptions(InventoryItem item, necesse.level.maps.Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getArmorTexture(item, level, player, headItem, chestItem, feetItem);
        Color col = this.getDrawColor(item, player);
        return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes + 32).colorLight(col, light).alpha(alpha).size(width + 32, height + 32).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX - 16, drawY - 16);
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<>(AphModifiers.MAGIC_HEALING, 0.1F));
    }
}
