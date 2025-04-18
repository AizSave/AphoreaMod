package aphorea.items.misc;

import aphorea.AphResources;
import aphorea.containers.book.BookContainerForm;
import aphorea.items.vanillaitemtypes.AphMiscItem;
import aphorea.registry.AphContainers;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AphWrittenBook extends AphMiscItem implements ItemInteractAction {
    public BookPage[] content;

    public AphWrittenBook(BookPage... content) {
        super(1);
        this.content = content;
    }

    public static AphWrittenBook getBook(int bookID) {
        return (AphWrittenBook) ItemRegistry.getItem(bookID);
    }

    public String getTitle() {
        return ItemRegistry.getDisplayName(this.getID());
    }

    @Override
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
                if (container.getClient().isClient() && BookContainerForm.bookID != this.getID()) {
                    BookContainerForm.bookID = this.getID();
                    BookContainerForm.page = 1;
                }
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    PacketOpenContainer p = new PacketOpenContainer(AphContainers.BOOK_CONTAINER);
                    ContainerRegistry.openAndSendContainer(client, p);
                }

                return new ContainerActionResult(1328013989);
            } else {
                return new ContainerActionResult(60840742, Localization.translate("itemtooltip", "rclickinvopenerror"));
            }
        };
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            if (attackerMob.isClient() && BookContainerForm.bookID != this.getID()) {
                BookContainerForm.bookID = this.getID();
                BookContainerForm.page = 1;
            }

            if (attackerMob.isServer()) {
                ServerClient client = ((PlayerMob) attackerMob).getServerClient();
                PacketOpenContainer p = new PacketOpenContainer(AphContainers.BOOK_CONTAINER);
                ContainerRegistry.openAndSendContainer(client, p);
            }

        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            if (attackerMob.isClient() && BookContainerForm.bookID != this.getID()) {
                BookContainerForm.bookID = this.getID();
                BookContainerForm.page = 1;
            }

            if (attackerMob.isServer()) {
                ServerClient client = ((PlayerMob) attackerMob).getServerClient();
                PacketOpenContainer p = new PacketOpenContainer(AphContainers.BOOK_CONTAINER);
                ContainerRegistry.openAndSendContainer(client, p);
            }

        }
        return ItemInteractAction.super.onLevelInteract(level, x, y, attackerMob, attackHeight, item, slot, seed, mapContent);
    }

    @Override
    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1000;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "rclickopentip"));
        return tooltips;
    }

    public static class BookPage {
        public PageInstruction[] pageInstructions;

        public BookPage(PageInstruction... pageInstructions) {
            this.pageInstructions = pageInstructions;
        }
    }

    abstract public static class PageInstruction {
        public static int rightMargin = 30;

        abstract public int execute(int y, FormContentBox pageContent);

        public int topPadding() {
            return 0;
        }

        public int bottomPadding() {
            return 0;
        }
    }

    public static class PageText extends PageInstruction {
        public String text;

        public PageText(String text) {
            this.text = text;
        }

        public FontOptions getFontOptions() {
            return new FontOptions(getFontSize()).color(Settings.UI.activeTextColor).alphaf(0.8F);
        }

        public int getFontSize() {
            return 14;
        }

        @Override
        public int execute(int y, FormContentBox pageContent) {
            FormLabel textLabel;
            pageContent.addComponent(textLabel = new FormLabel(processTranslationTags(text), getFontOptions(), -1, 0, y, pageContent.getWidth() - rightMargin));

            return textLabel.getHeight();
        }

        @Override
        public int bottomPadding() {
            return 16;
        }
    }

    public static class PageHeader1 extends PageText {
        public PageHeader1(String text) {
            super(text);
        }

        @Override
        public int topPadding() {
            return 32;
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(getFontSize()).color(Settings.UI.activeTextColor);
        }

        @Override
        public int getFontSize() {
            return 24;
        }
    }

    public static class PageHeader2 extends PageHeader1 {
        public PageHeader2(String text) {
            super(text);
        }

        @Override
        public int getFontSize() {
            return 20;
        }
    }

    public static class PageImage extends PageInstruction {
        public String imageID;

        public PageImage(String imageID) {
            this.imageID = imageID;
        }

        @Override
        public int execute(int y, FormContentBox pageContent) {
            GameTexture gameTexture = AphResources.bookTextures.get(imageID);
            pageContent.addComponent(new FormContentBox(0, y, gameTexture.getWidth(), gameTexture.getHeight()) {
                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    super.draw(tickManager, perspective, renderBox);
                    gameTexture.initDraw().draw(getX(), getY());
                }
            });

            return gameTexture.getHeight();
        }

        @Override
        public int bottomPadding() {
            return 16;
        }
    }

    @NotNull
    public static String processTranslationTags(String input) {
        Pattern pattern = Pattern.compile("\\[translate:([^.\\]]+)\\.([^]]+)]");
        Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String category = matcher.group(1);
            String translationKey = matcher.group(2);
            String translation = Localization.translate(category, translationKey);
            matcher.appendReplacement(result, Matcher.quoteReplacement(translation));
        }
        matcher.appendTail(result);

        return result.toString();
    }

}
