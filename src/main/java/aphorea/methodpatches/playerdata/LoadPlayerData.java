package aphorea.methodpatches.playerdata;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = PlayerMob.class, name = "applyLoadData", arguments = {LoadData.class})
public class LoadPlayerData {

    @Advice.OnMethodExit
    static void onExit(@Advice.This PlayerMob playerMob, @Advice.Argument(0) LoadData loadData) {
        AphPlayerData player = AphPlayerDataList.getCurrentPlayer(playerMob);
        player.loadData(loadData);
    }

}