package aphorea.buffs.Runes;

import aphorea.registry.AphBuffs;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

import java.util.concurrent.atomic.AtomicReference;

public class AphBaseRuneActiveBuff extends Buff {
    protected float baseEffectNumber;
    protected float extraEffectNumberMod;
    protected int cooldown;
    protected String cooldownBuff;
    protected ModifierValue<?>[] modifiers;

    public AphBaseRuneActiveBuff(float baseEffectNumber, float extraEffectNumberMod, int cooldown, String cooldownBuff, ModifierValue<?>... modifiers) {
        this.baseEffectNumber = baseEffectNumber;
        this.extraEffectNumberMod = extraEffectNumberMod;
        this.cooldown = cooldown;
        this.cooldownBuff = cooldownBuff;
        this.modifiers = modifiers;
        isVisible = false;
        canCancel = false;
    }

    public AphBaseRuneActiveBuff(int cooldown, float extraEffectNumberMod, String cooldownBuff, ModifierValue<?>... modifiers) {
        this(0, extraEffectNumberMod, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, float extraEffectNumberMod, ModifierValue<?>... modifiers) {
        this(cooldown, extraEffectNumberMod, null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, float extraEffectNumberMod, int cooldown, ModifierValue<?>... modifiers) {
        this(baseEffectNumber, extraEffectNumberMod, cooldown, null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, int cooldown, String cooldownBuff, ModifierValue<?>... modifiers) {
        this(baseEffectNumber, 1F, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, String cooldownBuff, ModifierValue<?>... modifiers) {
        this(0, 1F, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, ModifierValue<?>... modifiers) {
        this(cooldown, 1F, null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, int cooldown, ModifierValue<?>... modifiers) {
        this(baseEffectNumber, 1F, cooldown, null, modifiers);
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierValue<?> modifier : this.modifiers) {
            modifier.apply(buff);
        }
        if (buff.owner.isPlayer) {
            this.initExtraModifiers(buff, getEffectNumber((PlayerMob) buff.owner));
        }
    }

    public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
    }

    public float getEffectNumber(PlayerMob player) {
        return baseEffectNumber * getEffectNumberVariation(player);
    }

    public float getEffectNumberVariation(PlayerMob player) {
        AtomicReference<Float> variation = new AtomicReference<>(1F);
        AphBaseRuneTrinketBuff.getRuneModifiers(player).forEach(
                b -> variation.updateAndGet(v -> v + b.getEffectNumberVariation())
        );
        return Math.max(variation.get(), 0) * extraEffectNumberMod;

    }

    public int getCooldownDuration(PlayerMob player) {
        return (int) (getBaseCooldownDuration() * AphBaseRuneTrinketBuff.getCooldownVariation(player));
    }

    public int getBaseCooldownDuration() {
        return cooldown;
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        if (buff.owner.isPlayer) {
            int cooldownDuration = getCooldownDuration((PlayerMob) buff.owner);
            if (cooldownDuration > 0) {
                if (cooldownBuff != null) {
                    buff.owner.buffManager.addBuff(new ActiveBuff(cooldownBuff, buff.owner, cooldownDuration, null), false);
                }
                buff.owner.buffManager.addBuff(new ActiveBuff(AphBuffs.RUNE_INJECTOR_COOLDOWN, buff.owner, cooldownDuration, null), false);
            }
        }
    }
}
