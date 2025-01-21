package aphorea.containers;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.InternalInventoryItemContainerSlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class RunesInjectorContainer extends Container {
    public final Inventory inventory;
    public final int itemID;
    public final InternalInventoryItemInterface inventoryItem;
    public final PlayerInventorySlot inventoryItemSlot;
    private InventoryItem item;
    public int INVENTORY_START = -1;
    public int INVENTORY_END = -1;

    public RunesInjectorContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        this.itemID = reader.getNextShortUnsigned();
        int itemInventoryID = reader.getNextInt();
        int itemInventorySlot = reader.getNextInt();
        this.inventoryItemSlot = new PlayerInventorySlot(itemInventoryID, itemInventorySlot);
        this.item = this.inventoryItemSlot.getItem(client.playerMob.getInv());
        if (this.item != null && this.item.item.getID() == this.itemID && this.item.item instanceof InternalInventoryItemInterface) {
            this.inventoryItem = (InternalInventoryItemInterface) this.item.item;
            this.lockSlot(this.inventoryItemSlot);
            InternalInventoryItemInterface internalInventoryItemInterface = (InternalInventoryItemInterface) this.item.item;
            this.inventory = internalInventoryItemInterface.getInternalInventory(this.item);

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                int index = this.addSlot(this.getItemContainerSlot(this.inventory, i, internalInventoryItemInterface));
                if (this.INVENTORY_START == -1) {
                    this.INVENTORY_START = index;
                }

                if (this.INVENTORY_END == -1) {
                    this.INVENTORY_END = index;
                }

                this.INVENTORY_START = Math.min(this.INVENTORY_START, index);
                this.INVENTORY_END = Math.max(this.INVENTORY_END, index);
            }

            this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
        } else {
            this.inventoryItem = null;
            this.inventory = null;
        }
    }

    public ContainerSlot getItemContainerSlot(Inventory inventory, int slot, InternalInventoryItemInterface internalInventoryItemInterface) {
        return new InternalInventoryItemContainerSlot(inventory, slot, internalInventoryItemInterface);
    }

    public void tick() {
        super.tick();
        InventoryItem item;
        if (this.client.isClient()) {
            if (this.inventory == null) {
                this.client.getClientClient().getClient().closeContainer(true);
                return;
            }

            item = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (this.item != item && item != null) {
                if (!(item.item instanceof InternalInventoryItemInterface)) {
                    this.client.getClientClient().getClient().closeContainer(true);
                    return;
                }

                this.inventory.override(((InternalInventoryItemInterface) item.item).getInternalInventory(item));
                this.item = item;
            }
        }

        if (this.inventory.isDirty()) {
            item = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (item != null) {
                ((InternalInventoryItemInterface) item.item).saveInternalInventory(item, this.inventory);
            }

            this.inventory.clean();
            this.inventoryItemSlot.markDirty(this.client.playerMob.getInv());
        }

    }

    public InventoryItem getInventoryItem() {
        return this.item;
    }

    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        } else if (this.inventoryItemSlot == null) {
            return false;
        } else {
            InventoryItem invItem = this.inventoryItemSlot.getItem(client.playerMob.getInv());
            return invItem != null && invItem.item.getID() == this.itemID && invItem.item instanceof InternalInventoryItemInterface;
        }
    }
}
