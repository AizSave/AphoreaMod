package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphConsumableItem;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.level.maps.Level;

import java.util.ArrayList;
import java.util.stream.Stream;

abstract public class AphDeletedItem extends AphConsumableItem {
    public AphDeletedItem() {
        super(1, true);
        this.setItemCategory("consumable");
        this.dropsAsMatDeathPenalty = false;
        this.rarity = Rarity.LEGENDARY;
        this.worldDrawSize = 32;
    }

    abstract public ArrayList<InventoryItem> items(PlayerMob player);

    public int freeSpace(PlayerMob player) {
        return (int) player.getInv().main.streamSlots().filter(InventorySlot::isSlotClear).count();
    }

    public String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (freeSpace(player) < items(player).size()) return "nofreeslots";
        return null;
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        ArrayList<InventoryItem> itemsGiven = new ArrayList<>(this.items(player));
        Stream<InventorySlot> streamSlots = player.getInv().main.streamSlots().filter(InventorySlot::isSlotClear);
        streamSlots.forEach(slot -> {
            InventoryItem itemGiven = itemsGiven.get(0);
            if (itemGiven != null) {
                itemsGiven.remove(0);
                player.getInv().main.setItem(slot.slot, itemGiven);
            }
        });

        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }

        return item;
    }

    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader, String error) {
        if (level.isServer() && player != null && player.isServerClient()) {
            player.getServerClient().sendChatMessage(new LocalMessage("message", "needfreeslots", "slots", items(player).size()));
        } else {
            if (level.isServer() && player != null) {
                player.getServerClient().sendChatMessage(new LocalMessage("message", "needfreeslots", "slots", items(player).size()));
            }
        }
        return item;
    }

    public String getTranslatedTypeName() {
        return "Deleted Item";
    }

    @Override
    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "deleteditem"));
        return tooltips;
    }
}
