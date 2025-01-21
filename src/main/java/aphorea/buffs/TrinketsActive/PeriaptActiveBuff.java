package aphorea.buffs.TrinketsActive;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PeriaptActiveBuff extends Buff {
    public PeriaptActiveBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.isVisible = true;
        this.shouldSave = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob) buff.owner;
            player.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff("periaptcooldown"), player, 20.0F, null), false);
        }
    }
}
