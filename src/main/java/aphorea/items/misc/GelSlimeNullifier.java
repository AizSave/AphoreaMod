package aphorea.items.misc;

import aphorea.data.AphWorldData;
import aphorea.items.vanillaitemtypes.AphMiscItem;
import aphorea.registry.AphData;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GelSlimeNullifier extends AphMiscItem {

    public GelSlimeNullifier() {
        super(1);
        this.rarity = Rarity.LEGENDARY;
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 500;
    }

    @Override
    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 10000;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isServer()) {
            AphWorldData currentData = AphData.getWorldData(level.getWorldEntity());
            boolean gelSlimesNulled = currentData.gelSlimesNulled;

            if (gelSlimesNulled) {

                currentData.gelSlimesNulled = false;

                PacketChatMessage mensaje = new PacketChatMessage(Localization.translate("message", "gelslimesunnulled"));
                GameUtils.streamServerClients(level).forEach((j) -> j.sendPacket(mensaje));

            } else {

                currentData.gelSlimesNulled = true;

                PacketChatMessage message = new PacketChatMessage(Localization.translate("message", "gelslimesnulled"));
                GameUtils.streamServerClients(level).forEach((j) -> j.sendPacket(message));
            }
        }

        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "gelslimenullifier"));
        return tooltips;
    }
}
