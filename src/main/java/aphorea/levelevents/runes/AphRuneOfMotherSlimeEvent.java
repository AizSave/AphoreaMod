package aphorea.levelevents.runes;

import aphorea.patches.PlayerFlyingHeight;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;

public class AphRuneOfMotherSlimeEvent extends HitboxEffectEvent implements Attacker {
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet<>();
    public int startX;
    public int startY;
    public int targetX;
    public int targetY;

    public float effectNumber;

    private boolean showedImpact = false;
    private boolean teleported = false;

    public AphRuneOfMotherSlimeEvent() {
    }

    public AphRuneOfMotherSlimeEvent(Mob owner, int targetX, int targetY, float effectNumber) {
        super(owner, new GameRandom());
        this.startX = owner.getX();
        this.startY = owner.getY();
        this.targetX = targetX;
        this.targetY = targetY;
        this.effectNumber = effectNumber;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.startX);
        writer.putNextInt(this.startY);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextFloat(this.effectNumber);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.startX = reader.getNextInt();
        this.startY = reader.getNextInt();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.effectNumber = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet<>();

        showedImpact = false;
        teleported = false;
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.lifeTime += 50;
        if (this.lifeTime >= 1100) {
            this.over();
        } else if (this.lifeTime >= 1000) {
            if (!showedImpact) {
                SoundManager.playSound(GameResources.slimeSplash1, SoundEffect.effect(targetX, targetY).pitch(1.0F));
                SoundManager.playSound(GameResources.flick, SoundEffect.effect(targetX, targetY).pitch(0.8F));

                new AphAreaList(new AphArea(200, AphColors.paletteMotherSlime)).setOnlyVision(false).executeClient(level, targetX, targetY, 1, 0.8F, 0.5F);

                showedImpact = true;
            }
            if (!teleported) {
                PlayerFlyingHeight.playersFlyingHeight.remove(owner.getUniqueID());
                owner.setPos(targetX, targetY, false);

                teleported = true;
            }
        } else {
            float movePercent = (float) this.lifeTime / 1000;
            owner.setPos(startX + (targetX - startX) * movePercent, startY + (targetY - startY) * movePercent, false);

            if (this.lifeTime >= 500) {
                float downPercent = (float) (this.lifeTime - 500) / 500;
                downPercent = 1 - (float) Math.cos((downPercent * Math.PI) / 2);
                PlayerFlyingHeight.playersFlyingHeight.put(owner.getUniqueID(), (int) ((1F - downPercent) * 500));
            } else {
                float upPercent = (float) this.lifeTime / 500;
                upPercent = (float) Math.sin((upPercent * Math.PI) / 2);
                PlayerFlyingHeight.playersFlyingHeight.put(owner.getUniqueID(), (int) (upPercent * 500));
            }
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2000) {
            this.over();
        }
    }

    public Shape getHitBox() {
        if (lifeTime >= 1000) {
            float size = 200;
            return new Ellipse2D.Float(this.targetX - size / 2, this.targetY - size / 2, size, size);
        } else {
            return new Rectangle();
        }
    }

    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    public void clientHit(Mob target) {
        this.hits.add(target.getUniqueID());
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0F) {
                float knockback = 50.0F / modifier;
                float damagePercent = effectNumber / 100;
                if (target.isBoss()) {
                    damagePercent /= 50;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5;
                }
                target.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, target.getMaxHealth() * damagePercent), target.x - this.owner.x, target.y - this.owner.y, knockback, this.owner);
            }
            this.hits.add(target.getUniqueID());
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }
}
