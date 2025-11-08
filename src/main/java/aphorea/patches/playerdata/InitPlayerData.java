package aphorea.patches.playerdata;

import aphorea.data.AphPlayerDataList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = ClientClient.class, name = "applySpawned", arguments = {int.class})
public class InitPlayerData {

    @Advice.OnMethodExit
    public static void onExit(@Advice.FieldValue(value = "client") Client client) {
        if (client.getPlayer() != null) {
            PlayerMob player = client.getPlayer();

            if (player != null) {
                AphPlayerDataList.initPlayer(player);
            }
        }
    }

}