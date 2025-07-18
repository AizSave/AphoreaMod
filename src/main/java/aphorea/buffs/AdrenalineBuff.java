package aphorea.buffs;

import aphorea.registry.AphBuffs;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class AdrenalineBuff extends AphShownBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.ALL_DAMAGE, 0.05F);
        buff.addModifier(BuffModifiers.ARMOR, -0.05F);
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 5;
    }

    public static void giveAdrenaline(Mob mob, int levels, int duration, boolean sendPacket) {
        for (int i = 0; i < levels; i++) {
            giveAdrenaline(mob, duration, sendPacket);
        }
    }

    public static void giveAdrenaline(Mob mob, int duration, boolean sendPacket) {
        mob.buffManager.addBuff(new ActiveBuff(AphBuffs.ADRENALINE, mob, duration, null), sendPacket);
    }

    public static int getAdrenalineLevel(Mob mob) {
        if (!mob.buffManager.hasBuff(AphBuffs.ADRENALINE)) {
            return 0;
        }
        return mob.buffManager.getBuff(AphBuffs.ADRENALINE).getStacks();
    }
}
