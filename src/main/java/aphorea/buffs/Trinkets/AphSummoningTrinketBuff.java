package aphorea.buffs.Trinkets;

import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.inventory.item.ItemStatTip;

import java.util.LinkedList;

abstract public class AphSummoningTrinketBuff extends TrinketBuff {

    public String buffId;
    public String mobId;
    public int mobQuantity;
    public GameDamage damage;

    public AphSummoningTrinketBuff(String buffId, String mobId, int mobQuantity, GameDamage damage) {
        this.buffId = buffId;
        this.mobId = mobId;
        this.mobQuantity = mobQuantity;
        this.damage = damage;
    }

    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);

        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob) buff.owner;
            int summonMobs = mobQuantity - (int) player.serverFollowersManager.getFollowerCount(buffId);
            for (int i = 0; i < summonMobs; i++) {
                AttackingFollowingMob mob = (AttackingFollowingMob) MobRegistry.getMob(mobId, buff.owner.getLevel());

                player.serverFollowersManager.addFollower(buffId, mob, FollowPosition.WALK_CLOSE, buffId, 1, mobQuantity, null, true);
                mob.updateDamage(damage);
                mob.getLevel().entityManager.addMob(mob, buff.owner.x, buff.owner.y);
            }
        }
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}