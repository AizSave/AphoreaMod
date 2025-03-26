package aphorea.buffs;

import necesse.entity.mobs.buffs.staticBuffs.ShownCooldownBuff;
import necesse.gfx.gameTexture.GameTexture;

import java.io.FileNotFoundException;

public class AphShownCooldownBuff extends ShownCooldownBuff {
    @Override
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/" + this.getStringID());
        } catch (FileNotFoundException var2) {
            this.iconTexture = GameTexture.fromFile("buffs/showncooldown");
        }

    }
}
