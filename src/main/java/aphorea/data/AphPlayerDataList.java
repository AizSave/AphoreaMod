package aphorea.data;

import necesse.entity.mobs.PlayerMob;

import java.util.ArrayList;

public class AphPlayerDataList {
    public static ArrayList<AphPlayerData> players = new ArrayList<>();

    public static AphPlayerData getCurrentPlayer(String playerName) {
        AphPlayerData playerData = players.stream().filter(p -> p.playerName.equals(playerName)).findFirst().orElse(null);
        if(playerData == null) {
            playerData = initPlayer(playerName);
        }
        return playerData;
    }

    public static AphPlayerData getCurrentPlayer(PlayerMob player) {
        return getCurrentPlayer(player.playerName);
    }

    public static AphPlayerData initPlayer(String playerName) {
        AphPlayerData playerData = new AphPlayerData(playerName);
        players.add(playerData);
        return playerData;
    }

    public static AphPlayerData initPlayer(PlayerMob player) {
        return getCurrentPlayer(player.playerName);
    }

}