package aphorea.buffs.TrinketsActive;

import aphorea.AphDependencies;
import aphorea.utils.AphColors;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import org.jetbrains.annotations.NotNull;

public class BloodyPeriaptActiveBuff extends Buff {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(
            Particle.GType.CRITICAL,
            Particle.GType.IMPORTANT_COSMETIC,
            Particle.GType.COSMETIC
    );

    public boolean doLifeSteal;

    public boolean hasRPGMod;
    public BloodyPeriaptActiveBuff() {
        this.isVisible = false;
        this.canCancel = false;
        this.shouldSave = true;
        doLifeSteal = false;
        hasRPGMod = AphDependencies.checkRPGMod();
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SPEED, 0.5F);
        buff.addModifier(BuffModifiers.ATTACK_SPEED, 0.3F);
    }

    @Override
    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack) {
        String itemID = item.item.getStringID();
        if (itemID.equals("bloodbolt") || itemID.equals("bloodvolley")) {
            doLifeSteal = true;
        } else if (doLifeSteal) {
            doLifeSteal = false;
        }
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.damageType.equals(DamageTypeRegistry.MAGIC) && event.target.isHostile) {
            Mob owner = event.attacker.getAttackOwner();
            if (doLifeSteal) {
                int heal = (int) Math.ceil(event.damage * (hasRPGMod ? 0.002F : 0.02F));

                if (heal > 0) {
                    if (owner.isServer()) {
                        LevelEvent healEvent = new MobHealthChangeEvent(owner, heal);
                        owner.getLevel().entityManager.addLevelEvent(healEvent);
                    }

                    for (int i = 0; i < 20; i++) {
                        int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        owner.getLevel().entityManager.addParticle(owner, particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(AphColors.blood).heightMoves(10.0F, 30.0F).lifeTime(500);
                    }
                }
            }

        }
    }

}

