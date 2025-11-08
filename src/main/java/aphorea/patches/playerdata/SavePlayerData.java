package aphorea.patches.playerdata;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = PlayerMob.class, name = "addSaveData", arguments = {SaveData.class})
public class SavePlayerData {

    @Advice.OnMethodExit
    static void onExit(@Advice.This PlayerMob playerMob, @Advice.Argument(0) SaveData saveData) {
        AphPlayerData player = AphPlayerDataList.getCurrentPlayer(playerMob);
        player.saveData(saveData);
    }

}