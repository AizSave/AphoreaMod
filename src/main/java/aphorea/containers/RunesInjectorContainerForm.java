package aphorea.containers;

import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class RunesInjectorContainerForm<T extends RunesInjectorContainer> extends ContainerForm<T> {
    public FormLabelEdit label;
    public FormContainerSlot[] slots;


    public RunesInjectorContainerForm(Client client, final T container) {
        super(client, 408, 100, container);
        InventoryItem inventoryItem = container.getInventoryItem();
        InternalInventoryItemInterface item = container.inventoryItem;
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.addComponent(new FormLabelEdit(inventoryItem == null ? "NULL" : inventoryItem.getItemDisplayName(), labelOptions, Settings.UI.activeTextColor, 5, 5, this.getWidth() - 10, 50), -1000);

        FormFlow flow = new FormFlow(34);
        this.addSlots(flow);
        flow.next(4);
        this.setHeight(flow.next());
    }

    protected void addSlots(FormFlow flow) {
        this.slots = new FormContainerSlot[(this.container).INVENTORY_END - (this.container).INVENTORY_START + 1];
        int currentY = flow.next();

        for (int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + (this.container).INVENTORY_START;
            int x = i % 10;
            if (x == 0) {
                currentY = flow.next(40);
            }

            this.slots[i] = this.addComponent(new FormContainerSlot(this.client, this.container, slotIndex, 4 + x * 40, currentY));
        }
    }
}
