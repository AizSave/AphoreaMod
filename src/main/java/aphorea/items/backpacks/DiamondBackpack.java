package aphorea.items.backpacks;

public class DiamondBackpack extends AphBackpack {
    public DiamondBackpack() {
        this.rarity = Rarity.RARE;
    }

    public int getInternalInventorySize() {
        return 14;
    }
}
