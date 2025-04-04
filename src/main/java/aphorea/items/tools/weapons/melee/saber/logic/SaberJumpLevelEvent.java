package aphorea.items.tools.weapons.melee.saber.logic;

import aphorea.methodpatches.PlayerFlyingHeight;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphFlatArea;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class SaberJumpLevelEvent extends MobAbilityLevelEvent {
    protected float initialX;
    protected float initialY;

    protected float dirX;
    protected float dirY;
    protected float distance;
    protected long startTime;
    protected int lastProcessTime;
    protected int animTime;
    protected GameDamage damage;
    protected MobHitCooldowns hitCooldowns;
    protected HudDrawElement hudDrawElement;

    public SaberJumpLevelEvent() {
    }

    public SaberJumpLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(owner, new GameRandom(seed));
        this.initialX = owner.x;
        this.initialY = owner.y;
        this.dirX = dirX;
        this.dirY = dirY;
        this.distance = distance;
        this.startTime = owner.getTime();
        this.animTime = animTime;
        this.damage = damage;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.initialX);
        writer.putNextFloat(this.initialY);
        writer.putNextFloat(this.dirX);
        writer.putNextFloat(this.dirY);
        writer.putNextFloat(this.distance);
        writer.putNextLong(this.startTime);
        writer.putNextInt(this.lastProcessTime);
        writer.putNextInt(this.animTime);
        if (this.damage != null) {
            writer.putNextBoolean(true);
            this.damage.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }

    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.initialX = reader.getNextFloat();
        this.initialY = reader.getNextFloat();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.distance = reader.getNextFloat();
        this.startTime = reader.getNextLong();
        this.lastProcessTime = reader.getNextInt();
        this.animTime = reader.getNextInt();
        if (reader.getNextBoolean()) {
            this.damage = GameDamage.fromReader(reader);
        }

    }

    @Override
    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns();
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.owner != null && !this.owner.removed()) {
            int timeProgress = (int) Math.min(this.getTime() - this.startTime, this.animTime);
            if (this.lastProcessTime < timeProgress) {
                double lastPercentToMove = this.getMoveCurve((double) this.lastProcessTime / (double) this.animTime);
                double nextPercentToMove = this.getMoveCurve((double) timeProgress / (double) this.animTime);
                double percentToMove = nextPercentToMove - lastPercentToMove;
                float fullDistanceToMove = (float) ((double) this.distance * percentToMove);
                this.setOwnerPos(this.owner.x + this.dirX * fullDistanceToMove, this.owner.y + this.dirY * fullDistanceToMove);

                this.lastProcessTime = timeProgress;

                PlayerFlyingHeight.playersFlyingHeight.put(owner.getUniqueID(), (int) getMoveCurve(Math.sin(((float) timeProgress / this.animTime) * Math.PI) * 10_000));
            }

            if (timeProgress >= this.animTime) {
                this.over();
            }

        } else {
            this.over();
        }
    }

    public void setOwnerPos(float x, float y) {
        this.owner.setPos(x, y, this.owner.isSmoothSnapped() || GameMath.squareDistance(this.owner.x, this.owner.y, x, y) < 4.0F);
    }

    protected double getMoveCurve(double x) {
        return Math.pow(x, 0.5);
    }

    boolean alreadyArea = false;

    private static final int[][] NEIGHBOR_OFFSETS = {
            {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private boolean checkNeighborTiles(Level level, int tileX, int tileY) {
        for (int[] offset : NEIGHBOR_OFFSETS) {
            if (!level.isSolidTile(tileX + offset[0], tileY + offset[1])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void over() {
        super.over();

        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }

        if (!alreadyArea) {
            alreadyArea = true;
            AphArea area = new AphFlatArea(100, AphColors.black).setDamageArea(damage.damage);
            new AphAreaList(area)
                    .setDamageType(damage.type)
                    .execute(this.owner);
        }

        if (!checkNeighborTiles(level, (int) (initialX / 32), (int) (initialY / 32)) && checkNeighborTiles(owner.getLevel(), owner.getX() / 32, owner.getY() / 32)) {
            setOwnerPos(initialX, initialY);
        }

        PlayerFlyingHeight.playersFlyingHeight.put(owner.getUniqueID(), 0);
    }
}