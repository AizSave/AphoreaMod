package aphorea.other.itemtype;

import aphorea.registry.AphContainers;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AphRunesInjector extends TrinketItem implements InternalInventoryItemInterface, TickItem {
    protected GameTexture validTexture;
    public int modifierRunesNumber;

    public AphRunesInjector(int modifierRunesNumber) {
        super(Rarity.COMMON, 400);
        this.setItemCategory("misc", "pouches");
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
        this.modifierRunesNumber = modifierRunesNumber;
    }

    public Stream<AphBaseRune> getBaseRunes(InventoryItem item) {
        return getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphBaseRune).map(s -> (AphBaseRune) s.getItem().item);
    }

    public Stream<AphModifierRune> getModifierRunes(InventoryItem item) {
        return getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphModifierRune).map(s -> (AphModifierRune) s.getItem().item);
    }

    public void tick(Inventory inventory, int slot, InventoryItem item, GameClock clock, GameState state, Entity entity, WorldSettings worldSettings, Consumer<InventoryItem> setItem) {
        this.tickInternalInventory(item, clock, state, entity, worldSettings);
    }

    public boolean isValidPouchItem(InventoryItem item, InventoryItem runesInjector) {
        if (item == null || item.item == null) return false;
        if(item.item instanceof AphBaseRune) {
            return !getBaseRunes(runesInjector).findAny().isPresent();
        } else if(item.item instanceof AphModifierRune) {
            return getBaseRunes(runesInjector).count() == 1;
        } else {
            return false;
        }
    }

    public boolean isValidAddItem(InventoryItem item, InventoryItem runesInjector) {
        return this.isValidPouchItem(item, runesInjector);
    }

    public boolean isValidPouchItem(InventoryItem item) {
        if (item == null || item.item == null) return false;
        return item.item instanceof AphBaseRune || item.item instanceof AphModifierRune;
    }

    public boolean isValidAddItem(InventoryItem item) {
        return this.isValidPouchItem(item);
    }

    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (this.validTexture != null && getBaseRunes(item).count() == 1) {
            return new GameSprite(this.validTexture);
        }

        return super.getItemSprite(item, perspective);
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/runesinjector");
        this.validTexture = GameTexture.fromFile("items/runesinjector_valid");
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    public GameMessage getLocalization(InventoryItem item) {
        String pouchName = this.getPouchName(item);
        return pouchName != null ? new StaticMessage(pouchName) : super.getLocalization(item);
    }

    public float getBrokerValue(InventoryItem item) {
        float value = super.getBrokerValue(item);
        Inventory internalInventory = this.getInternalInventory(item);

        for(int i = 0; i < internalInventory.getSize(); ++i) {
            if (!internalInventory.isSlotClear(i)) {
                value += internalInventory.getItem(i).getBrokerValue();
            }
        }

        return value;
    }

    public boolean isValidInjector(InventoryItem inventoryItem) {
        return getBaseRunes(inventoryItem).count() == 1 && !hasDuplicateModifierRunes(inventoryItem);
    }

    public boolean hasDuplicateModifierRunes(InventoryItem item) {
        return getModifierRunes(item)
                .map(Item::getStringID)
                .collect(Collectors.groupingBy(stringID -> stringID, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem inventoryItem) {
        if (isValidInjector(inventoryItem)) {
            int numberOfModifierRunes = (int) getModifierRunes(inventoryItem).count();
            TrinketBuff[] buffs = new TrinketBuff[numberOfModifierRunes + 1];

            getBaseRunes(inventoryItem).findFirst().ifPresent(baseRune -> buffs[0] = baseRune.getBuff());

            Iterator<AphModifierRune> iterator = getModifierRunes(inventoryItem).iterator();
            for (int i = 1; iterator.hasNext(); i++) {
                buffs[i] = iterator.next().getBuff();
            }

            return buffs;
        } else {
            return new TrinketBuff[0];
        }
    }

    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }

            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }

            if (playerSlot != null) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    this.openContainer(client, playerSlot);
                }

                return new ContainerActionResult(-1002911334);
            } else {
                return new ContainerActionResult(208675834, Localization.translate("itemtooltip", "rclickinvopenerror"));
            }
        };
    }

    protected void openContainer(ServerClient client, PlayerInventorySlot inventorySlot) {
        PacketOpenContainer p = new PacketOpenContainer(AphContainers.RUNES_INJECTOR_CONTAINER, ItemInventoryContainer.getContainerContent(this, inventorySlot));
        ContainerRegistry.openAndSendContainer(client, p);
    }

    public int inventoryCanAddItem(Level level, PlayerMob player, InventoryItem item, InventoryItem input, String purpose, boolean isValid, int stackLimit) {
        if (this.isValidAddItem(input, item)) {
            Inventory internalInventory = this.getInternalInventory(item);
            return internalInventory.canAddItem(level, player, input, purpose);
        } else {
            return super.inventoryCanAddItem(level, player, item, input, purpose, isValid, stackLimit);
        }
    }

    @Override
    public int getInternalInventorySize() {
        return 1 + modifierRunesNumber;
    }

    @Override
    public Inventory getInternalInventory(InventoryItem item) {
        return InternalInventoryItemInterface.super.getInternalInventory(item);
    }

    @Override
    public Inventory getNewInternalInventory(InventoryItem item) {
        return InternalInventoryItemInterface.super.getNewInternalInventory(item);
    }

    @Override
    public void tickInternalInventory(InventoryItem item, GameClock clock, GameState state, Entity entity, WorldSettings worldSettings) {
        InternalInventoryItemInterface.super.tickInternalInventory(item, clock, state, entity, worldSettings);
    }

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        InternalInventoryItemInterface.super.saveInternalInventory(item, inventory);
    }

    public boolean isValidItem(InventoryItem item) {
        return item == null || isValidAddItem(item);
    }

    @Override
    public GameTooltips getPickupToggleTooltip(boolean isDisabled) {
        return InternalInventoryItemInterface.super.getPickupToggleTooltip(isDisabled);
    }

    @Override
    public boolean canDisablePickup() {
        return InternalInventoryItemInterface.super.canDisablePickup();
    }

    @Override
    public boolean canQuickStackInventory() {
        return InternalInventoryItemInterface.super.canQuickStackInventory();
    }

    @Override
    public boolean canRestockInventory() {
        return InternalInventoryItemInterface.super.canRestockInventory();
    }

    @Override
    public boolean canSortInventory() {
        return InternalInventoryItemInterface.super.canSortInventory();
    }

    @Override
    public boolean canChangePouchName() {
        return InternalInventoryItemInterface.super.canChangePouchName();
    }

    @Override
    public String getPouchName(InventoryItem item) {
        return InternalInventoryItemInterface.super.getPouchName(item);
    }

    @Override
    public void setPouchName(InventoryItem item, String name) {
        InternalInventoryItemInterface.super.setPouchName(item, name);
    }

    @Override
    public void setPouchPickupDisabled(InventoryItem item, boolean disabled) {
        InternalInventoryItemInterface.super.setPouchPickupDisabled(item, disabled);
    }

    @Override
    public boolean isPickupDisabled(InventoryItem item) {
        return InternalInventoryItemInterface.super.isPickupDisabled(item);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "canequipastrinket"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "line"));
        if(isValidInjector(item)) {
            AphBaseRune baseRune = getBaseRunes(item).findFirst().orElse(null);
            if(baseRune != null) {
                baseRune.addToolTips(tooltips, item, perspective, true);
            } else {
                tooltips.add("Unknown error");
            }
        } else {
            int baseRunesN = (int) getBaseRunes(item).count();
            if(baseRunesN == 0) {
                tooltips.add(Localization.translate("itemtooltip", "requiresbaserune"));
            } else if(baseRunesN > 1) {
                tooltips.add(Localization.translate("itemtooltip", "onlyonebaserune"));
            } else if(hasDuplicateModifierRunes(item)) {
                tooltips.add(Localization.translate("itemtooltip", "duplicatedmodifierrunes"));
            } else {
                tooltips.add("Unknown error");
            }
        }
        tooltips.add(Localization.translate("itemtooltip", "line"));
        return tooltips;
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }
}
