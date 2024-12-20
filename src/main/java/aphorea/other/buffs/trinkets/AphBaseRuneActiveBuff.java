package aphorea.other.buffs.trinkets;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class AphBaseRuneActiveBuff extends Buff {
    private final float baseEffectNumber;

    final int cooldown;
    final String cooldownBuff;
    private final ModifierValue<?>[] modifiers;

    public AphBaseRuneActiveBuff(float baseEffectNumber, int cooldown, String cooldownBuff, ModifierValue<?>... modifiers) {
        this.baseEffectNumber = baseEffectNumber;

        this.cooldown = cooldown;
        this.cooldownBuff = cooldownBuff;
        this.modifiers = modifiers;
        isVisible = false;
        canCancel = false;
    }

    public AphBaseRuneActiveBuff(int cooldown, String cooldownBuff, ModifierValue<?>... modifiers) {
        this(0, cooldown, cooldownBuff, modifiers);
    }

    public AphBaseRuneActiveBuff(int cooldown, ModifierValue<?>... modifiers) {
        this(cooldown, null, modifiers);
    }

    public AphBaseRuneActiveBuff(float baseEffectNumber, int cooldown, ModifierValue<?>... modifiers) {
        this(baseEffectNumber, cooldown, null, modifiers);
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierValue<?> modifier : this.modifiers) {
            modifier.apply(buff);
        }
        if(buff.owner.isPlayer) {
            this.initExtraModifiers(buff, getEffectNumber((PlayerMob) buff.owner));
        }
    }

    public void initExtraModifiers(ActiveBuff buff, float numberEffect) {
    }

    public float getEffectNumber(PlayerMob player) {
        return baseEffectNumber * getEffectNumberVariation(player);
    }

    public float getEffectNumberVariation(PlayerMob player) {
        return AphBaseRuneTrinketBuff.getEffectNumberVariation(player);
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
        if(buff.owner.isPlayer) {
            if(getCooldownDuration((PlayerMob) buff.owner) > 0) {
                if(cooldownBuff != null) {
                    ActiveBuff giveBuff = new ActiveBuff(cooldownBuff, buff.owner, getCooldownDuration((PlayerMob) buff.owner), null);
                    buff.owner.buffManager.addBuff(giveBuff, false);
                }

                ActiveBuff giveBuff2 = new ActiveBuff("runesinjectorcooldown", buff.owner, getCooldownDuration((PlayerMob) buff.owner), null);
                buff.owner.buffManager.addBuff(giveBuff2, false);
            }
        }
    }
}
