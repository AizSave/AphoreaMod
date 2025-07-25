package aphorea.buffs.Banners;

import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

import java.util.function.BiFunction;

public class AphBasicBannerBuff extends AphBannerBuff {
    public BiFunction<Float, Float, Float> getValue;
    public float baseValue;
    public AphBasicBannerBuffModifier[] modifiers;

    public AphBasicBannerBuff(BiFunction<Float, Float, Float> getValue, float baseValue, AphBasicBannerBuffModifier... modifiers) {
        this.getValue = getValue;
        this.baseValue = baseValue;
        this.modifiers = modifiers;
    }

    public AphBasicBannerBuff(AphBasicBannerBuffModifier... modifiers) {
        this((value, effect) -> value * effect, 0, modifiers);
    }

    public static AphBasicBannerBuff floatModifier(BiFunction<Float, Float, Float> getValue, float baseValue, Modifier<Float> floatModifier, float value) {
        return new AphBasicBannerBuff(getValue, baseValue, AphBasicBannerBuffModifier.floatModifier(floatModifier, value));
    }

    public static AphBasicBannerBuff intModifier(BiFunction<Float, Float, Float> getValue, float baseValue, Modifier<Integer> intModifier, int value) {
        return new AphBasicBannerBuff(getValue, baseValue, AphBasicBannerBuffModifier.intModifier(intModifier, value));
    }

    public static AphBasicBannerBuff floatModifier(Modifier<Float> floatModifier, float value) {
        return new AphBasicBannerBuff(AphBasicBannerBuffModifier.floatModifier(floatModifier, value));
    }

    public static AphBasicBannerBuff intModifier(Modifier<Integer> intModifier, int value) {
        return new AphBasicBannerBuff(AphBasicBannerBuffModifier.intModifier(intModifier, value));
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        giveEffects(buff);
    }

    public void giveEffects(ActiveBuff ab) {
        for (AphBasicBannerBuffModifier modifier : modifiers) {
            float calculatedValue = getValue.apply(modifier.value, getInspirationEffect(ab));
            if (modifier.floatModifier != null) {
                ab.setModifier(modifier.floatModifier, calculatedValue);
            }
            if (modifier.intModifier != null) {
                ab.setModifier(modifier.intModifier, (int) (calculatedValue));
            }
        }
    }


    public static class AphBasicBannerBuffModifier {
        public Modifier<Float> floatModifier;
        public Modifier<Integer> intModifier;
        public float value;

        public AphBasicBannerBuffModifier(Modifier<Float> floatModifier, Modifier<Integer> intModifier, float value) {
            this.floatModifier = floatModifier;
            this.intModifier = intModifier;
            this.value = value;
        }

        public static AphBasicBannerBuffModifier floatModifier(Modifier<Float> floatModifier, float value) {
            return new AphBasicBannerBuffModifier(floatModifier, null, value);
        }

        public static AphBasicBannerBuffModifier intModifier(Modifier<Integer> intModifier, int value) {
            return new AphBasicBannerBuffModifier(null, intModifier, value);
        }
    }

}
