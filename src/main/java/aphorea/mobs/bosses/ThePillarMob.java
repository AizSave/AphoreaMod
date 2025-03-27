package aphorea.mobs.bosses;

import aphorea.objects.ThePillarEntranceObject;
import aphorea.objects.ThePillarObject;
import aphorea.packets.AphRemoveObjectEntity;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ThePillarMob extends BossMob {
    public static int SEARCH_PLAYERS_DISTANCE = 1024;
    private static final AphAreaList searchArea = new AphAreaList(
            new AphArea(SEARCH_PLAYERS_DISTANCE, AphColors.spinel)
    );
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(5000, 7500, 10000, 12500, 15000);
    private int aliveTimer;
    public static GameTexture icon;

    protected MobHealthScaling scaling = new MobHealthScaling(this);


    public ThePillarMob() {
        super(10000);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setArmor(10);
        this.setSpeed(0.0F);
        this.setFriction(1000.0F);
        this.setKnockbackModifier(0.0F);
        this.collision = new Rectangle(-16 * 3, -16 * 2, 32 * 3, 32 * 2);
        this.hitBox = new Rectangle(-16 * 3, -16 * 2, 32 * 3, 32 * 2);
        this.selectBox = new Rectangle(-14 * 3 - 10, -41 * 3 - 4, 28 * 3 + 20, 48 * 3 + 20);
        this.shouldSave = false;
        this.aliveTimer = 20;
        this.isStatic = true;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        SoundManager.setMusic(MusicRegistry.TheFirstTrial, SoundManager.MusicPriority.EVENT, 1.5F);
        EventStatusBarManager.registerMobHealthStatusBar(this);
        BossNearbyBuff.applyAround(this);
        searchArea.executeClient(getLevel(), this.x, this.y, 1, 1, 0, 100);
        this.tickAlive();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        this.tickAlive();
    }

    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    private void tickAlive() {
        --this.aliveTimer;
        if (this.aliveTimer <= 0) {
            this.remove();
        }
    }

    public void keepAlive(ThePillarObject.ThePillarObjectEntity entity) {
        this.aliveTimer = 20;
        this.setPos(entity.getMobX(), entity.getMobY(), true);
    }

    protected void playDeathSound() {
    }

    public boolean canBePushed(Mob other) {
        return false;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public boolean canTakeDamage() {
        return true;
    }

    public boolean countDamageDealt() {
        return true;
    }

    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int) ((float) (this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        this.scaling.setupHealthPacket(writer, isFull);
        super.setupHealthPacket(writer, isFull);
    }

    @Override
    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.scaling.applyHealthPacket(reader, isFull);
        super.applyHealthPacket(reader, isFull);
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        if (this.scaling != null) {
            this.scaling.updatedMaxHealth();
        }

    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 16;
        icon.initDraw().sprite(0, 0, 32).size(32, 32).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-16, -16, 32, 32);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        int x = this.getTileX() - 1;
        int y = this.getTileY() - 1;

        GameObject object = this.getLevel().getObject(x, y);
        if(object != null) {
            ObjectEntity objectEntity = object.getCurrentObjectEntity(getLevel(), x, y);
            if(objectEntity instanceof ThePillarObject.ThePillarObjectEntity) {
                objectEntity.remove();
                getServer().network.sendToClientsAtEntireLevel(new AphRemoveObjectEntity(x, y), getLevel());

                boolean openingStaircase = false;
                if (!(this.getLevel() instanceof IncursionLevel)) {
                    Point entrancePosition = new Point(x + 1, y + 2);
                    if (!this.getLevel().getLevelObject(entrancePosition.x, entrancePosition.y).getMultiTile().getMasterObject().getStringID().equals("thepillarentrance")) {
                        this.getLevel().entityManager.addLevelEvent(new ThePillarEntranceObject.ThePillarEntranceEvent(x + 1, y + 2));
                        openingStaircase = true;
                    }
                }

                boolean finalOpeningStaircase = openingStaircase;
                attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach((c) -> {
                    if (finalOpeningStaircase) {
                        c.sendChatMessage(new LocalMessage("misc", "staircaseopening"));
                    }
                });

            }
        }

    }
}
