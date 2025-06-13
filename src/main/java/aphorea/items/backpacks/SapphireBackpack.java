package aphorea.items.backpacks;

public class SapphireBackpack extends AphBackpack {
    public SapphireBackpack() {
        this.rarity = Rarity.NORMAL;
    }

    public int getInternalInventorySize() {
        return 8;
    }
}
