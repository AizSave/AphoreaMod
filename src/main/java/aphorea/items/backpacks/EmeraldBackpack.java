package aphorea.items.backpacks;

public class EmeraldBackpack extends AphBackpack {
    public EmeraldBackpack() {
        this.rarity = Rarity.UNCOMMON;
    }

    public int getInternalInventorySize() {
        return 12;
    }
}
