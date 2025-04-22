package aphorea.buffs.Trinkets.Periapt.Summoner;

import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.MobFollower;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.trinketItem.TrinketItem;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NecromancyPeriaptBuff extends TrinketBuff {

    static String mobId = "undeadskeleton";

    static GameDamage damage = new GameDamage(DamageTypeRegistry.SUMMON, 14);

    public NecromancyPeriaptBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (buff.owner.isServer() && (event.target.removed() || event.target.getHealth() <= 0) && event.target.isHostile) {
            PlayerMob player = (PlayerMob) buff.owner;

            float spawnX = player.x;
            float spawnY = player.y;

            List<MobFollower> skeletonList = getUndeadSkeletons(player).collect(Collectors.toList());

            if (skeletonList.size() >= 3) {
                MobFollower firstSkeleton = skeletonList.get(0);
                if (!firstSkeleton.mob.removed()) {
                    spawnX = firstSkeleton.mob.x;
                    spawnY = firstSkeleton.mob.y;
                    player.serverFollowersManager.removeFollower(firstSkeleton.mob, true, false);
                }
            }

            AttackingFollowingMob mob = (AttackingFollowingMob) MobRegistry.getMob(mobId, player.getLevel());

            player.serverFollowersManager.addFollower("necromancyperiapt", mob, FollowPosition.PYRAMID, "necromancyperiapt", Float.MIN_VALUE, Integer.MAX_VALUE, (MserverClient, Mmob) -> ((AttackingFollowingMob) Mmob).updateDamage(damage), true);
            mob.getLevel().entityManager.addMob(mob, spawnX, spawnY);
        }
    }

    public Stream<MobFollower> getUndeadSkeletons(PlayerMob player) {
        return player.serverFollowersManager.streamFollowers().filter((m) -> m.mob.getStringID().equals(mobId));
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "necromancyperiapt"));
        tooltips.add(Localization.translate("itemtooltip", "necromancyperiapt2"));
        return tooltips;
    }
}