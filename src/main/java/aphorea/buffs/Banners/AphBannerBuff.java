package aphorea.buffs.Banners;

import aphorea.registry.AphModifiers;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.VicinityBuff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;

import java.io.FileNotFoundException;

public class AphBannerBuff extends VicinityBuff {
    public float bannerEffect = 1F;

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        if(buff.getAttacker() != null && buff.getAttacker().getAttackOwner() != null) {
            bannerEffect = buff.getAttacker().getAttackOwner().buffManager.getModifier(AphModifiers.BANNER_EFFECT);
        }
    }

    public void updateLocalDisplayName() {
        this.displayName = new LocalMessage("item", getRealName());
    }

    @Override
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/" + getRealName());
        } catch (FileNotFoundException var2) {
            this.iconTexture = GameTexture.fromFile("buffs/unknown");
        }
    }

    public String getRealName() {
        String name = this.getStringID();
        if(name.startsWith("aph_")) {
            return name.replace("aph_", "");
        }
        return name;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        ab.getModifierTooltips().forEach(modifierTooltip -> tooltips.add(modifierTooltip.toTooltip(true)));
        return tooltips;
    }

    public boolean shouldRemove(ActiveBuff ab) {
        if(ab.getAttacker() == null || ab.getAttacker().getAttackOwner() == null) {
            return false;
        }
        return ab.getAttacker().getAttackOwner().buffManager.getModifier(AphModifiers.BANNER_EFFECT) < bannerEffect;
    }
}
