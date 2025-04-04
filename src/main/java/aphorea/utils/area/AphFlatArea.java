package aphorea.utils.area;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AphFlatArea extends AphArea {
    public AphFlatArea(float range, Color... colors) {
        super(range, colors);
    }

    public AphFlatArea(float range, float alpha, Color... colors) {
        super(range, alpha, colors);
    }

    public AphArea setDamageArea(GameDamage gameDamage) {
        this.areaTypes.add(AphAreaType.DAMAGE);
        this.areaDamage.setBaseValue(gameDamage.damage);
        this.damageType = gameDamage.type;
        this.armorPen = (int) gameDamage.armorPen;

        return this;
    }

    public AphArea setDamageArea(float damage) {
        return setDamageArea(areaDamage.setBaseValue(damage));
    }

    public AphArea setHealingArea(int healing) {
        return setHealingArea(areaHealing.setBaseValue(healing));
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
