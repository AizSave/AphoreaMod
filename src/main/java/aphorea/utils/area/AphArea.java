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
import necesse.level.maps.Level;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AphArea {
    public float range;
    public float antRange;
    public float currentRange;
    public Color[] colors;
    public int position;
    public Set<AphAreaType> areaTypes = new HashSet<>();
    public DamageType damageType;
    float baseCritChance = 0;
    int armorPen = 0;

    int buffDuration = 1000;
    int debuffDuration = 1000;

    public FloatUpgradeValue areaDamage;
    public IntUpgradeValue areaHealing;
    public String[] buffs;
    public String[] debuffs;

    public AphArea(float range, Color... colors) {
        this.range = range;
        this.colors = colors;
        this.damageType = DamageTypeRegistry.NORMAL;
    }

    public AphArea(float range, float alpha, Color... colors) {
        this(range, adjustAlpha(alpha, colors));
    }

    private static Color[] adjustAlpha(float alpha, Color... colors) {
        Color[] adjustedColors = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            Color original = colors[i];
            adjustedColors[i] = new Color(original.getRed(), original.getGreen(), original.getBlue(), (int) (alpha * 255));
        }
        return adjustedColors;
    }

    public AphArea setDamageArea(FloatUpgradeValue damage) {
        this.areaTypes.add(AphAreaType.DAMAGE);
        this.areaDamage = damage;

        return this;
    }

    public AphArea setDamageArea(float damage) {
        return setDamageArea(new FloatUpgradeValue(0, 0.2F).setBaseValue(damage));
    }

    public AphArea setDamageArea(float damage, float tier1Damage) {
        return setDamageArea(new FloatUpgradeValue(0, 0.2F).setBaseValue(damage).setUpgradedValue(1, tier1Damage));
    }

    public AphArea setBaseCritChance(int baseCritChance) {
        this.baseCritChance = baseCritChance;
        return this;
    }

    public AphArea setArmorPen(int armorPen) {
        this.armorPen = armorPen;
        return this;
    }

    public AphArea setHealingArea(IntUpgradeValue healing) {
        this.areaTypes.add(AphAreaType.HEALING);
        this.areaHealing = healing;

        return this;
    }

    public AphArea setHealingArea(int healing) {
        return setHealingArea(new IntUpgradeValue(0, 0.2F).setBaseValue(healing));
    }

    public AphArea setHealingArea(int healing, int tier1Healing) {
        return setHealingArea(new IntUpgradeValue(0, 0.2F).setBaseValue(healing).setUpgradedValue(1, tier1Healing));
    }

    public AphArea setBuffArea(int duration, String... buffs) {
        this.areaTypes.add(AphAreaType.BUFF);
        this.buffs = buffs;
        this.buffDuration = duration;

        return this;
    }

    public AphArea setDebuffArea(int duration, String... debuffs) {
        this.areaTypes.add(AphAreaType.DEBUFF);
        this.debuffs = debuffs;
        this.debuffDuration = duration;

        return this;
    }

    public void showAreaParticles(Level level, float x, float y, AphAreaList areaList, Color[] forcedColors, float rangeModifier, float borderParticleModifier, float innerParticleModifier, int particleTime) {
        int range = Math.round(this.range * rangeModifier);
        int antRange = Math.round(this.antRange * rangeModifier);
        if (colors != null || forcedColors != null) {
            float initialParticleCount = (float) (360 * range) / 400;
            float initialAnteriorParticleCount = antRange == 0 ? 0 : (float) (360 * antRange) / 400;

            int particles = Math.round(initialParticleCount * borderParticleModifier);
            int innerParticles = Math.round((initialParticleCount - initialAnteriorParticleCount) * innerParticleModifier);

            for (int i = 0; i < particles; i++) {
                float angle = (float) i / particles * 360;
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) range;
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) range;
                level.entityManager.addParticle(x + dx, y + dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL).next()).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05F, 0.1F)).color(getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0F, 3F), GameRandom.globalRandom.getFloatBetween(5F, 10F)).lifeTime(particleTime);
            }

            if (borderParticleModifier <= 0 || 0.1F * range + antRange < range * 0.9F) {
                for (int i = 0; i < innerParticles; i++) {
                    float angle = GameRandom.globalRandom.getIntBetween(0, 359);
                    float d = GameRandom.globalRandom.getFloatBetween(borderParticleModifier <= 0 ? antRange : 0.1F * range + antRange, borderParticleModifier <= 0 ? range : 0.9F * range);
                    float dx = (float) Math.sin(Math.toRadians(angle)) * d;
                    float dy = (float) Math.cos(Math.toRadians(angle)) * d;

                    level.entityManager.addParticle(x + dx, y + dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL).next()).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05F, 0.1F)).color(getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0F, 3F), GameRandom.globalRandom.getFloatBetween(5F, 10F)).lifeTime(particleTime);
                }
            }

            if (position > 0) {
                AphArea antArea = areaList.areas[position - 1];

                antArea.showAreaParticles(level, x, y, areaList, forcedColors, rangeModifier, borderParticleModifier, innerParticleModifier, particleTime);
            }

        }
    }

    public Color getColor(Color[] forcedColors) {
        return GameRandom.globalRandom.getOneOf(forcedColors != null ? forcedColors : colors);
    }

    public float getBaseDamage() {
        return areaDamage.getValue(0);
    }

    public GameDamage getDamage(@Nullable InventoryItem item) {
        return new GameDamage(damageType, (item == null || !(item.item instanceof ToolItem)) ? getBaseDamage() : areaDamage.getValue(item.item.getUpgradeTier(item)), armorPen, baseCritChance);
    }

    public int getHealing(@Nullable InventoryItem item) {
        return (item == null || !(item.item instanceof ToolItem)) ? areaHealing.getValue(0) : areaHealing.getValue(item.item.getUpgradeTier(item));
    }

    public void execute(Mob attacker, Mob target, float modRange, int x, int y, boolean centerIsAttacker, InventoryItem item, ToolItem toolItem) {
        float distanceToTarget = target.getDistance(attacker);
        boolean reachMob = centerIsAttacker
                ? ((attacker == target && position == 0) || (distanceToTarget <= (range * modRange) && distanceToTarget > (antRange * modRange)))
                : (target.getDistance(x, y) <= (range * modRange) && target.getDistance(x, y) > (antRange * modRange));

        if (reachMob) {
            if (this.areaTypes.contains(AphAreaType.DAMAGE) && target != attacker && canAreaAttack(attacker, target)) {
                target.isServerHit(getDamage(item), target.x - attacker.x, target.y - attacker.y, 0, attacker);
            }
            if (this.areaTypes.contains(AphAreaType.HEALING) && (target == attacker || AphMagicHealing.canHealMob(attacker, target))) {
                AphMagicHealing.healMob(attacker, target, this.getHealing(item), item, toolItem);
            }
            if (attacker.isServer()) {
                if (this.areaTypes.contains(AphAreaType.BUFF) && target != attacker && target.isSameTeam(attacker)) {
                    Arrays.stream(buffs).forEach(
                            buffID -> {
                                ActiveBuff buff = new ActiveBuff(BuffRegistry.getBuff(buffID), target, buffDuration, attacker);
                                target.buffManager.addBuff(buff, true);
                            }
                    );
                }
                if (this.areaTypes.contains(AphAreaType.DEBUFF) && target != attacker && canAreaAttack(attacker, target)) {
                    Arrays.stream(debuffs).forEach(
                            debuffID -> {
                                ActiveBuff debuff = new ActiveBuff(BuffRegistry.getBuff(debuffID), target, debuffDuration, attacker);
                                target.buffManager.addBuff(debuff, true);
                            }
                    );
                }
            }
        }
    }

    public boolean canAreaAttack(Mob attacker, Mob target) {
        if (attacker instanceof PlayerMob) {
            return target.canBeTargeted(attacker, ((PlayerMob) attacker).getNetworkClient());
        } else {
            return target.canBeHit(attacker) && !attacker.isSameTeam(target);
        }
    }
}
