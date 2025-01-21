package aphorea.items.consumable;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import aphorea.items.vanillaitemtypes.AphConsumableItem;
import aphorea.registry.AphContainers;
import aphorea.registry.AphItems;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.travel.TravelContainer;
import necesse.level.maps.Level;

public class InitialRune extends AphConsumableItem {
    public InitialRune() {
        super(1, false);
        this.rarity = Rarity.UNIQUE;
        this.itemCooldownTime.setBaseValue(1000);
        this.worldDrawSize = 32;
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            if (!TravelContainer.canOpen(client)) {
                client.sendChatMessage(new LocalMessage("ui", "travelopeninvalid"));
            } else {
                PacketOpenContainer p = new PacketOpenContainer(AphContainers.INITIAL_RUNE_CONTAINER);
                ContainerRegistry.openAndSendContainer(client, p);
            }
        }

        return item;
    }

    public String canAttack(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        String out = super.canAttack(level, x, y, player, item);
        if (out != null) {
            return out;
        } else {
            AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(player);
            return !playerData.runeSelected ? null : Localization.translate("message", "alreadyselectedinitialrune");
        }
    }

    public String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(player);
        return !playerData.runeSelected ? null : Localization.translate("message", "alreadyselectedinitialrune");
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(perspective);
        if (playerData.runeSelected) {
            tooltips.add(Localization.translate("message", "alreadyselectedinitialrune"));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        }
        return tooltips;
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "initialrune");
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        int sprite = (int) ((perspective.getWorldTime() / 200) % AphItems.initialRunes.size());
        return AphItems.initialRunes.get(sprite).getItemSprite(item, perspective);
    }
}
