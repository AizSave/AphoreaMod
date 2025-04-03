package aphorea.items.tools.weapons.melee.greatsword.logic;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;

import java.awt.*;
import java.awt.geom.Point2D;

public class GreatswordDashLevelEvent extends MobDashLevelEvent {
    public ItemAttackerMob attackerMob;
    public InventoryItem item;
    public int seed;

    public GreatswordDashLevelEvent() {
    }

    public GreatswordDashLevelEvent(ItemAttackerMob attackerMob, InventoryItem item, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(attackerMob, seed, dirX, dirY, distance, animTime, damage);
        this.attackerMob = attackerMob;
        this.item = item;
        this.seed = seed;
    }

    public void init() {
        super.init();
        if (this.level != null && this.level.isClient() && this.owner != null) {
            float forceMod = Math.min((float) this.animTime / 700.0F, 1.0F);
            float forceX = this.dirX * this.distance * forceMod;
            float forceY = this.dirY * this.distance * forceMod;

            for (int i = 0; i < 30; ++i) {
                this.level.entityManager.addParticle(this.owner.x + (float) GameRandom.globalRandom.nextGaussian() * 15.0F + forceX / 5.0F, this.owner.y + (float) GameRandom.globalRandom.nextGaussian() * 20.0F + forceY / 5.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 5.0F, forceY * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 5.0F).color(AphColors.lighter_gray).height(18.0F).lifeTime(700);
            }

            SoundManager.playSound(GameResources.swoosh, SoundEffect.effect(this.owner).volume(0.2F).pitch(2.5F));
        }

        if (this.owner != null) {
            this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, this.owner, this.animTime, null), false);
            this.owner.addBuff(new ActiveBuff(AphBuffs.SABER_DASH_ACTIVE, this.owner, this.animTime, null), false);
        }
    }

    public Shape getHitBox() {
        Point2D.Float dir;
        if (this.owner.getDir() == 3) {
            dir = GameMath.getPerpendicularDir(-this.dirX, -this.dirY);
        } else {
            dir = GameMath.getPerpendicularDir(this.dirX, this.dirY);
        }

        float width = 160.0F;
        float frontOffset = 0;
        float range = 160.0F;
        float rangeOffset = -80.0F;
        return new LineHitbox(this.owner.x + dir.x * rangeOffset + this.dirX * frontOffset, this.owner.y + dir.y * rangeOffset + this.dirY * frontOffset, dir.x, dir.y, range, width);
    }
}
