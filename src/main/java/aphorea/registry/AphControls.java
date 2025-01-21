package aphorea.registry;

import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.items.runes.AphRunesInjector;
import aphorea.packets.AphRunesInjectorAbilityPacket;
import aphorea.utils.AphColors;
import necesse.engine.GlobalData;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputID;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

import java.util.ArrayList;
import java.util.Arrays;

public class AphControls {
    public static void registerCore() {
        Control.addModControl(new Control(InputID.KEY_G, "runesinjectorability") {
            @Override
            public void activate(InputEvent event) {
                super.activate(event);
                if (isPressed()) {
                    MainGame mainGame = (MainGame) GlobalData.getCurrentState();
                    Client client = mainGame.getClient();
                    if (client != null) {
                        PlayerMob player = client.getPlayer();
                        if (player != null) {
                            MainGameCamera camera = mainGame.getCamera();
                            int mouseLevelX = event.pos.sceneX + camera.getX();
                            int mouseLevelY = event.pos.sceneY + camera.getY();

                            player.buffManager.getBuffs().values().stream().filter(b -> b.buff instanceof AphBaseRuneTrinketBuff).map(b -> (AphBaseRuneTrinketBuff) b.buff).findFirst().ifPresent(
                                    runeBuff -> {
                                        ArrayList<InventoryItem> inventoryItems = player.equipmentBuffManager.getTrinketItems();
                                        boolean notTheRuneOwner = inventoryItems.stream()
                                                .anyMatch(inventoryItem -> {
                                                    if (inventoryItem != null && inventoryItem.item instanceof AphRunesInjector && Arrays.stream(((AphRunesInjector) inventoryItem.item).getBuffs(inventoryItem)).anyMatch(b -> b == runeBuff)) {
                                                        InventoryItem rune = ((AphRunesInjector) inventoryItem.item).getBaseRune(inventoryItem);
                                                        if (rune != null) {
                                                            String runeOwner = rune.getGndData().getString("runeOwner", null);
                                                            return runeOwner != null && !runeOwner.equals(player.playerName);
                                                        }
                                                    }
                                                    return false;
                                                });

                                        if (notTheRuneOwner) {
                                            UniqueFloatText text = new UniqueFloatText(player.getX(), player.getY() - 20, Localization.translate("itemtooltip", "notruneowner"), (new FontOptions(16)).outline().color(AphColors.fail_message), "injectorfail") {
                                                public int getAnchorX() {
                                                    return player.getX();
                                                }

                                                public int getAnchorY() {
                                                    return player.getY() - 20;
                                                }
                                            };
                                            player.getLevel().hudManager.addElement(text);
                                        } else {
                                            client.network.sendPacket(new AphRunesInjectorAbilityPacket(client.getSlot(), mouseLevelX, mouseLevelY, runeBuff));
                                        }
                                    }
                            );
                        }
                    }
                }
            }
        });
    }
}
