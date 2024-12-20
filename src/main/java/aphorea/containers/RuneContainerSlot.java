package aphorea.containers;

import aphorea.other.itemtype.AphBaseRune;
import aphorea.other.itemtype.AphModifierRune;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.*;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

import java.awt.*;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;

public class RuneContainerSlot extends FormComponent implements FormPositionContainer {
    private FormPosition position;
    private boolean active;
    protected Client client;
    protected Container container;
    protected int containerSlotIndex;
    public boolean isSelected;
    protected GameSprite decal;
    public boolean drawDecalWhenOccupied;
    private boolean isHovering;

    public RuneContainerSlot(Client client, Container container, int containerSlotIndex, int x, int y) {
        this.drawDecalWhenOccupied = false;
        this.client = client;
        this.container = container;
        this.containerSlotIndex = containerSlotIndex;
        if (container != null && this.getContainerSlot() == null) {
            throw new IllegalArgumentException("Container slot with index " + containerSlotIndex + " does not exist in container");
        } else {
            this.position = new FormFixedPosition(x, y);
            this.setActive(true);
        }
    }

    /** @deprecated */
    @Deprecated
    public RuneContainerSlot(Client client, int containerSlotIndex, int x, int y) {
        this(client, (Container)null, containerSlotIndex, x, y);
    }

    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        this.handleMouseMoveEvent(event);
        this.handleActionInputEvents(event);
    }

    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        this.handleActionControllerEvents(event);
    }

    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    protected void handleMouseMoveEvent(InputEvent event) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }

    }

    protected void handleActionInputEvents(InputEvent event) {
        if (event.state && !event.isKeyboardEvent()) {
            GameWindow window = WindowManager.getWindow();
            ContainerAction action = null;
            SelectionFloatMenu menu;
            if (event.getID() == -100) {
                if (this.isMouseOver(event) && (!window.isKeyDown(340) || !FormTypingComponent.appendItemToTyping(this.getContainerSlot().getItem()))) {
                    if (Control.INV_QUICK_MOVE.isDown()) {
                        this.runAction(ContainerAction.QUICK_MOVE, event.shouldSubmitSound());
                    } else if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash()) {
                        this.runAction(ContainerAction.QUICK_TRASH, event.shouldSubmitSound());
                    } else if (Control.INV_QUICK_DROP.isDown() && !this.getContainerSlot().isItemLocked()) {
                        this.runAction(ContainerAction.QUICK_DROP, event.shouldSubmitSound());
                    } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                        this.runAction(ContainerAction.TOGGLE_LOCKED, event.shouldSubmitSound());
                    } else {
                        menu = new SelectionFloatMenu(this) {
                            public void draw(TickManager tickManager, PlayerMob perspective) {
                                if (!RuneContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                    this.remove();
                                }

                                super.draw(tickManager, perspective);
                            }
                        };
                        menu.setCreateEvent(event);
                        this.addLeftClickActions(menu);
                        if (!menu.isEmpty()) {
                            if (!this.getContainerSlot().isClear()) {
                                menu.add(Localization.translate("ui", "slottakefull"), () -> {
                                    this.runAction(ContainerAction.LEFT_CLICK, false);
                                    menu.remove();
                                });
                            }

                            this.getManager().openFloatMenu(menu);
                            this.playTickSound();
                        } else {
                            this.runAction(ContainerAction.LEFT_CLICK, event.shouldSubmitSound());
                        }
                    }
                }
            } else if (event.getID() != -99 && !event.isRepeatEvent(this)) {
                if (event.getID() == -102) {
                    if (this.isMouseOver(event)) {
                        this.runAction(ContainerAction.QUICK_MOVE_ONE, event.shouldSubmitSound());
                    }
                } else if (event.getID() == -103 && this.isMouseOver(event)) {
                    this.runAction(ContainerAction.QUICK_GET_ONE, event.shouldSubmitSound());
                }
            } else if (this.isMouseOver(event)) {
                InventoryItem invItem;
                if (Control.INV_QUICK_MOVE.isDown()) {
                    ContainerSlot containerSlot = this.getContainerSlot();
                    invItem = containerSlot.getItem();
                    int itemID = invItem == null ? -1 : invItem.item.getID();
                    if (event.getID() == -99 || event.isRepeatEvent(new Object[]{this, ContainerAction.TAKE_ONE, itemID})) {
                        if (itemID != -1) {
                            event.startRepeatEvents(new Object[]{this, ContainerAction.TAKE_ONE, itemID});
                        }

                        this.runAction(ContainerAction.TAKE_ONE, event.shouldSubmitSound());
                    }
                } else if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash()) {
                    this.runAction(ContainerAction.QUICK_TRASH_ONE, event.shouldSubmitSound());
                } else if (Control.INV_QUICK_DROP.isDown() && !this.getContainerSlot().isItemLocked()) {
                    this.runAction(ContainerAction.QUICK_DROP_ONE, event.shouldSubmitSound());
                } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                    this.runAction(ContainerAction.TOGGLE_LOCKED, event.shouldSubmitSound());
                } else {
                    menu = new SelectionFloatMenu(this) {
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!RuneContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }

                            super.draw(tickManager, perspective);
                        }
                    };
                    menu.setCreateEvent(event);
                    this.addRightClickActions(menu);
                    Supplier rAction;
                    if (!menu.isEmpty()) {
                        if (!this.getContainerSlot().isClear()) {
                            invItem = this.getContainerSlot().getItem();
                            rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                            if (rAction != null) {
                                String tip = invItem.item.getInventoryRightClickControlTip(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                                if (tip != null) {
                                    menu.add(tip, () -> {
                                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                        menu.remove();
                                    });
                                } else {
                                    menu.add(Localization.translate("ui", "slotuse"), () -> {
                                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                        menu.remove();
                                    });
                                }
                            } else {
                                menu.add(Localization.translate("ui", "slotsplit"), () -> {
                                    this.runAction(ContainerAction.RIGHT_CLICK, false);
                                    menu.remove();
                                });
                            }
                        }

                        this.getManager().openFloatMenu(menu);
                        this.playTickSound();
                    } else {
                        invItem = this.getContainerSlot().getItem();
                        rAction = invItem == null ? null : invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (rAction != null) {
                            this.runAction(ContainerAction.RIGHT_CLICK_ACTION, event.shouldSubmitSound());
                        } else {
                            this.runAction(ContainerAction.RIGHT_CLICK, event.shouldSubmitSound());
                        }
                    }
                }
            }

            if (action != null) {
                ContainerActionResult result = this.getContainer().applyContainerAction(this.containerSlotIndex, (ContainerAction)action);
                this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, (ContainerAction)action, result.value));
                if (result.error != null) {
                    Renderer.hudManager.addElement(new UniqueScreenFloatText(window.mousePos().hudX, window.mousePos().hudY, result.error, (new FontOptions(16)).outline(), "slotError"));
                }

                if ((result.value != 0 || result.error != null) && event.shouldSubmitSound()) {
                    this.playTickSound();
                }
            }

        }
    }

    protected void handleActionControllerEvents(ControllerEvent event) {
        if (event.buttonState) {
            if (this.isControllerFocus()) {
                SelectionFloatMenu menu;
                ControllerFocus currentFocus;
                if (event.getState() == ControllerInput.MENU_SELECT) {
                    menu = new SelectionFloatMenu(this) {
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!RuneContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }

                            super.draw(tickManager, perspective);
                        }
                    };
                    this.addLeftClickActions(menu);
                    if (!menu.isEmpty()) {
                        if (!this.getContainerSlot().isClear()) {
                            menu.add(Localization.translate("ui", "slottakefull"), () -> {
                                this.runAction(ContainerAction.LEFT_CLICK, false);
                                menu.remove();
                            });
                        }

                        currentFocus = this.getManager().getCurrentFocus();
                        if (currentFocus != null) {
                            this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                        } else {
                            this.getManager().openFloatMenu(menu);
                        }
                    } else {
                        this.runAction(ContainerAction.LEFT_CLICK, event.shouldSubmitSound());
                    }

                    event.use();
                } else if (event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) {
                    currentFocus = this.getManager().getCurrentFocus();
                    if (currentFocus != null) {
                        menu = new SelectionFloatMenu(this) {
                            public void draw(TickManager tickManager, PlayerMob perspective) {
                                if (!RuneContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                    this.remove();
                                }

                                super.draw(tickManager, perspective);
                            }
                        };
                        this.addRightClickActions(menu);
                        if (!this.getContainerSlot().isClear()) {
                            menu.add(Localization.translate("ui", "slottransfer"), () -> {
                                this.runAction(ContainerAction.QUICK_MOVE, false);
                                menu.remove();
                            });
                            InventoryItem invItem = this.getContainerSlot().getItem();
                            Supplier<ContainerActionResult> rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                            if (rAction != null) {
                                String tip = invItem.item.getInventoryRightClickControlTip(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                                if (tip != null) {
                                    menu.add(tip, () -> {
                                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                        menu.remove();
                                    });
                                } else {
                                    menu.add(Localization.translate("ui", "slotuse"), () -> {
                                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                        menu.remove();
                                    });
                                }
                            } else {
                                menu.add(Localization.translate("ui", "slotsplit"), () -> {
                                    this.runAction(ContainerAction.RIGHT_CLICK, false);
                                    menu.remove();
                                });
                            }

                            menu.add(Localization.translate("ui", this.getContainerSlot().isItemLocked() ? "slotunlock" : "slotlock"), () -> {
                                this.runAction(ContainerAction.TOGGLE_LOCKED, false);
                                menu.remove();
                            });
                            if (!this.getContainerSlot().isItemLocked()) {
                                menu.add(Localization.translate("ui", "slottrash"), () -> {
                                    this.runAction(ContainerAction.QUICK_TRASH, false);
                                    menu.remove();
                                });
                            }

                            menu.add(Localization.translate("ui", "slottakeone"), () -> {
                                this.runAction(ContainerAction.TAKE_ONE, false);
                                menu.remove();
                            });
                            if (!this.getContainerSlot().isItemLocked()) {
                                menu.add(Localization.translate("ui", "slotdrop"), () -> {
                                    this.runAction(ContainerAction.QUICK_DROP, false);
                                    menu.remove();
                                });
                            }
                        }

                        if (!menu.isEmpty()) {
                            this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                            this.playTickSound();
                        }
                    }

                    event.use();
                } else if (event.getState() == ControllerInput.MENU_INTERACT_ITEM) {
                    menu = new SelectionFloatMenu(this) {
                        public void draw(TickManager tickManager, PlayerMob perspective) {
                            if (!RuneContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                                this.remove();
                            }

                            super.draw(tickManager, perspective);
                        }
                    };
                    this.addRightClickActions(menu);
                    InventoryItem invItem;
                    Supplier rAction;
                    if (!menu.isEmpty()) {
                        if (!this.getContainerSlot().isClear()) {
                            invItem = this.getContainerSlot().getItem();
                            rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                            if (rAction != null) {
                                String tip = invItem.item.getInventoryRightClickControlTip(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                                if (tip != null) {
                                    menu.add(tip, () -> {
                                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                        menu.remove();
                                    });
                                } else {
                                    menu.add(Localization.translate("ui", "slotuse"), () -> {
                                        this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                                        menu.remove();
                                    });
                                }
                            } else {
                                menu.add(Localization.translate("ui", "slotsplit"), () -> {
                                    this.runAction(ContainerAction.RIGHT_CLICK, false);
                                    menu.remove();
                                });
                            }
                        }

                        currentFocus = this.getManager().getCurrentFocus();
                        if (currentFocus != null) {
                            this.getManager().openFloatMenuAt(menu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + currentFocus.boundingBox.height);
                        } else {
                            this.getManager().openFloatMenu(menu);
                        }

                        this.playTickSound();
                    } else if (!this.getContainerSlot().isClear()) {
                        invItem = this.getContainerSlot().getItem();
                        rAction = invItem.item.getInventoryRightClickAction(this.getContainer(), invItem, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (rAction != null) {
                            this.runAction(ContainerAction.RIGHT_CLICK_ACTION, event.shouldSubmitSound());
                        } else {
                            this.runAction(ContainerAction.RIGHT_CLICK, event.shouldSubmitSound());
                        }
                    } else {
                        this.runAction(ContainerAction.RIGHT_CLICK, event.shouldSubmitSound());
                    }

                    event.use();
                } else if (event.getState() == ControllerInput.MENU_QUICK_TRANSFER) {
                    this.runAction(ContainerAction.QUICK_MOVE, event.shouldSubmitSound());
                    event.use();
                } else if (event.getState() == ControllerInput.MENU_QUICK_TRASH) {
                    this.runAction(ContainerAction.QUICK_TRASH, event.shouldSubmitSound());
                    event.use();
                } else if (event.getState() == ControllerInput.MENU_DROP_ITEM) {
                    this.runAction(ContainerAction.QUICK_DROP, event.shouldSubmitSound());
                    event.use();
                } else if (event.getState() == ControllerInput.MENU_LOCK_ITEM) {
                    this.runAction(ContainerAction.TOGGLE_LOCKED, event.shouldSubmitSound());
                    event.use();
                } else if (event.getState() == ControllerInput.MENU_MOVE_ONE_ITEM) {
                    this.runAction(ContainerAction.QUICK_MOVE_ONE, event.shouldSubmitSound());
                    event.use();
                } else if (event.getState() == ControllerInput.MENU_GET_ONE_ITEM) {
                    this.runAction(ContainerAction.QUICK_GET_ONE, event.shouldSubmitSound());
                    event.use();
                }
            }

        }
    }

    protected void runAction(ContainerAction action, boolean playSound) {
        ContainerActionResult result = this.getContainer().applyContainerAction(this.containerSlotIndex, action);
        this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, action, result.value));
        if (result.error != null) {
            ControllerFocus currentFocus = this.getManager().getCurrentFocus();
            if (currentFocus != null && Input.lastInputIsController) {
                Renderer.hudManager.addElement(new UniqueScreenFloatText(currentFocus.boundingBox.x + currentFocus.boundingBox.width / 2, currentFocus.boundingBox.y, result.error, (new FontOptions(16)).outline(), "slotError"));
            } else {
                GameWindow window = WindowManager.getWindow();
                Renderer.hudManager.addElement(new UniqueScreenFloatText(window.mousePos().hudX, window.mousePos().hudY, result.error, (new FontOptions(16)).outline(), "slotError"));
            }
        }

        if ((result.value != 0 || result.error != null) && playSound) {
            this.playTickSound();
        }

    }

    protected void addLeftClickActions(SelectionFloatMenu menu) {
    }

    protected void addRightClickActions(SelectionFloatMenu menu) {
    }

    public boolean canCurrentlyLockItem() {
        return true;
    }

    public boolean canCurrentlyQuickTrash() {
        return true;
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isHovering() && !this.getContainerSlot().isClear()) {
            if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash() && !this.getContainerSlot().isItemLocked()) {
                Renderer.setCursor(GameWindow.CURSOR.TRASH);
            } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem() && this.getContainerSlot().canLockItem()) {
                if (this.getContainerSlot().isItemLocked()) {
                    Renderer.setCursor(GameWindow.CURSOR.UNLOCK);
                } else {
                    Renderer.setCursor(GameWindow.CURSOR.LOCK);
                }
            }
        }

        Color drawCol = this.getDrawColor();
        InventoryItem item = this.getContainerSlot().getItem();
        HoverStateTextures slotTextures = item != null && item.isNew() ? Settings.UI.inventoryslot_big_new : Settings.UI.inventoryslot_big;
        GameTexture slotTexture = this.isHovering() ? slotTextures.highlighted : slotTextures.active;
        slotTexture.initDraw().color(drawCol).draw(this.getX(), this.getY());
        this.drawDecal(perspective);
        if (item != null) {
            if(item.item instanceof AphBaseRune || item.item instanceof AphModifierRune) {
                int x = this.getX() + 4;
                int y = this.getY() + 4;

                GameSprite itemSprite;
                if(item.item instanceof AphBaseRune) {
                    AphBaseRune rune = (AphBaseRune) item.item;
                    itemSprite = new GameSprite(rune.validTexture);
                } else {
                    AphModifierRune rune = (AphModifierRune) item.item;
                    itemSprite = new GameSprite(rune.validTexture);
                }
                itemSprite.initDraw().color(item.item.getDrawColor(item, perspective)).size(32).draw(x, y);

                int amount = item.getAmount();
                if (amount > 1) {
                    String amountString;
                    if (amount > 9999) {
                        amountString = GameUtils.metricNumber(amount, 2, true, RoundingMode.FLOOR, (String)null);
                    } else {
                        amountString = "" + amount;
                    }

                    FontOptions options = Item.tipFontOptions;
                    int width = FontManager.bit.getWidthCeil(amountString, options);
                    FontManager.bit.drawString((float)(x + 32 - width), (float)y, amountString, options);
                }
            } else {
                item.draw(perspective, this.getX() + 4, this.getY() + 4);
            }
            if (this.isHovering()) {
                item.setNew(false);
                Input input = WindowManager.getWindow().getInput();
                if (!input.isKeyDown(-100) && !input.isKeyDown(-99)) {
                    this.addItemTooltips(item, perspective);
                }
            }
        } else if (this.isHovering()) {
            this.addClearTooltips(perspective);
        }

        if (this.getContainerSlot().isItemLocked()) {
            Settings.UI.note_locked.initDraw().draw(this.getX() + 5, this.getY() + 35 - Settings.UI.note_locked.getHeight());
        }

    }

    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), new ControllerInputState[]{ControllerInput.MENU_SELECT});
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "slotactions"), new ControllerInputState[]{ControllerInput.MENU_ITEM_ACTIONS_MENU});
    }

    public Color getDecalDrawColor() {
        if (!this.isActive()) {
            return Settings.UI.inactiveFadedTextColor;
        } else if (this.getContainer().isSlotLocked(this.getContainerSlot())) {
            return Settings.UI.inactiveFadedTextColor;
        } else {
            return !this.isHovering() && !this.isSelected ? Settings.UI.activeFadedTextColor : Settings.UI.highlightFadedTextColor;
        }
    }

    public void drawDecal(PlayerMob perspective) {
        if (this.decal != null) {
            if (!this.drawDecalWhenOccupied && !this.getContainerSlot().isClear()) {
                return;
            }

            this.decal.initDraw().color(this.getDecalDrawColor()).draw(this.getX() + 20 - this.decal.width / 2, this.getY() + 20 - this.decal.height / 2);
        }

    }

    public List<Rectangle> getHitboxes() {
        return singleBox(new Rectangle(this.getX() + 2, this.getY() + 2, 36, 36));
    }

    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        return item.getTooltip(perspective, new GameBlackboard());
    }

    public void addItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(this.getItemTooltip(item, perspective));
        if (Settings.showControlTips) {
            String rightControlTip = item.item.getInventoryRightClickControlTip(this.getContainer(), item, this.containerSlotIndex, this.getContainerSlot());
            if (rightControlTip != null) {
                if (Input.lastInputIsController) {
                    tooltips.add(new InputTooltip(ControllerInput.MENU_INTERACT_ITEM, rightControlTip));
                } else {
                    tooltips.add(new InputTooltip(-99, rightControlTip));
                }
            }
        }

        GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
    }

    public GameTooltips getClearTooltips() {
        return null;
    }

    public void addClearTooltips(PlayerMob perspective) {
        GameTooltips clearTooltips = this.getClearTooltips();
        if (clearTooltips != null) {
            GameTooltipManager.addTooltip(clearTooltips, TooltipLocation.FORM_FOCUS);
        }

    }

    public Color getDrawColor() {
        if (!this.isActive()) {
            return Settings.UI.inactiveElementColor;
        } else if (this.getContainer().isSlotLocked(this.getContainerSlot())) {
            return Settings.UI.inactiveElementColor;
        } else {
            return !this.isHovering() && !this.isSelected ? Settings.UI.activeElementColor : Settings.UI.highlightElementColor;
        }
    }

    public RuneContainerSlot setDecal(GameSprite sprite) {
        this.decal = sprite;
        return this;
    }

    public RuneContainerSlot setDecal(GameTexture texture) {
        this.setDecal(new GameSprite(texture));
        return this;
    }

    public Container getContainer() {
        return this.container != null ? this.container : this.client.getContainer();
    }

    public ContainerSlot getContainerSlot() {
        return this.getContainer().getSlot(this.containerSlotIndex);
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FormPosition getPosition() {
        return this.position;
    }

    public void setPosition(FormPosition position) {
        this.position = position;
    }
}
