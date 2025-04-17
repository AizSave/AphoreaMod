package aphorea.buffs.Banners;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.buffs.ActiveBuff;
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

    public static AphMightyBasicBannerBuff floatModifier(BiFunction<Float, Float, Float> getValue, float baseValue, Modifier<Float> floatModifier, float value) {
        return new AphMightyBasicBannerBuff(getValue, baseValue, AphBasicBannerBuffModifier.floatModifier(floatModifier, value));
    }

    public static AphMightyBasicBannerBuff intModifier(BiFunction<Float, Float, Float> getValue, float baseValue, Modifier<Integer> intModifier, int value) {
        return new AphMightyBasicBannerBuff(getValue, baseValue, AphBasicBannerBuffModifier.intModifier(intModifier, value));
    }

    public static AphMightyBasicBannerBuff floatModifier(Modifier<Float> floatModifier, float value) {
        return new AphMightyBasicBannerBuff(AphBasicBannerBuffModifier.floatModifier(floatModifier, value));
    }

    public static AphMightyBasicBannerBuff intModifier(Modifier<Integer> intModifier, int value) {
        return new AphMightyBasicBannerBuff(AphBasicBannerBuffModifier.intModifier(intModifier, value));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.buff.getStringID().endsWith("_normal")) {
            giveEffects(buff);
        }
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.buff.getStringID().endsWith("_normal")) {
            giveEffects(buff);
        }
    }

    public void giveEffects(ActiveBuff buff) {
        if (buff.buff.getStringID().endsWith("_normal") && buff.owner.buffManager.hasBuff(buff.buff.getStringID().replace("_normal", "_greater"))) {
            for (AphBasicBannerBuffModifier modifier : modifiers) {
                if (modifier.floatModifier != null) {
                    buff.setModifier(modifier.floatModifier, baseValue);
                }
                if (modifier.intModifier != null) {
                    buff.setModifier(modifier.intModifier, (int) (baseValue));
                }
            }

            onInactive();
            return;
        }

        super.giveEffects(buff);
        onActive();
    }

    public void onActive() {
        this.iconTexture = this.iconTextureActive;
        this.displayName = new LocalMessage("item", this.getStringID());
    }

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
