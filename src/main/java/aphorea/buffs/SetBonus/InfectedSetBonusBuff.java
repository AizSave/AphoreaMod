package aphorea.buffs.SetBonus;

import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class InfectedSetBonusBuff extends SetBonusBuff {
    public static String mobId = "livingsapling";
    public static GameDamage damage = new GameDamage(DamageTypeRegistry.SUMMON, 8);

    public InfectedSetBonusBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "infectedsetbonus")));
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "livingsapling")));
        return tooltips;
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);

        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob) buff.owner;
            int summonMobs = 3 - (int) player.serverFollowersManager.getFollowerCount(buff.buff.getStringID());
            if (summonMobs > 0) {
                for (int i = 0; i < summonMobs; i++) {
                    AttackingFollowingMob mob = (AttackingFollowingMob) MobRegistry.getMob(mobId, buff.owner.getLevel());

                    player.serverFollowersManager.addFollower(buff.buff.getStringID(), mob, FollowPosition.WALK_CLOSE, buff.buff.getStringID(), 1, 3, null, true);
                    mob.updateDamage(damage);
                    mob.getLevel().entityManager.addMob(mob, buff.owner.x, buff.owner.y);
                }
            }
        }
    }
}
