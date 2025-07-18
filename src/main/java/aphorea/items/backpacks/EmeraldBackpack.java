package aphorea.items.backpacks;

public class EmeraldBackpack extends AphBackpack {
    public EmeraldBackpack() {
        this.rarity = Rarity.RARE;
    }

    public int getInternalInventorySize() {
        return 14;
    }
}
