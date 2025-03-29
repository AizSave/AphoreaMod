package aphorea.buffs.Banners;

import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.gameTexture.GameTexture;

import java.io.FileNotFoundException;
import java.util.function.BiFunction;

public class AphMightyBasicBannerBuff extends AphBasicBannerBuff {
    private GameTexture iconTextureInactive;
    private GameTexture iconTextureActive;

    public AphMightyBasicBannerBuff(BiFunction<Float, Float, Float> getValue, float baseValue, AphBasicBannerBuffModifier... modifiers) {
        super(getValue, baseValue, modifiers);
    }

    public AphMightyBasicBannerBuff(AphBasicBannerBuffModifier... modifiers) {
        super(modifiers);
    }

    @Override
    public void onActive() {
        this.iconTexture = this.iconTextureActive;
        this.displayName = new LocalMessage("item", this.getStringID());
    }

    @Override
    public void onInactive() {
        this.iconTexture = this.iconTextureInactive;
        this.displayName = new LocalMessage("item", this.getStringID() + "_inactive");
    }

    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/" + this.getStringID());
            this.iconTextureActive = GameTexture.fromFileRaw("buffs/" + this.getStringID());
            this.iconTextureInactive = GameTexture.fromFileRaw("buffs/" + this.getStringID() + "_inactive");
        } catch (FileNotFoundException var2) {
            this.iconTexture = GameTexture.fromFile("buffs/unknown");
            this.iconTextureActive = GameTexture.fromFile("buffs/unknown");
            this.iconTextureInactive = GameTexture.fromFile("buffs/unknown");
        }
    }

    public void updateLocalDisplayName() {
        this.displayName = new LocalMessage("item", this.getStringID());
    }
}
