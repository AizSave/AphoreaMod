package aphorea.buffs.Trinkets;

import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.inventory.item.ItemStatTip;

import java.util.LinkedList;

abstract public class AphSummoningTrinketBuff extends TrinketBuff {

    String buffId;
    String mobId;
    int mobQuantity;
    GameDamage damage;

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
            ServerClient serverClient = player.getServerClient();
            int summonMobs = mobQuantity - (int) serverClient.getFollowerCount(buffId);
            for (int i = 0; i < summonMobs; i++) {
                AttackingFollowingMob mob = (AttackingFollowingMob) MobRegistry.getMob(mobId, buff.owner.getLevel());

                serverClient.addFollower(buffId, mob, FollowPosition.WALK_CLOSE, buffId, 1, 2, null, true);
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