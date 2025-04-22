package aphorea.buffs.Trinkets.Charm;

import aphorea.buffs.AdrenalineBuff;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.MobFollower;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.util.Objects;

public class BloomrushCharmBuff extends TrinketBuff {
    public static String mobId = "livingsapling";
    public static GameDamage damage = new GameDamage(DamageTypeRegistry.SUMMON, 8);

    public BloomrushCharmBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.STAMINA_CAPACITY, 0.5F);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "zephyrcharmtip"));
        tooltips.add(Localization.translate("itemtooltip", "adrenalinecharm"));
        tooltips.add(Localization.translate("itemtooltip", "bloomrushcharm"));
        tooltips.add(Localization.translate("itemtooltip", "adrenaline"));
        tooltips.add(Localization.translate("itemtooltip", "livingsapling"));
        return tooltips;
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        AdrenalineBuff.giveAdrenaline(buff.owner, 20000, true);
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        updateBuffs(buff, AdrenalineBuff.getAdrenalineLevel(buff.owner));
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        int level = AdrenalineBuff.getAdrenalineLevel(buff.owner);
        updateBuffs(buff, level);

        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob) buff.owner;
            int summonMobs = level - (int) player.serverFollowersManager.getFollowerCount(buff.buff.getStringID());
            if (summonMobs > 0) {
                for (int i = 0; i < summonMobs; i++) {
                    AttackingFollowingMob mob = (AttackingFollowingMob) MobRegistry.getMob(mobId, buff.owner.getLevel());

                    player.serverFollowersManager.addFollower(buff.buff.getStringID(), mob, FollowPosition.WALK_CLOSE, buff.buff.getStringID(), 1, 5, null, true);
                    mob.updateDamage(damage);
                    mob.getLevel().entityManager.addMob(mob, buff.owner.x, buff.owner.y);
                }
            } else if (summonMobs < 0) {
                MobFollower[] followers = player.serverFollowersManager.streamFollowers()
                        .filter(m -> Objects.equals(m.summonType, buff.buff.getStringID()))
                        .toArray(MobFollower[]::new);
                for (int i = 0; i < Math.abs(summonMobs); i++) {
                    player.serverFollowersManager.removeFollower(followers[i].mob, true, false);
                }
            }

        }
    }

    public void updateBuffs(ActiveBuff buff, int level) {
        buff.setModifier(BuffModifiers.STAMINA_USAGE, -0.1F * level);
        buff.setModifier(BuffModifiers.STAMINA_USAGE, -0.1F * level);
    }

}
