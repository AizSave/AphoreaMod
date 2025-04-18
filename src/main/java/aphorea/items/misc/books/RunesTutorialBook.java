package aphorea.items.misc.books;

import aphorea.items.misc.AphWrittenBook;

public class RunesTutorialBook extends AphWrittenBook {
    public RunesTutorialBook() {
        super(
                new BookPage(
                        new PageHeader1("[translate:runestutorialbook.openheader]"),
                        new PageText("[translate:runestutorialbook.opentext]"),
                        new PageImage("runestutorial_open"),
                        new PageHeader1("[translate:runestutorialbook.equipheader]"),
                        new PageText("[translate:runestutorialbook.equiptext]"),
                        new PageImage("runestutorial_equip")
                ),
                new BookPage(
                        new PageHeader1("[translate:runestutorialbook.runesheader]"),
                        new PageText("[translate:runestutorialbook.runestext]"),
                        new PageHeader2("[translate:runestutorialbook.baserunesheader]"),
                        new PageText("[translate:runestutorialbook.baserunestext]"),
                        new PageImage("runestutorial_baserunes"),
                        new PageHeader2("[translate:runestutorialbook.modifierrunesheader]"),
                        new PageText("[translate:runestutorialbook.modifierrunestext]"),
                        new PageImage("runestutorial_modifierrunes")
                ),
                new BookPage(
                        new PageHeader1("[translate:runestutorialbook.tableheader]"),
                        new PageText("[translate:runestutorialbook.tabletext]"),
                        new PageImage("runestutorial_table"),
                        new PageText("[translate:runestutorialbook.crafttext]"),
                        new PageImage("runestutorial_craft")
                )
        );
    }
}