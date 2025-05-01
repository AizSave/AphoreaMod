package aphorea.utils.area;

import necesse.entity.mobs.GameDamage;
import necesse.inventory.InventoryItem;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class AphFlatArea extends AphArea {
    public AphFlatArea(float range, Color... colors) {
        super(range, colors);
    }

    public AphFlatArea(float range, float alpha, Color... colors) {
        super(range, alpha, colors);
    }

    public AphFlatArea setDamageArea(GameDamage gameDamage) {
        this.areaTypes.add(AphAreaType.DAMAGE);
        this.areaDamage.setBaseValue(gameDamage.damage);
        this.damageType = gameDamage.type;
        this.armorPen = (int) gameDamage.armorPen;

        return this;
    }

    public AphFlatArea setDamageArea(float damage) {
        setDamageArea(areaDamage.setBaseValue(damage));
        return this;
    }

    public AphFlatArea setHealingArea(int healing) {
        setHealingArea(areaHealing.setBaseValue(healing));
        return this;
    }

    @Override
    public GameDamage getDamage(@Nullable InventoryItem item) {
        return new GameDamage(damageType, getBaseDamage(), armorPen, baseCritChance);
    }

    @Override
    public int getHealing(@Nullable InventoryItem item) {
        return areaHealing.getValue(0);
    }

}
