package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.CameraShake;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.temple.TempleLevel;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionPosition;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ThePillarEntranceObject extends StaticMultiObject {
    protected ThePillarEntranceObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "thepillarentrance");
        this.mapColor = AphColors.spinel_light;
        this.displayMapTooltip = true;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera
            camera, PlayerMob perspective) {
        ThePillarEntranceObjectEntity oe = this.getMultiTile(level, 0, tileX, tileY).getMasterLevelObject(level, 0, tileX, tileY).map((o) -> o.getCurrentObjectEntity(ThePillarEntranceObjectEntity.class)).orElse(null);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        float animationProgress = oe == null ? 1.0F : oe.getRevealAnimationProgress();
        int tileProgress = (int) ((float) this.multiWidth * (1.0F - animationProgress) * 32.0F);
        int offset = Math.max(tileProgress - this.multiX * 32, 0);
        int startX = this.multiX * 32 + offset;
        int endX = startX + 32 - offset;
        drawX += offset;
        if (endX > startX) {
            int yOffset = texture.getHeight() - this.multiHeight * 32;
            TextureDrawOptionsEnd options;
            if (this.multiY == 0) {
                options = texture.initDraw().section(startX, endX, 0, 32 + yOffset).light(light).pos(drawX, drawY - yOffset);
            } else {
                int startY = this.multiY * 32 + yOffset;
                options = texture.initDraw().section(startX, endX, startY, startY + 32).light(light).pos(drawX, drawY);
            }

            tileList.add((tm) -> options.draw());
        }
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer() && player.isServerClient()) {
            player.getServerClient().sendChatMessage("Coming soon: Stay tuned for the next big AphoreaMod update!");
        }

        super.interact(level, x, y, player);
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return this.isMultiTileMaster() ? new ThePillarEntranceObjectEntity(level, x, y) : super.getNewObjectEntity(level, x, y);
    }

    public static int[] registerObject() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(96, 64);
        ids[0] = ObjectRegistry.registerObject("thepillarentrance", new ThePillarEntranceObject(0, 0, 3, 2, ids, collision), 0.0F, false);
        ids[1] = ObjectRegistry.registerObject("thepillarentrance2", new ThePillarEntranceObject(1, 0, 3, 2, ids, collision), 0.0F, false);
        ids[2] = ObjectRegistry.registerObject("thepillarentrance3", new ThePillarEntranceObject(2, 0, 3, 2, ids, collision), 0.0F, false);
        ids[3] = ObjectRegistry.registerObject("thepillarentrance4", new ThePillarEntranceObject(0, 1, 3, 2, ids, collision), 0.0F, false);
        ids[4] = ObjectRegistry.registerObject("thepillarentrance5", new ThePillarEntranceObject(1, 1, 3, 2, ids, collision), 0.0F, false);
        ids[5] = ObjectRegistry.registerObject("thepillarentrance6", new ThePillarEntranceObject(2, 1, 3, 2, ids, collision), 0.0F, false);
        return ids;
    }

    @Override
    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return Collections.emptyList();
    }

    public static class ThePillarEntranceObjectEntity extends PortalObjectEntity {
        private long revealAnimationStartTime;
        private int revealAnimationRunTime;

        public ThePillarEntranceObjectEntity(Level level, int x, int y) {
            super(level, "thepillarentrance", x, y, level.getIdentifier(), 50, 50);
        }

        public void init() {
            super.init();
            if (this.getLevel() != null) {
                LevelIdentifier identifier = this.getLevel().getIdentifier();
                if (identifier.isIslandPosition()) {
                    this.destinationIdentifier = new LevelIdentifier(identifier.getIslandX(), identifier.getIslandY(), -200);
                    Point destinationTile = TempleLevel.getEntranceSpawnPos(identifier.getIslandX(), identifier.getIslandY());

                    this.destinationTileX = destinationTile.x;
                    this.destinationTileY = destinationTile.y;
                } else {
                    this.destinationIdentifier = identifier;
                    this.destinationTileX = this.getTileX();
                    this.destinationTileY = this.getTileY();
                }
            }

        }

        public void use(Server server, ServerClient client) {
            this.teleportClientToAroundDestination(client, (level) -> {
                GameObject exit = ObjectRegistry.getObject(ObjectRegistry.getObjectID("thepillarexit"));
                if (exit != null) {
                    exit.placeObject(level, this.destinationTileX - 1, this.destinationTileY, 0, false);
                    PortalObjectEntity exitEntity = (PortalObjectEntity) level.entityManager.getObjectEntity(this.destinationTileX - 1, this.destinationTileY);
                    if (exitEntity != null) {
                        exitEntity.destinationTileX = this.getX();
                        exitEntity.destinationTileY = this.getY();
                        exitEntity.destinationIdentifier = this.getLevel().getIdentifier();
                    }
                }

                return true;
            }, true);
            this.runClearMobs(getLevel(), getX(), getY());
        }

        public void startRevealAnimation(int runTimeMilliseconds) {
            this.revealAnimationStartTime = this.getLocalTime();
            this.revealAnimationRunTime = runTimeMilliseconds;
        }

        public float getRevealAnimationProgress() {
            if (this.revealAnimationStartTime > 0L) {
                long timeSinceStart = this.getLocalTime() - this.revealAnimationStartTime;
                float out = (float) timeSinceStart / (float) this.revealAnimationRunTime;
                if (out >= 1.0F) {
                    this.revealAnimationStartTime = 0L;
                    return 1.0F;
                } else {
                    return out;
                }
            } else {
                return 1.0F;
            }
        }
    }

    public static class ThePillarEntranceEvent extends LevelEvent {
        public static int ANIMATION_TIME = 10000;
        public long startTime;
        public int tileX;
        public int tileY;
        protected SoundPlayer secondStageRumble;

        public ThePillarEntranceEvent() {
        }

        public ThePillarEntranceEvent(int tileX, int tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
        }

        public void setupSpawnPacket(PacketWriter writer) {
            super.setupSpawnPacket(writer);
            writer.putNextInt(this.tileX);
            writer.putNextInt(this.tileY);
        }

        public void applySpawnPacket(PacketReader reader) {
            super.applySpawnPacket(reader);
            this.tileX = reader.getNextInt();
            this.tileY = reader.getNextInt();
        }

        public void init() {
            super.init();
            if (this.isServer()) {
                for (int x = this.tileX - 1; x <= this.tileX + 1; ++x) {
                    for (int y = this.tileY; y <= this.tileY + 1; ++y) {
                        for (int layer = 0; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
                            this.level.entityManager.doObjectDamage(layer, x, y, 1000000, 1000000.0F, null, null);
                        }
                    }
                }
            }

            ObjectRegistry.getObject("thepillarentrance").placeObject(this.level, this.tileX - 1, this.tileY, 0, false);
            ObjectEntity entity = this.level.entityManager.getObjectEntity(this.tileX - 1, this.tileY);
            if (entity instanceof ThePillarEntranceObjectEntity) {
                ((ThePillarEntranceObjectEntity) entity).startRevealAnimation(ANIMATION_TIME);
            }

            this.startTime = this.level.getWorldEntity().getTime();
            if (this.isClient()) {
                CameraShake cameraShake = this.level.getClient().startCameraShake((float) (this.tileX * 32 + 16), (float) (this.tileY * 32 + 16), ANIMATION_TIME, 40, 5.0F, 5.0F, true);
                cameraShake.minDistance = 200;
                cameraShake.listenDistance = 2000;
            } else {
                this.over();
            }

        }

        public void clientTick() {
            super.clientTick();
            long timeProgress = this.level.getWorldEntity().getTime() - this.startTime;
            if (timeProgress > (long) ANIMATION_TIME) {
                this.over();
            } else {
                if (this.secondStageRumble == null || this.secondStageRumble.isDone()) {
                    this.secondStageRumble = SoundManager.playSound(GameResources.rumble, SoundEffect.effect((float) (this.tileX * 32 + 16), (float) (this.tileY * 32 + 16)).volume(4.0F).falloffDistance(2000));
                }

                if (this.secondStageRumble != null) {
                    this.secondStageRumble.refreshLooping(1.0F);
                }

                float floatProgress = Math.abs(GameMath.limit((float) timeProgress / (float) ANIMATION_TIME, 0.0F, 1.0F) - 1.0F);
                int pixels = (int) (floatProgress * 32.0F * 3.0F);

                for (int i = 0; i < 4; ++i) {
                    this.level.entityManager.addParticle((float) (this.tileX * 32 - 32 + pixels) + GameRandom.globalRandom.floatGaussian() * 5.0F, (float) (this.tileY * 32) + GameRandom.globalRandom.nextFloat() * 32.0F * 2.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 3.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).color(AphColors.spinel_darker).heightMoves(0.0F, GameRandom.globalRandom.getFloatBetween(20.0F, 30.0F)).lifeTime(1000);
                }

            }
        }

        public Set<RegionPosition> getRegionPositions() {
            return Collections.singleton(this.level.regionManager.getRegionPosByTile(this.tileX, this.tileY));
        }
    }


}
