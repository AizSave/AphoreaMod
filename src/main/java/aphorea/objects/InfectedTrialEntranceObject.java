package aphorea.objects;

import aphorea.levels.InfectedTrialRoomLevel;
import aphorea.presets.trial.InfectedTrialRoom;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrialEntranceObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.TrialEntranceObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.presets.set.TrialRoomSet;
import necesse.level.maps.presets.trialRoomPresets.TrialRoomPreset;

import java.util.List;

public class InfectedTrialEntranceObject extends TrialEntranceObject {

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new InfectedTrialEntranceObjectEntity(level, x, y);
    }

    public static class InfectedTrialEntranceObjectEntity extends TrialEntranceObjectEntity {

        public InfectedTrialEntranceObjectEntity(Level level, int x, int y) {
            super(level, x, y);
        }

        @Override
        public Level generateTrialLevel(Level parentLevel, int parentTileX, int parentTileY, LevelIdentifier trialRoomIdentifier, Server server) {
            TrialRoomLevel trialLevel = new InfectedTrialRoomLevel(trialRoomIdentifier, server.world.worldEntity);
            trialLevel.setFallbackLevel(parentLevel, parentTileX, parentTileY);
            GameRandom random = new GameRandom(trialRoomIdentifier.stringID.hashCode());
            TrialRoomPreset preset = new InfectedTrialRoom(random, TrialRoomSet.deepStone, this::getNextLootList);
            preset.applyToLevel(trialLevel, 0, 0);
            this.destinationTileX = preset.exitTileX;
            this.destinationTileY = preset.exitTileY;
            return trialLevel;
        }

        @Override
        public List<InventoryItem> getNextLootList() {
            System.out.println(this.lootList.isEmpty());
            return super.getNextLootList();
        }
    }
}
