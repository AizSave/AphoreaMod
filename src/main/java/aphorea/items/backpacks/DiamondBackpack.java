package aphorea.items.backpacks;

public class DiamondBackpack extends AphBackpack {
    public DiamondBackpack() {
        this.rarity = Rarity.EPIC;
    }

    public int getInternalInventorySize() {
        return 16;
    }
}
