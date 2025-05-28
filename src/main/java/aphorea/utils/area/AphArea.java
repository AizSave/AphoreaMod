package aphorea.utils.area;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Line2D;
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

    public int buffDuration = 1000;
    public int debuffDuration = 1000;

    public GameDamage areaDamage;
    public int areaHealing;
    public String[] buffs;
    public String[] debuffs;

    public boolean directExecuteHealing = false;

    public boolean onlyVision = true;

    public boolean ignoreLight = true;

    ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

    public AphArea(float range, Color... colors) {
        this.range = range;
        this.colors = colors;
    }

    public AphArea(float range, float alpha, Color... colors) {
        this(range, adjustAlpha(alpha, colors));
    }

    @NotNull
    private static Color[] adjustAlpha(float alpha, @NotNull Color... colors) {
        Color[] adjustedColors = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            Color original = colors[i];
            adjustedColors[i] = new Color(original.getRed(), original.getGreen(), original.getBlue(), (int) (alpha * 255));
        }
        return adjustedColors;
    }

    public AphArea setDamageArea(GameDamage damage) {
        this.areaTypes.add(AphAreaType.DAMAGE);
        this.areaDamage = damage;

        return this;
    }

    public AphArea setHealingArea(int healing) {
        this.areaTypes.add(AphAreaType.HEALING);
        this.areaHealing = healing;

        return this;
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

    public AphArea setDirectExecuteHealing(boolean directExecuteHealing) {
        this.directExecuteHealing = directExecuteHealing;
        return this;
    }

    public AphArea setOnlyVision(boolean onlyVision) {
        this.onlyVision = onlyVision;
        return this;
    }

    public AphArea setIgnoreLight(boolean ignoreLight) {
        this.ignoreLight = ignoreLight;
        return this;
    }

    public GameDamage getDamage() {
        return areaDamage;
    }

    public int getHealing() {
        return areaHealing;
    }

    public void executeServer(Mob attacker, @NotNull Mob target, float x, float y, float modRange, InventoryItem item, ToolItem toolItem) {
        float distance = target.getDistance(x, y);
        if ((position == 0 == isCenter(attacker, target, distance)) || (inRange(distance, modRange) && inVision(target, x, y))) {
            if (this.areaTypes.contains(AphAreaType.DAMAGE) && target != attacker && canAreaAttack(attacker, target)) {
                target.isServerHit(areaDamage, target.x - attacker.x, target.y - attacker.y, 0, attacker);
            }
            if (this.areaTypes.contains(AphAreaType.HEALING) && (target == attacker || AphMagicHealing.canHealMob(attacker, target))) {
                if (directExecuteHealing) {
                    AphMagicHealing.healMobExecute(attacker, target, areaHealing, item, toolItem);
                } else {
                    AphMagicHealing.healMob(attacker, target, areaHealing, item, toolItem);
                }
            }
            if (attacker.isServer()) {
                if (this.areaTypes.contains(AphAreaType.BUFF) && (target == attacker || target.isSameTeam(attacker))) {
                    Arrays.stream(buffs).forEach(
                            buffID -> target.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff(buffID), target, buffDuration, attacker), true)
                    );
                }
                if (this.areaTypes.contains(AphAreaType.DEBUFF) && target != attacker && canAreaAttack(attacker, target)) {
                    Arrays.stream(debuffs).forEach(
                            debuffID -> target.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff(debuffID), target, debuffDuration, attacker), true)
                    );
                }
            }
        }
    }

    public static boolean isCenter(Mob attacker, Mob target, float distance) {
        return attacker == target && distance == 0;
    }

    public boolean inRange(float distance, float modRange) {
        return distance <= (range * modRange) && distance > (antRange * modRange);
    }

    public static boolean inVision(@NotNull Mob target, float x, float y) {
        return !target.getLevel().collides(new Line2D.Float(x, y, target.x, target.y), new CollisionFilter().projectileCollision());
    }

    public static boolean canAreaAttack(Mob attacker, @NotNull Mob target) {
        return target.canBeTargeted(attacker, attacker.isPlayer ? ((PlayerMob) attacker).getNetworkClient() : null);
    }

    public static int lateralBorderReduction = 10;

    public void showParticles(Level level, float x, float y, Color[] forcedColors, float rangeModifier, float borderParticleModifier, float innerParticleModifier, int particleTime) {
        int range = Math.round(this.range * rangeModifier);
        int antRange = Math.round(this.antRange * rangeModifier);

        float[] rays;
        if (onlyVision) {
            rays = getRays(level, x, y, range, new CollisionFilter().projectileCollision());
        } else {
            rays = getFullyRays(range);
        }

        for (int i = 0; i < rays.length; i++) {
            float rayDistance = rays[i];
            if (rayDistance > antRange && (colors != null || forcedColors != null)) {

                float trueRange = Math.min(range, rayDistance);
                float angle = (float) (2 * Math.PI * i / rays.length);

                if (GameRandom.globalRandom.getChance(0.25F * borderParticleModifier)) {
                    float dx = (float) Math.cos(angle) * trueRange;
                    float dy = (float) Math.sin(angle) * trueRange;
                    level.entityManager.addParticle(x + dx, y + dy, particleTypeSwitcher.next()).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05F, 0.1F)).color(getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0F, 3F), GameRandom.globalRandom.getFloatBetween(5F, 10F))
                            .ignoreLight(ignoreLight).lifeTime(particleTime);
                }

                float innerRange = trueRange;

                float neighbourRay = getNeighbourRay(i, rays);
                float neighbourRange = Math.max(antRange, Math.min(range, neighbourRay));

                if (neighbourRange < trueRange) {
                    int borderRange = (int) (trueRange - neighbourRange);
                    innerRange -= borderRange;

                    for (int j = 0; j < (borderRange / lateralBorderReduction); j++) {
                        if (GameRandom.globalRandom.getChance(borderParticleModifier)) {

                            float dx = (float) Math.cos(angle) * (neighbourRange + j * lateralBorderReduction);
                            float dy = (float) Math.sin(angle) * (neighbourRange + j * lateralBorderReduction);
                            level.entityManager.addParticle(x + dx, y + dy, particleTypeSwitcher.next()).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05F, 0.1F)).color(getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0F, 3F), GameRandom.globalRandom.getFloatBetween(5F, 10F))
                                    .ignoreLight(ignoreLight).lifeTime(particleTime);
                        }
                    }
                }
                if (innerRange > antRange && GameRandom.globalRandom.getChance(innerParticleModifier * ((innerRange - antRange) / 2000)) && 0.1F * innerRange + antRange < innerRange * 0.9F) {
                    float r = GameRandom.globalRandom.getFloatBetween(0, 1);
                    float d = (innerRange - antRange) * easeOutQuad(r) * 0.8F + 0.1F + antRange;
                    float dx = (float) Math.cos(angle) * d;
                    float dy = (float) Math.sin(angle) * d;

                    level.entityManager.addParticle(x + dx, y + dy, particleTypeSwitcher.next()).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getFloatBetween(0.05F, 0.1F)).color(getColor(forcedColors)).heightMoves(GameRandom.globalRandom.getFloatBetween(0F, 3F), GameRandom.globalRandom.getFloatBetween(5F, 10F))
                            .ignoreLight(ignoreLight).lifeTime(particleTime);
                }
            }
        }
    }

    public static float easeOutQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static float[] getRays(Level level, float x, float y, float range, CollisionFilter filter) {
        int raysCount = (int) (2 * Math.PI * range);
        float[] rays = new float[raysCount];
        for (int i = 0; i < raysCount; i++) {
            float angle = (float) (2 * Math.PI * i / raysCount);
            rays[i] = (float) GameUtils.castRay(level, x, y, Math.cos(angle) * range, Math.sin(angle) * range, range, 0, filter).totalDist;
        }
        return rays;
    }

    public static float[] getFullyRays(float range) {
        int raysCount = (int) (2 * Math.PI * range);
        float[] rays = new float[raysCount];
        Arrays.fill(rays, range);
        return rays;
    }

    public static float getNeighbourRay(int ray, float[] rays) {
        float antRay = rays[ray == 0 ? rays.length - 1 : ray - 1];
        float nextRay = rays[ray == rays.length - 1 ? 0 : ray + 1];
        return Math.min(antRay, nextRay);
    }

    public Color getColor(Color[] forcedColors) {
        return GameRandom.globalRandom.getOneOf(forcedColors != null ? forcedColors : colors);
    }
}
