package aphorea.items.consumable;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import aphorea.items.vanillaitemtypes.AphConsumableItem;
import aphorea.registry.AphContainers;
import aphorea.registry.AphItems;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
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

import java.awt.geom.Line2D;

public class InitialRune extends AphConsumableItem {
    public InitialRune() {
        super(1, false);
        this.rarity = Rarity.UNIQUE;
        this.itemCooldownTime.setBaseValue(1000);
        this.worldDrawSize = 32;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
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

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(player);
        return !playerData.runeSelected ? null : Localization.translate("message", "alreadyselectedinitialrune");
    }

    @Override
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

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "initialrune");
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (perspective != null) {
            int sprite = (int) ((perspective.getWorldTime() / 200) % AphItems.initialRunes.size());
            return AphItems.initialRunes.get(sprite).getItemSprite(item, perspective);
        } else {
            return AphItems.initialRunes.get(0).getItemSprite(item, null);
        }
    }
}
