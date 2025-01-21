package aphorea.buffs.Trinkets;

import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealingFunctions;
import necesse.engine.network.Packet;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

abstract public class AphDamageWhenHealTrinketBuff extends TrinketBuff implements AphMagicHealingFunctions {
    int healingPerAttack;
    Map<String, Integer> healingDone = new HashMap<>();

    AphAreaList areaList;

    public AphDamageWhenHealTrinketBuff(int healingPerAttack, AphAreaList areaList) {
        this.healingPerAttack = healingPerAttack;
        this.areaList = areaList;
    }

    public abstract Packet getPacket(PlayerMob player, float rangeModifier);

    @Override
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    public void onMagicalHealing(Mob healer, Mob target, int healing, int realHealing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        if (healer.isServer() && healer.isPlayer) {
            String playerName = ((PlayerMob) healer).playerName;

            int thisHealingDone = healingDone.getOrDefault(playerName, 0) + realHealing;

            if (thisHealingDone > healingPerAttack) {
                thisHealingDone -= healingPerAttack;

                this.areaList.executeAreas(healer);

                healer.getServer().network.sendToAllClients(getPacket((PlayerMob) healer, 1));
            }

            healingDone.put(playerName, thisHealingDone);
        }
    }
}
