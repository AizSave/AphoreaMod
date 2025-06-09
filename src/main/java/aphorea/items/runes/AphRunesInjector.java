package aphorea.items.runes;

import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import aphorea.registry.AphContainers;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ItemRegistry;
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
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AphRunesInjector extends TrinketItem implements InternalInventoryItemInterface, TickItem {
    protected GameTexture validTexture;
    public int modifierRunesNumber;
    public int baseRunesNumber;
    public int tooltipsNumber;

    public AphRunesInjector(Rarity rarity, int extraToolTips, int modifierRunesNumber) {
        super(rarity, 400);
        this.setItemCategory(ItemCategory.craftingManager, "runes", "runesinjectors");
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;

        this.tooltipsNumber = extraToolTips;
        this.modifierRunesNumber = modifierRunesNumber;
        this.baseRunesNumber = 0;

        ItemRegistry.getItems().forEach(
                i -> {
                    if (i instanceof AphRunesInjector) {
                        this.disables.add(i.getStringID());
                    }
                }
        );
    }

    public AphRunesInjector(Rarity rarity, int extraToolTips, int modifierRunesNumber, int baseRunesNumber) {
        this(rarity, modifierRunesNumber, extraToolTips);

        this.baseRunesNumber = baseRunesNumber;
    }

    public List<AphBaseRune> getBaseRunes(InventoryItem item) {
        return getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphBaseRune).map(s -> (AphBaseRune) s.getItem().item).collect(Collectors.toList());
    }

    public InventoryItem getBaseRune(InventoryItem item) {
        return getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphBaseRune).map(InventorySlot::getItem).findFirst().orElse(null);
    }

    public List<AphModifierRune> getModifierRunes(InventoryItem item) {
        return getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphModifierRune).map(s -> (AphModifierRune) s.getItem().item).collect(Collectors.toList());
    }

    public List<AphModifierRuneTrinketBuff> getModifierBuffs(InventoryItem item) {
        List<AphModifierRuneTrinketBuff> modifierBuffs = getInternalInventory(item)
                .streamSlots()
                .filter(s -> s.getItem() != null && s.getItem().item instanceof AphModifierRune)
                .map(s -> ((AphModifierRune) s.getItem().item).getBuff())
                .collect(Collectors.toList());

        modifierBuffs.add(getBuff());

        return modifierBuffs;
    }

    public void tick(Inventory inventory, int slot, InventoryItem item, GameClock clock, GameState state, Entity entity, WorldSettings worldSettings, Consumer<InventoryItem> setItem) {
        this.tickInternalInventory(item, clock, state, entity, worldSettings);
    }

    public boolean isValidPouchItem(InventoryItem item, InventoryItem runesInjector) {
        if (item == null || item.item == null) return false;
        return item.item instanceof AphBaseRune || item.item instanceof AphModifierRune;
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
        if (this.validTexture != null && isInvalidInjector(item, perspective) == null) {
            return new GameSprite(this.validTexture);
        }

        return super.getItemSprite(item, perspective);
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/runes/" + this.getStringID());
        this.validTexture = GameTexture.fromFile("items/runes/" + this.getStringID() + "_valid");
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

        for (int i = 0; i < internalInventory.getSize(); ++i) {
            if (!internalInventory.isSlotClear(i)) {
                value += internalInventory.getItem(i).getBrokerValue();
            }
        }

        return value;
    }

    public String isInvalidInjector(InventoryItem inventoryItem, PlayerMob player) {
        String error = isInvalidInjector(inventoryItem);
        if (error != null) {
            return error;
        }
        InventoryItem baseRune = getBaseRune(inventoryItem);
        if (baseRune == null) {
            return "requiresbaserune";
        }
        String runeOwner = baseRune.getGndData().getString("runeOwner", null);
        if (runeOwner != null && !Objects.equals(player.playerName, runeOwner)) {
            return "notruneowner";
        }
        return null;
    }

    public String isInvalidInjector(InventoryItem inventoryItem) {
        int baseRunes = getBaseRunes(inventoryItem).size();
        if (baseRunes == 0) {
            return "requiresbaserune";
        } else if (baseRunes > 1) {
            return "onlyonebaserune";
        } else if (hasDuplicateModifierRunes(inventoryItem)) {
            return "duplicatedmodifierrunes";
        }
        return null;
    }


    public boolean hasDuplicateModifierRunes(InventoryItem item) {
        return getModifierRunes(item).stream()
                .map(Item::getStringID)
                .collect(Collectors.groupingBy(stringID -> stringID, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem inventoryItem) {
        if (isInvalidInjector(inventoryItem) == null) {
            List<AphModifierRuneTrinketBuff> modifierBuffs = getModifierBuffs(inventoryItem);
            TrinketBuff[] buffs = new TrinketBuff[modifierBuffs.size() + 1];

            buffs[0] = getBaseRunes(inventoryItem).get(0).getTrinketBuff();

            Iterator<AphModifierRuneTrinketBuff> iterator = modifierBuffs.iterator();
            for (int i = 1; iterator.hasNext(); i++) {
                buffs[i] = iterator.next();
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
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "runesslots", "modifierslots", modifierRunesNumber));
        addToolTips(tooltips, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "line"));
        String invalid = isInvalidInjector(item, perspective);
        if (invalid == null) {
            AphBaseRune baseRune = getBaseRunes(item).get(0);
            if (baseRune != null) {
                baseRune.addToolTips(tooltips, item, this, perspective, true);
                tooltips.add(Localization.translate("itemtooltip", "useruneinfusor"));
            } else {
                tooltips.add("Unknown error");
            }
        } else {
            tooltips.add(Localization.translate("itemtooltip", invalid));

        }
        tooltips.add(Localization.translate("itemtooltip", "line"));
        getBaseRunes(item).forEach(
                rune -> tooltips.add("§i[B]§0 " + Localization.translate("item", rune.getStringID()))
        );
        getModifierRunes(item).forEach(
                rune -> tooltips.add("§a[M]§0 " + Localization.translate("item", rune.getStringID()))
        );

        return tooltips;
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return null;
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "runesinjector");
    }

    public AphModifierRuneTrinketBuff getBuff() {
        return (AphModifierRuneTrinketBuff) BuffRegistry.getBuff(getStringID());
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective) {
        AphModifierRuneTrinketBuff buff = getBuff();
        float effectNumberVariation = buff.getEffectNumberVariation();
        float effectCooldownVariation = buff.getCooldownVariation();
        float healthCost = buff.getHealthCost();

        if (effectNumberVariation > 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaseruneeffectnumber", "variation", Math.round(effectNumberVariation * 100)));
        } else if (effectNumberVariation < 0) {
            tooltips.add(Localization.translate("itemtooltip", "decreaseruneeffectnumber", "variation", Math.round(-effectNumberVariation * 100)));
        }

        if (effectCooldownVariation > 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaserunecooldown", "variation", Math.round(effectCooldownVariation * 100)));
        } else if (effectCooldownVariation < 0) {
            tooltips.add(Localization.translate("itemtooltip", "decreaserunecooldown", "variation", Math.round(-effectCooldownVariation * 100)));
        }

        if (healthCost > 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaserunehealthcost", "health", Math.round(healthCost * 100)));
        } else if (healthCost < 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaserunehealthhealing", "health", Math.round(-healthCost * 100)));
        }

        for (int i = 0; i < tooltipsNumber; i++) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
            tooltips.add(Localization.translate("itemtooltip", getStringID() + "_mod" + tooltipNumber));
        }
    }

    public int getTooltipsNumber() {
        return tooltipsNumber;
    }
}
