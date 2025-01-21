package aphorea.utils.customchargeattacks;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class AphCustomGreatswordChargeAttackHandler<T extends AphGreatswordCustomChargeToolItem> extends MouseAngleAttackHandler {
    public T toolItem;
    public InventoryItem item;
    public int seed;
    public long startTime;
    public AphCustomGreatswordChargeLevel<T>[] chargeLevels;
    public int chargeTimeRemaining;
    public int currentChargeLevel;
    public int timeSpentUpToCurrentChargeLevel;
    public boolean endedByInteract;

    public AphCustomGreatswordChargeAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, T toolItem, int seed, int startX, int startY, AphCustomGreatswordChargeLevel<T>[] chargeLevels) {
        super(player, slot, 20, 1000.0F, startX, startY);
        this.toolItem = toolItem;
        this.item = item;
        this.seed = seed;
        this.chargeLevels = chargeLevels;
        this.currentChargeLevel = -1;
        this.chargeTimeRemaining = Arrays.stream(chargeLevels).mapToInt((l) -> l.timeToCharge).sum();
        this.timeSpentUpToCurrentChargeLevel = 0;
        if (chargeLevels.length == 0) {
            throw new IllegalArgumentException("Must have at least one charge level for greatswords");
        } else {
            this.startTime = player.getLevel().getWorldEntity().getLocalTime();
        }
    }

    public long getTimeSinceStart() {
        return this.player.getWorldEntity().getLocalTime() - this.startTime;
    }

    public void updateCurrentChargeLevel() {
        while (true) {
            if (this.currentChargeLevel < this.chargeLevels.length - 1) {
                long timeSinceStart = this.getTimeSinceStart();
                long timeSpentOnCurrent = timeSinceStart - (long) this.timeSpentUpToCurrentChargeLevel;
                AphCustomGreatswordChargeLevel<T> nextLevel = this.chargeLevels[this.currentChargeLevel + 1];
                long timeToChargeNextLevel = Math.round((float) nextLevel.timeToCharge * (1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player)));
                if (timeSpentOnCurrent >= timeToChargeNextLevel) {
                    this.timeSpentUpToCurrentChargeLevel = (int) ((long) this.timeSpentUpToCurrentChargeLevel + timeToChargeNextLevel);
                    this.chargeTimeRemaining -= nextLevel.timeToCharge;
                    ++this.currentChargeLevel;
                    nextLevel.onReachedLevel(this);
                    continue;
                }
            }

            return;
        }
    }

    public float getChargePercent() {
        int chargeTime = this.timeSpentUpToCurrentChargeLevel + Math.round((float) this.chargeTimeRemaining * (1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player)));
        return (float) Math.min(this.getTimeSinceStart(), chargeTime) / (float) chargeTime;
    }

    public void onUpdate() {
        super.onUpdate();
        this.updateCurrentChargeLevel();
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.player.getX() + (int) (dir.x * 100.0F);
        int attackY = this.player.getY() + (int) (dir.y * 100.0F);
        float chargePercent = this.getChargePercent();
        Packet attackContent = new Packet();
        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        showItem.getGndData().setBoolean("IsCharging", true);
        this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, showItem);
        this.player.showAttack(showItem, attackX, attackY, this.seed, attackContent);
        if (this.player.isServer()) {
            ServerClient client = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, showItem, attackX, attackY, this.seed, attackContent), this.player, client);
        }

        if (this.currentChargeLevel >= 0) {
            this.chargeLevels[this.currentChargeLevel].updateAtLevel(this, showItem);
        }
    }

    public void drawWeaponParticles(InventoryItem showItem, Color color) {
        float chargePercent = showItem.getGndData().getFloat("chargePercent");
        float angle = this.toolItem.getSwingRotation(showItem, this.player.getDir(), chargePercent);
        int attackDir = this.player.getDir();
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
        int range = GameRandom.globalRandom.getIntBetween(0, this.toolItem.getAttackRange(this.item));
        this.player.getLevel().entityManager.addParticle(this.player.x + (float) offsetX + dx * (float) range + GameRandom.globalRandom.floatGaussian() * 3.0F, this.player.y + 4.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.player.dx, this.player.dy).color(color).height(20.0F - dy * (float) range - (float) offsetY);
    }

    public void drawParticleExplosion(int particleCount, Color color, int minForce, int maxForce) {
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 360.0F / (float) particleCount;

        for (int i = 0; i < particleCount; ++i) {
            int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(minForce, maxForce);
            float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(minForce, maxForce) * 0.8F;
            this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(color).heightMoves(0.0F, 30.0F).lifeTime(500);
        }

    }

    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.player.endAttackHandler(false);
    }

    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.player.endAttackHandler(false);
    }

    public void onEndAttack(boolean bySelf) {
        this.updateCurrentChargeLevel();
        if (this.currentChargeLevel >= 0 && !this.endedByInteract) {
            this.player.constantAttack = true;
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.player.getX() + (int) (dir.x * 100.0F);
            int attackY = this.player.getY() + (int) (dir.y * 100.0F);
            Packet attackContent = new Packet();
            InventoryItem attackItem = this.item.copy();
            AphCustomGreatswordChargeLevel<T> currentLevel = this.chargeLevels[this.currentChargeLevel];
            attackItem.getGndData().setBoolean("shouldFire", true);
            attackItem.getGndData().setInt("cooldown", this.toolItem.getAttackAnimTime(attackItem, this.player) + 100);
            currentLevel.setupAttackItem(attackItem);
            this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, attackItem);
            this.player.showAttack(attackItem, attackX, attackY, this.seed, attackContent);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, attackItem, attackX, attackY, this.seed, attackContent), this.player, client);
            }

            this.toolItem.superOnAttack(this.player.getLevel(), attackX, attackY, this.player, this.player.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, new PacketReader(attackContent));

            for (ActiveBuff b : this.player.buffManager.getArrayBuffs()) {
                b.onItemAttacked(attackX, attackY, this.player, this.player.getCurrentAttackHeight(), attackItem, this.slot, 0);
            }

            toolItem.endChargeAttack(player, dir, this.currentChargeLevel);

        } else {
            this.player.stopAttack(false);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketPlayerStopAttack(client.slot), this.player, client);
            }
        }
    }

}
