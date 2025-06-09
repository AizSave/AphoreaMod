package aphorea.biomes.levels;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.biomes.trial.TrialRoomLevel;

import java.util.stream.Stream;

public class InfectedTrialRoomLevel extends TrialRoomLevel {
    public InfectedTrialRoomLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public InfectedTrialRoomLevel(LevelIdentifier levelIdentifier, WorldEntity worldEntity) {
        super(levelIdentifier, worldEntity);
    }

    int presentPlayersAnt = 0;

    @Override
    public void serverTick() {
        super.serverTick();
        if (presentPlayersAnt != presentPlayers) {
            if (presentPlayersAnt == 0) {
                int summoned = 0;
                while (summoned < 3) {
                    int tileX = GameRandom.globalRandom.getIntBetween(0, 49);
                    int tileY = GameRandom.globalRandom.getIntBetween(0, 49);
                    if ((tileX < 21 || tileY > 18)
                            && this.getObject(tileX + 1, tileY).getID() == 0
                            && this.getObject(tileX - 1, tileY).getID() == 0
                            && this.getObject(tileX, tileY + 1).getID() == 0
                            && this.getObject(tileX, tileY - 1).getID() == 0
                            && this.getObject(tileX, tileY).getID() == 0
                            && entityManager.mobs.getInRegionByTileRange(tileX, tileY, 5).isEmpty()) {
                        entityManager.addMob(MobRegistry.getMob("infectedtreant", this), tileX * 32 + 16, tileY * 32 + 16);
                        summoned++;
                    }
                }
            } else if (presentPlayers == 0) {
                for (Mob mob : entityManager.mobs) {
                    if (mob.isHostile) mob.remove();
                }
            }
            presentPlayersAnt = presentPlayers;
        }
    }

    @Override
    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        return Stream.concat(
                super.getMobModifiers(mob),
                Stream.of(new ModifierValue<>(BuffModifiers.BLINDNESS, 0.6F))
        );
    }
}
