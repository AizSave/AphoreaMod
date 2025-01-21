package aphorea.items.backpacks;

public class BasicBackpack extends AphBackpack {
    public BasicBackpack() {
        this.rarity = Rarity.COMMON;
    }

    public int getInternalInventorySize() {
        return 4;
    }
}
