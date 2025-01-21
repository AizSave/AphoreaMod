package aphorea.items.backpacks;

public class SapphireBackpack extends AphBackpack {
    public SapphireBackpack() {
        this.rarity = Rarity.COMMON;
    }

    public int getInternalInventorySize() {
        return 6;
    }
}
