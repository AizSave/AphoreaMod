package aphorea.buffs.Trinkets.Periapts;

import aphorea.other.buffs.trinkets.AphPeriaptActivableBuff;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.awt.*;

public class FrozenPeriaptBuff extends AphPeriaptActivableBuff {

    public FrozenPeriaptBuff() {
        super("frozenperiaptactive");
    }

    @Override
    public Color getColor() {
        return new Color(0, 204, 255);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.buffManager.hasBuff("freeze")) {
            buff.owner.buffManager.removeBuff("freeze", true);
        }
        if (buff.owner.buffManager.hasBuff("frostslow")) {
            buff.owner.buffManager.removeBuff("frostslow", true);
        }
    }

    public void onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event) {
        if (event.target.buffManager.hasBuff(BuffRegistry.Debuffs.FREEZING)) {
            event.damage = event.damage.setDamage(event.damage.damage * 1.2F);
        }
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.attacker.getAttackOwner().isPlayer) {
            Mob owner = event.attacker.getAttackOwner();
            if (event.damageType.equals(DamageTypeRegistry.MELEE) || event.damageType.equals(DamageTypeRegistry.RANGED) || owner.buffManager.hasBuff("frozenperiaptactive")) {
                if (!event.target.buffManager.hasBuff(BuffRegistry.Debuffs.FREEZING)) {
                    event.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FREEZING, event.target, 3000, event.attacker), false);
                }

                for (int i = 0; i < 20; i++) {
                    int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                    float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    owner.getLevel().entityManager.addParticle(event.target, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(new Color(0, 153, 255)).heightMoves(10.0F, 30.0F).lifeTime(500);
                }
            }

        }
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "frozenperiapt"));
        tooltips.add(Localization.translate("itemtooltip", "frozenperiapt2"));
        tooltips.add(Localization.translate("itemtooltip", "frozenperiapt3"));
        return tooltips;
    }

}