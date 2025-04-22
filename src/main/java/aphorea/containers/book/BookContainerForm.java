package aphorea.containers.book;

import aphorea.items.misc.AphWrittenBook;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;

import java.awt.*;

public class BookContainerForm<T extends BookContainer> extends ContainerFormSwitcher<T> {
    public Client client;

    private final Form principalForm;

    public static int bookID = -1;
    public static int page = 1;

    public FormContentBox pageContent;

    public FormLabel pageNumber;

    public int pagesAmount;
    public AphWrittenBook.BookPage[] bookPages;

    public BookContainerForm(Client client, final T container) {
        super(client, container);

        this.client = client;

        this.bookPages = getBookInstructions();
        this.pagesAmount = bookPages.length;

        FormComponentList formComponents = this.addComponent(new FormComponentList());
        this.principalForm = formComponents.addComponent(new Form(925, 500));

        this.principalForm.addComponent(new FormLabel(getTitle(), new FontOptions(30).color(Settings.UI.activeTextColor), -1, 20, 10, this.principalForm.getWidth() - 20));

        FormBreakLine middleVerticalLine = this.principalForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 10, 50, this.principalForm.getWidth() - 20, true));
        middleVerticalLine.color = Settings.UI.activeTextColor;

        this.principalForm.addComponent(pageContent = new FormContentBox(20, 70, 925 - 40, this.principalForm.getHeight() - 170));

        FormBreakLine middleVerticalLine2 = this.principalForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 10, this.principalForm.getHeight() - 80, this.principalForm.getWidth() - 20, true));
        middleVerticalLine2.color = Settings.UI.activeTextColor;

        this.principalForm.addComponent(pageNumber = new FormLabel(String.valueOf(page), new FontOptions(20).color(Settings.UI.activeTextColor), 0, this.principalForm.getWidth() / 2, this.principalForm.getHeight() - 65, 20));

        this.principalForm.addComponent(new FormCustomButton(this.principalForm.getWidth() / 2 - 60, this.principalForm.getHeight() - 65, 40, 20) {
            @Override
            public void draw(Color color, int i, int i1, PlayerMob playerMob) {
                GameFontHandler font = FontManager.bit;

                float alpha = 0.6F;
                if (1 >= page) {
                    alpha = 0.2F;
                } else if (this.isHovering()) {
                    alpha = 1;
                }
                FontOptions fontOptions = new FontOptions(20).color(Settings.UI.activeTextColor).alphaf(alpha);
                String drawText = "<";
                font.drawString(this.getX() + (float) this.getBoundingBox().width / 2 - font.getWidth(drawText, fontOptions) / 2, this.getY(), drawText, fontOptions);

            }
        }).onClicked(event -> {
            if (1 < page) {
                page--;
                playTickSound();
                updateContent();
            }
        });

        this.principalForm.addComponent(new FormCustomButton(this.principalForm.getWidth() / 2 + 20, this.principalForm.getHeight() - 65, 40, 20) {
            @Override
            public void draw(Color color, int i, int i1, PlayerMob playerMob) {
                GameFontHandler font = FontManager.bit;

                float alpha = 0.6F;
                if (pagesAmount <= page) {
                    alpha = 0.2F;
                } else if (this.isHovering()) {
                    alpha = 1;
                }
                FontOptions fontOptions = new FontOptions(20).color(Settings.UI.activeTextColor).alphaf(alpha);
                String drawText = ">";
                font.drawString(this.getX() + (float) this.getBoundingBox().width / 2 - font.getWidth(drawText, fontOptions) / 2, this.getY(), drawText, fontOptions);

            }
        }).onClicked(event -> {
            if (pagesAmount > page) {
                page++;
                playTickSound();
                updateContent();
            }
        });

        this.principalForm.addComponent(new FormLocalTextButton("ui", "closebutton", 4, this.principalForm.getHeight() - 40, this.principalForm.getWidth() - 8)).onClicked((e) -> client.closeContainer(true));
        this.makeCurrent(formComponents);

        updateContent();
    }

    public void updateContent() {
        pageNumber.setText(String.valueOf(page));

        this.pageContent.clearComponents();

        AphWrittenBook.BookPage bookPage = bookPages[page - 1];

        int currentY = 5;

        for (int i = 0; i < bookPage.pageInstructions.length; i++) {
            AphWrittenBook.PageInstruction pageInstruction = bookPage.pageInstructions[i];
            if (i != 0) {
                currentY += pageInstruction.topPadding();
            }
            currentY += pageInstruction.execute(currentY, pageContent);
            if (i != (bookPage.pageInstructions.length - 1)) {
                currentY += pageInstruction.bottomPadding();
            }
        }

        pageContent.setContentBox(new Rectangle(0, 0, pageContent.getWidth(), currentY));

        pageContent.setScrollY(0);
    }

    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.principalForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public boolean shouldOpenInventory() {
        return false;
    }

    public boolean shouldShowToolbar() {
        return false;
    }

    public static String getTitle() {
        return bookID == -1 ? "Not found" : AphWrittenBook.getBook(bookID).getTitle();
    }

    public static AphWrittenBook.BookPage[] getBookInstructions() {
        return bookID == -1 ? new AphWrittenBook.BookPage[0] : AphWrittenBook.getBook(bookID).content;
    }
}
