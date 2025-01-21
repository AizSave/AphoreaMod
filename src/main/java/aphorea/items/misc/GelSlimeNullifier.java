package aphorea.items.misc;

import aphorea.data.AphWorldData;
import aphorea.items.vanillaitemtypes.AphMiscItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

public class GelSlimeNullifier extends AphMiscItem {

    public static AphWorldData worldData = new AphWorldData();

    public GelSlimeNullifier() {
        super(1);
        this.rarity = Rarity.LEGENDARY;
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, Mob mob) {
        return 500;
    }

    @Override
    public int getItemCooldownTime(InventoryItem item, Mob mob) {
        return 10000;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        if (level.isServer()) {
            AphWorldData currentData = worldData.getData(level.getWorldEntity());
            boolean gelSlimesNulled = currentData.gelslimesnulled;

            if (gelSlimesNulled) {

                currentData.gelslimesnulled = false;

                PacketChatMessage mensaje = new PacketChatMessage(Localization.translate("message", "gelslimesunnulled"));
                GameUtils.streamServerClients(level).forEach((j) -> j.sendPacket(mensaje));

            } else {

                currentData.gelslimesnulled = true;

                PacketChatMessage mensaje = new PacketChatMessage(Localization.translate("message", "gelslimesnulled"));
                GameUtils.streamServerClients(level).forEach((j) -> j.sendPacket(mensaje));
            }
        }

        return super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
    }

    @Override
    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "gelslimenullifier"));
        return tooltips;
    }
}
