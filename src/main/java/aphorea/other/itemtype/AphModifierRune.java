package aphorea.other.itemtype;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;

public class AphModifierRune extends Item {
    private final String buffID;
    public GameTexture validTexture;

    public AphModifierRune(String buffID) {
        super(1);
        this.buffID = buffID;
    }

    public AphModifierRune() {
        this(null);
    }

    public TrinketBuff getBuff() {
        return (TrinketBuff) BuffRegistry.getBuff(buffID == null ? getStringID() : buffID);
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/modifier");
        this.validTexture = GameTexture.fromFile("items/modifier_valid");
    }
}
