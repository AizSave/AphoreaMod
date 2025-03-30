package aphorea.levelevents.thepillar;

import aphorea.particles.ThePillarFallingCrystalParticle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

import java.awt.geom.Ellipse2D;

public class ThePillarFallingCrystalAttackEvent extends MobAbilityLevelEvent implements Attacker {
    protected long spawnTime;
    protected int x;
    protected int y;
    protected GameDamage damage;
    protected boolean playedStartSound;
    private final int warningTime = 1000;

    protected boolean turnIntoObject;

    public ThePillarFallingCrystalAttackEvent() {
    }

    public ThePillarFallingCrystalAttackEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, uniqueIDRandom);
        this.spawnTime = owner.getWorldEntity().getTime();
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.turnIntoObject = GameRandom.globalRandom.getChance(0.25F);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spawnTime = reader.getNextLong();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.turnIntoObject = reader.getNextBoolean();
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.spawnTime);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
        writer.putNextBoolean(this.turnIntoObject);
    }

    public void init() {
        super.init();
        if (this.isClient()) {
            this.level.entityManager.addParticle(new ThePillarFallingCrystalParticle(this.level, (float)this.x, (float)this.y, this.spawnTime, 1000L), Particle.GType.CRITICAL);
        }

    }

    public void clientTick() {
        if (!this.isOver()) {
            long eventTime = this.level.getWorldEntity().getTime() - this.spawnTime;
            if (eventTime > 1000L && !this.playedStartSound) {
                SoundManager.playSound(GameResources.magicbolt2, SoundEffect.effect((float)this.x, (float)this.y));
                this.playedStartSound = true;
            }

            if (eventTime > 1200L) {
                SoundManager.playSound(GameResources.firespell1, SoundEffect.effect((float)this.x, (float)this.y).volume(0.5F));
                this.over();
            }

        }
    }

    public void serverTick() {
        if (!this.isOver()) {
            long eventTime = this.level.getWorldEntity().getTime() - this.spawnTime;
            if (eventTime > 1200L) {
                Ellipse2D hitBox = new Ellipse2D.Float((float)(this.x - 38), (float)(this.y - 30), 76.0F, 60.0F);
                GameUtils.streamTargets(this.owner, GameUtils.rangeTileBounds(this.x, this.y, 5)).filter((m) -> {
                    return m.canBeHit(this.owner) && hitBox.intersects(m.getHitBox());
                }).forEach((m) -> {
                    m.isServerHit(this.damage, (float)(m.getX() - this.x), (float)(m.getY() - this.y), 100.0F, this.owner);
                });
                this.over();
            }

        }
    }

    public GameMessage getAttackerName() {
        return this.owner != null ? this.owner.getAttackerName() : new StaticMessage("EP_BOMB_ATTACK");
    }

    public DeathMessageTable getDeathMessages() {
        return this.owner != null ? this.owner.getDeathMessages() : DeathMessageTable.fromRange("generic", 8);
    }

    public Mob getFirstAttackOwner() {
        return this.owner;
    }

    @Override
    public void over() {
        int tileX = this.x / 32;
        int tileY = this.y / 32;
        if(turnIntoObject && (level.getObject(tileX, tileY).getID() == 0 || level.getObject(tileX, tileY).isGrass)) {
            ObjectRegistry.getObject("spinelclustersmall").placeObject(level, tileX, tileY, GameRandom.globalRandom.getIntBetween(0, 3), false);
        }
        super.over();
    }
}
