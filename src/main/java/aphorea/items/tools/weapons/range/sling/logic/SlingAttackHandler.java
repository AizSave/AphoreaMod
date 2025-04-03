package aphorea.items.tools.weapons.range.sling.logic;

import aphorea.items.tools.weapons.range.sling.AphSlingToolItem;
import aphorea.utils.AphColors;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;

import java.awt.*;
import java.awt.geom.Point2D;

public class SlingAttackHandler extends MousePositionAttackHandler {
    public int chargeTime;
    public boolean fullyCharged;
    public AphSlingToolItem toolItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public boolean endedByInteract;
    protected int endAttackBuffer;

    private float angle_v;
    private float angle;

    public void restartAngle() {
        angle_v = 4;
        angle = 0;
    }

    public void moveAngle() {
        angle += angle_v;
        if (angle >= 360) {
            angle = angle - 360;
        }
    }

    public void increaseAngleM() {
        if (angle_v < 22.5F) {
            angle_v += 0.2F;
            if (angle_v > 22.5F) {
                angle_v = 22.5F;
            }
        }
    }


    public SlingAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSlingToolItem toolItem, int chargeTime, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.toolItem = toolItem;
        this.chargeTime = chargeTime;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();

        restartAngle();
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float) this.getTimeSinceStart() / this.chargeTime;
    }

    public Point getNextItemAttackerLevelPos(Mob currentTarget) {
        InventoryItem attackItem = this.item.copy();
        attackItem.getGndData().setFloat("skillPercent", 1.0F);
        return ((ItemAttackerWeaponItem) attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, attackItem);
    }

    public void onUpdate() {
        super.onUpdate();

        this.moveAngle();
        this.increaseAngleM();

        Point2D.Float dir = GameMath.normalize((float) this.lastX - this.attackerMob.x, (float) this.lastY - this.attackerMob.y);
        float chargePercent = this.getChargePercent();
        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setBoolean("charging", true);
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        showItem.getGndData().setFloat("showAngle", angle);
        this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);

        if (chargePercent >= 1.0F) {
            if (!this.attackerMob.isPlayer) {
                this.endAttackBuffer += this.updateInterval;
                if (this.endAttackBuffer >= 350) {
                    this.endAttackBuffer = 0;
                    this.attackerMob.endAttackHandler(true);
                    return;
                }
            }

            if (this.attackerMob.isClient()) {
                this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + dir.x * 16.0F + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), this.attackerMob.y + 4.0F + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx / 10.0F, this.attackerMob.dy / 10.0F).color(AphColors.leather).height(20.0F - dir.y * 16.0F);
            }

            if (!this.fullyCharged) {
                this.fullyCharged = true;
                if (this.attackerMob.isClient()) {
                    int particles = 35;
                    float anglePerParticle = 360.0F / (float) particles;
                    ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

                    for (int i = 0; i < particles; ++i) {
                        int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                        this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(AphColors.leather).heightMoves(0.0F, 30.0F).lifeTime(500);
                    }

                    SoundManager.playSound(GameResources.tick, SoundEffect.effect(this.attackerMob).volume(0.1F).pitch(2.5F));
                }
            }
        }
    }

    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (!this.endedByInteract && chargePercent >= 1F) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob) this.attackerMob).constantAttack = true;
            }

            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            attackItem.getGndData().setBoolean("charged", true);
            if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                Point attackPos = ((ItemAttackerWeaponItem) attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                this.lastX = attackPos.x;
                this.lastY = attackPos.y;
            }

            if (this.attackerMob.isClient()) {
                SoundManager.playSound(GameResources.run, SoundEffect.effect(this.attackerMob));
            }

            this.toolItem.doAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, attackItem, this.seed);

            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                b.onItemAttacked(this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0);
            }
        }

        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}
