package aphorea.levelevents.runes;

import aphorea.projectiles.rune.RuneOfCryoQueenProjectile;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class AphRuneOfCryoQueenEvent extends MobAbilityLevelEvent {
    public float effectNumber;
    private int x;
    private int y;
    private int timer;
    private int index;
    private float startAngle;
    private boolean clockwise;

    public AphRuneOfCryoQueenEvent() {
    }

    public AphRuneOfCryoQueenEvent(Mob owner, int x, int y, float startAngle, boolean clockwise, float effectNumber) {
        super(owner, new GameRandom());
        this.effectNumber = effectNumber;
        this.x = x;
        this.y = y;
        this.startAngle = startAngle;
        this.clockwise = clockwise;

        this.timer = 0;
        this.index = 0;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.effectNumber);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
        writer.putNextFloat(this.startAngle);
        writer.putNextBoolean(this.clockwise);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.effectNumber = reader.getNextFloat();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.startAngle = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();

        this.timer = 0;
        this.index = 0;
    }

    public void init() {
        super.init();
        this.hitsObjects = false;

        this.timer = 0;
        this.index = 0;

        if (isClient()) {
            float pitch = GameRandom.globalRandom.getOneOf(1.0F, 1.05F);
            SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.x, this.y).pitch(pitch));
        }
    }

    public void clientTick() {
        super.clientTick();
        tick();
    }

    public void serverTick() {
        super.serverTick();
        tick();
    }

    public void tick() {
        for (this.timer += 50; this.timer >= 30; ++this.index) {
            if (this.index >= 18) {
                this.over();
            }

            this.timer -= 30;
            float angle = this.startAngle + (float) (this.index * 20);
            float speed = getProjectileSpeed();

            owner.getLevel().entityManager.projectiles.add(new RuneOfCryoQueenProjectile(this.x, this.y, 20.0F, angle, this.clockwise, speed, (int) effectNumber, 100, owner));

        }

    }

    protected float getProjectileSpeed() {
        return 150.0F;
    }

}