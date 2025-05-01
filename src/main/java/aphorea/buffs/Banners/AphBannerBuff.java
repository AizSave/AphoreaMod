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
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
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
        if (name.startsWith("aph_")) {
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

    public float getInspirationEffect(ActiveBuff ab) {
        return ab.owner == null ? 1 : ab.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT);
    }

    public static boolean shouldChange(ActiveBuff antAb, ActiveBuff newAb) {
        return (antAb.owner == null ? 1 : antAb.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)) < (newAb.owner == null ? 1 : newAb.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT));
    }
}
