package aphorea.containers;

import aphorea.registry.AphItems;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;

import java.util.Objects;

public class InitialRuneContainerForm<T extends InitialRuneContainer> extends ContainerForm<T> {

    public FormContainerSlot[] slots;
    public FormInitialRunesList runesList;

    public InitialRuneContainerForm(Client client, final T container) {
        super(client, 408, 140 + 40, container);

        this.addComponent(new FormLocalLabel(new StaticMessage(Localization.translate("item", "initialrune")), new FontOptions(20), -1, 10, 10));

        this.addComponent(this.runesList = new FormInitialRunesList(6, 40, this.getWidth() - 6, this.getHeight() - 46 - 40, client) {
            @Override
            public void onRuneClicked(InventoryItem rune, InputEvent event) {
                this.playTickSound();
                int n = -1;
                for (int i = 0; i < AphItems.initialRunes.size(); i++) {
                    if (Objects.equals(AphItems.initialRunes.get(i).getStringID(), rune.item.getStringID())) {
                        n = i;
                        break;
                    }
                }

                if (n != -1) {
                    container.executeRuneAction.runAndSend(n, 0);
                }
            }
        });

        this.addComponent(new FormLocalLabel(new StaticMessage(Localization.translate("message", "initialrunetip")), new FontOptions(14), -1, 10, this.getHeight() - 46 + 8));


        loadRunes();
    }

    public void loadRunes() {
        this.runesList.setRunes(container.getInitialRunes());
    }
}