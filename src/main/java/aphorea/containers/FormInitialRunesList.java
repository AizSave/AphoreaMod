package aphorea.containers;

import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

abstract public class FormInitialRunesList extends FormGeneralGridList<FormInitialRunesList.RuneGrid> {

    public FormInitialRunesList(int x, int y, int width, int height, Client client) {
        super(x, y, width, height, 40, 40);
    }

    public void setRunes(Collection<InventoryItem> runes) {
        this.elements = new ArrayList<>();
        if (runes != null) {
            this.elements.addAll(runes.stream().map(RuneGrid::new).collect(Collectors.toList()));
        }

        this.limitMaxScroll();
    }

    public abstract void onRuneClicked(InventoryItem rune, InputEvent event);

    public GameMessage getEmptyMessage() {
        return new LocalMessage("ui", "noworlds");
    }

    public class RuneGrid extends FormListGridElement<FormInitialRunesList> {
        public final InventoryItem rune;

        public RuneGrid(InventoryItem rune) {
            this.rune = rune;
        }

        protected void draw(FormInitialRunesList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color color = Settings.UI.activeElementColor;
            if (this.isMouseOver(parent)) {
                color = Settings.UI.highlightElementColor;

                ListGameTooltips tooltips = rune.item.getTooltips(rune, perspective, null);
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }

            rune.drawIcon(perspective, 4, 4, 32, color);
        }

        @Override
        protected void onClick(FormInitialRunesList formInitialRunesList, int i, InputEvent inputEvent, PlayerMob playerMob) {
            FormInitialRunesList.this.onRuneClicked(rune, inputEvent);
        }

        @Override
        protected void onControllerEvent(FormInitialRunesList formInitialRunesList, int i, ControllerEvent controllerEvent, TickManager tickManager, PlayerMob playerMob) {
            if (controllerEvent.getState() == ControllerInput.MENU_SELECT) {
                FormInitialRunesList.this.onRuneClicked(rune, InputEvent.ControllerButtonEvent(controllerEvent, tickManager));
                controllerEvent.use();
            }
        }
    }
}
