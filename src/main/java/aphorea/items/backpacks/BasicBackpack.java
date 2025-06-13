package aphorea.items.backpacks;

public class BasicBackpack extends AphBackpack {
    public BasicBackpack() {
        this.rarity = Rarity.NORMAL;
    }

    public int getInternalInventorySize() {
        return 6;
    }
}
