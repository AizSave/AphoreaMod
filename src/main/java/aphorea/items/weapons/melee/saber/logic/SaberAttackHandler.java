package aphorea.items.weapons.melee.saber.logic;

import aphorea.items.weapons.melee.saber.AphSaberToolItem;
import necesse.engine.GlobalData;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

import java.awt.*;
import java.awt.geom.Point2D;

public class SaberAttackHandler extends GreatswordAttackHandler {

    public SaberAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
    }

    public void superSuperOnUpdate() {
        if (this.attackerMob.isPlayer) {
            if (this.attackerMob.isClient()) {
                PlayerMob player = (PlayerMob)this.attackerMob;
                GameCamera camera = GlobalData.getCurrentState().getCamera();
                if (this.getNextClientAngle(player, camera) != this.targetAngle) {
                    this.sendPacketUpdate(true);
                }
            }
        } else if (this.lastItemAttackerTarget != null) {
            this.targetAngle = this.getNextItemAttackerAngle(this.lastItemAttackerTarget);
        }

        if (this.currentAngle != (float)this.targetAngle) {
            float delta = GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle);
            float change = this.speed * (float)this.updateInterval / 250.0F;
            if (Math.abs(delta) < change) {
                this.currentAngle = (float)this.targetAngle;
            } else if (delta < 0.0F) {
                this.currentAngle = GameMath.fixAngle(this.currentAngle + change);
                if (GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle) > 0.0F) {
                    this.currentAngle = (float)this.targetAngle;
                }
            } else if (delta > 0.0F) {
                this.currentAngle = GameMath.fixAngle(this.currentAngle - change);
                if (GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle) < 0.0F) {
                    this.currentAngle = (float)this.targetAngle;
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        this.superSuperOnUpdate();
        this.updateCurrentChargeLevel();
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0F);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0F);
        float chargePercent = this.getChargePercent();
        if (this.currentChargeLevel >= this.chargeLevels.length - 1 && (!this.attackerMob.isPlayer || (toolItem instanceof AphSaberToolItem && ((AphSaberToolItem) toolItem).isAuto))) {
            this.endAttackBuffer += this.updateInterval;
            if (this.endAttackBuffer >= 250) {
                this.endAttackBuffer = 0;
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }

        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        showItem.getGndData().setBoolean("charging", true);
        this.attackerMob.showAttackAndSendAttacker(showItem, attackX, attackY, 0, this.seed);
        if (this.currentChargeLevel >= 0) {
            this.chargeLevels[this.currentChargeLevel].updateAtLevel(this, showItem);
        }

    }

    @Override
    public float getChargePercent() {
        if(toolItem instanceof AphSaberToolItem) {
            AphSaberToolItem saberToolItem = (AphSaberToolItem) toolItem;
            if(saberToolItem.isAuto || (saberToolItem.nonPlayerChargeLevels != null && this.chargeLevels.length == saberToolItem.nonPlayerChargeLevels.length)) {
                return super.getChargePercent();
            } else {
                int chargeTime = this.timeSpentUpToCurrentChargeLevel + Math.round((float) this.chargeTimeRemaining * (1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob)));
                float linearPercent = (float) Math.min(this.getTimeSinceStart(), chargeTime) / (float) chargeTime;
                return -4 * (float) Math.pow(linearPercent - 0.5f, 2) + 1;
            }

        } else {
            return super.getChargePercent();
        }
    }

    @Override
    public void drawWeaponParticles(InventoryItem showItem, Color color) {
        float chargePercent = showItem.getGndData().getFloat("chargePercent");
        showItem.getGndData().setBoolean("charging", true);
        float angle = this.toolItem.getSwingRotation(showItem, this.attackerMob.getDir(), chargePercent);
        int attackDir = this.attackerMob.getDir();
        int offsetX = 0;
        int offsetY = 0;
        if (attackDir == 0) {
            angle = -angle - 90.0F;
            offsetY = -8;
        } else if (attackDir == 1) {
            angle = -angle + 180.0F + 45.0F;
            offsetX = 8;
        } else if (attackDir == 2) {
            angle = -angle + 90.0F;
            offsetY = 12;
        } else {
            angle = angle + 90.0F + 45.0F;
            offsetX = -8;
        }

        float dx = GameMath.sin(angle);
        float dy = GameMath.cos(angle);
        int range = GameRandom.globalRandom.getIntBetween(0, (int) (this.toolItem.getAttackRange(this.item) * 0.1F));
        this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + (float)offsetX + dx * (float)range + GameRandom.globalRandom.floatGaussian() * 3.0F, this.attackerMob.y + 4.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx, this.attackerMob.dy).color(color).height(20.0F - dy * (float)range - (float)offsetY);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        super.onEndAttack(bySelf);
        if (!this.endedByInteract) {
            if (this.currentChargeLevel == 4) {
                Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                launchSaberProjectile(dir, true);
            } else if ((this.currentChargeLevel == 3 || this.currentChargeLevel == 5)) {
                Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                launchSaberProjectile(dir, false);
            }
        }
    }

    private void launchSaberProjectile(Point2D.Float dir, boolean exact) {
        float velocity = exact ? 300.0F : 200.0F;
        int distanceExtra = exact ? 7 : 3;
        GameDamage originalDamage = this.toolItem.getAttackDamage(this.item);
        GameDamage damage = exact ? originalDamage : originalDamage.modDamage(0.5F);
        float finalVelocity = (float) Math.round(this.toolItem.getEnchantment(this.item).applyModifierLimited(ToolItemModifiers.VELOCITY, ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * velocity * this.attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
        Projectile projectile = ((AphSaberToolItem) toolItem).getProjectile(this.attackerMob.getLevel(), this.attackerMob, this.attackerMob.x, this.attackerMob.y, this.attackerMob.x + dir.x * 100.0F, this.attackerMob.y + dir.y * 100.0F, finalVelocity, (int) ((float) this.toolItem.getAttackRange(this.item)) * distanceExtra, damage, 0);
        if (projectile != null) {
            GameRandom random = new GameRandom(this.seed);
            projectile.resetUniqueID(random);
            this.attackerMob.getLevel().entityManager.projectiles.addHidden(projectile);
            if (this.attackerMob.isServer()) {
                this.attackerMob.getLevel().getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
            }
        }
    }
}
