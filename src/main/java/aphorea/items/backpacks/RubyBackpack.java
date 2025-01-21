package aphorea.items.backpacks;

public class RubyBackpack extends AphBackpack {
    public RubyBackpack() {
        this.rarity = Rarity.UNCOMMON;
    }

    public int getInternalInventorySize() {
        return 10;
    }
}
