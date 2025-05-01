package aphorea.items.tools.weapons.melee.sword;

import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;

public class CryoKatana extends KatanaToolItem {
    public CryoKatana() {
        super(1900);
        this.rarity = Rarity.EPIC;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(80.0F).setUpgradedValue(1.0F, 80.0F);
        this.attackRange.setBaseValue(120);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(1.0F);
        this.maxDashStacks.setBaseValue(20);
        this.dashRange.setBaseValue(500);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FROSTBURN, target, 3000, attacker), true);
    }

    @Override
    public void showKatanaAttack(Level level, final AttackAnimMob mob, final int seed, final InventoryItem item) {
        level.entityManager.addLevelEventHidden(new SwordCleanSliceAttackEvent(mob, seed, 12, this) {
            Trail[] trails = null;
            public void tick(float angle, float currentAttackProgress) {
                int attackRange = CryoKatana.this.getAttackRange(item);
                Point2D.Float base = new Point2D.Float(mob.x, mob.y);
                int attackDir = mob.getDir();
                if (attackDir == 0) {
                    base.x += 8.0F;
                } else if (attackDir == 2) {
                    base.x -= 8.0F;
                }

                int minTrailRange = 60;
                int distancePerTrail = 5;
                boolean strictTrailAngles = item.getGndData().getBoolean("sliceDash");
                if (strictTrailAngles) {
                    attackRange -= 20;
                    minTrailRange -= 20;
                }
                angle = CryoKatana.this.getSwingDirection(item, mob).apply(currentAttackProgress);

                Point2D.Float dir = GameMath.getAngleDir(angle);
                int sliceDirOffset = CryoKatana.this.getAnimInverted(item) ? -90 : 90;
                if (attackDir == 3) {
                    sliceDirOffset = -sliceDirOffset;
                }

                Point2D.Float sliceDir = GameMath.getAngleDir(angle + (float) sliceDirOffset);
                int i;
                if (this.trails == null) {
                    i = strictTrailAngles ? 1000 : 500;
                    int trailCount = Math.max(1, (attackRange - minTrailRange - 10) / distancePerTrail);
                    this.trails = new Trail[trailCount];

                    for (int ix = 0; ix < this.trails.length; ++ix) {
                        Trail trail = new Trail(this.getVector(currentAttackProgress, attackRange, ix, distancePerTrail, base, dir, sliceDir), this.level, AphColors.ice, i);
                        this.trails[ix] = trail;
                        trail.removeOnFadeOut = false;
                        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
                        this.level.entityManager.addTrail(trail);
                    }
                } else {
                    for (i = 0; i < this.trails.length; ++i) {
                        if (strictTrailAngles) {
                            this.trails[i].addPointIfSameDirection(this.getVector(currentAttackProgress, attackRange, i, distancePerTrail, base, dir, sliceDir), 0.2F, 20.0F, 50.0F);
                        } else {
                            this.trails[i].addPoint(this.getVector(currentAttackProgress, attackRange, i, distancePerTrail, base, dir, sliceDir));
                        }
                    }
                }

            }

            public TrailVector getVector(float currentAttackProgress, int attackRange, int index, int distancePerTrail, Point2D.Float base, Point2D.Float dir, Point2D.Float sliceDir) {
                float thickness = GameMath.lerp((float) index / (float) (this.trails.length - 1), 25.0F, 10.0F);
                if (currentAttackProgress < 0.33F) {
                    thickness *= 3.0F * currentAttackProgress;
                } else if (currentAttackProgress > 0.66F) {
                    thickness *= 3.0F * (1.0F - currentAttackProgress);
                }

                int distanceOffset = attackRange - index * distancePerTrail;
                GameRandom random = (new GameRandom(seed)).nextSeeded(index + 5);
                float xOffset = random.getFloatOffset(0.0F, 10.0F);
                float yOffset = random.getFloatOffset(0.0F, 10.0F);
                Point2D.Float edgePos = new Point2D.Float(base.x + dir.x * (float) distanceOffset + xOffset, base.y + dir.y * (float) distanceOffset + yOffset);
                return new TrailVector(edgePos.x, edgePos.y, sliceDir.x, sliceDir.y, thickness, 0.0F);
            }

            public void onDispose() {
                super.onDispose();
                if (this.trails != null) {
                    for (Trail trail : this.trails) {
                        trail.removeOnFadeOut = true;
                    }
                }

            }
        });
    }

}
