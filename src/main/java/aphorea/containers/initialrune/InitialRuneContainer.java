package aphorea.containers.initialrune;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import aphorea.registry.AphItems;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.PointCustomAction;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class InitialRuneContainer extends Container {
    public final PointCustomAction executeRuneAction;

    public InitialRuneContainer(final NetworkClient client, int uniqueSeed, Packet contentPacket) {
        super(client, uniqueSeed);

        this.executeRuneAction = this.registerAction(new PointCustomAction() {
            protected void run(int runeNumber, int y) {
                ArrayList<InventoryItem> initialRunes = getInitialRunes();
                if (client.isServer() && runeNumber >= 0 && runeNumber < initialRunes.size()) {
                    ServerClient serverClient = client.getServerClient();
                    if (serverClient.playerMob.getInv().removeItems(ItemRegistry.getItem("initialrune"), 1, false, false, false, false, "initialrune") > 0) {
                        AphPlayerData playerData = AphPlayerDataList.getCurrentPlayer(serverClient.playerMob);
                        if (!playerData.runeSelected) {
                            InventoryItem item = initialRunes.get(runeNumber);
                            item.setGndData(new GNDItemMap().setString("runeOwner", playerData.playerName));
                            if (serverClient.playerMob.getInv().addItem(item, true, "initialrune", null)) {
                                playerData.runeSelected = true;
                                serverClient.closeContainer(true);
                            } else {
                                int attempts = 0;
                                while (!serverClient.playerMob.getInv().addItem(new InventoryItem(ItemRegistry.getItem("initialrune")), true, "initialrune", null) && attempts < 20) {
                                    attempts++;
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    public ArrayList<InventoryItem> getInitialRunes() {
        return AphItems.initialRunes.stream().map(InventoryItem::new).collect(Collectors.toCollection(ArrayList::new));
    }
}