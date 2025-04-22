package aphorea.buffs.Trinkets.Periapt;

import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
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
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class AbysmalPeriaptBuff extends TrinketBuff {

    public AbysmalPeriaptBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.target.isHostile) {
            Mob owner = event.attacker.getAttackOwner();
            if (event.damageType.equals(DamageTypeRegistry.MAGIC)) {
                int heal = (int) Math.ceil(event.damage * 0.03F);

                if (heal > 0) {
                    if (owner.isServer()) {
                        LevelEvent healEvent = new MobHealthChangeEvent(owner, heal);
                        owner.getLevel().entityManager.addLevelEvent(healEvent);
                    }

                    for (int i = 0; i < 20; i++) {
                        int angle = (int) (360.0F + GameRandom.globalRandom.nextFloat() * 360.0F);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        owner.getLevel().entityManager.addParticle(owner, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(dx, dy, 0.8F).color(GameRandom.globalRandom.getOneOf(AphColors.paletteDeepDemonic)).heightMoves(10.0F, 30.0F).lifeTime(500);
                    }
                }
            }

        }
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "abysmalperiapt"));
        return tooltips;
    }

}