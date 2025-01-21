package aphorea.mobs.bosses;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MiniUnstableGelSlime extends FlyingHostileMob {

    int escape;
    boolean initialTP = false;
    int countTP;

    public static GameDamage attack = new GameDamage(30);
    public static int attack_knockback = 50;

    public void setInitialTP(Boolean initialTP) {
        this.initialTP = initialTP;
    }

    public static GameTexture texture;

    public static LootTable lootTable = new LootTable(
            ChanceLootItem.between(0.3f, "unstablegel", 1, 1)
    );

    public MiniUnstableGelSlime() {
        super(60);
        setSpeed(40);
        setFriction(2);

        staySmoothSnapped = true;

        this.escape = 0;

        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -7 - 14, 28, 28);
    }

    @Override
    public void init() {
        super.init();
        ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 1024 * 32, attack, attack_knockback, 40000), new FlyingAIMover());

        this.addBuff(new ActiveBuff(AphBuffs.IMMORTAL, this, 1000, this), true);

        if (GameRandom.globalRandom.getChance(0.5F) && this.initialTP) {
            this.executeTeleport();
        } else if (this.isClient()) {
            this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.x, this.y, AphColors.unstableGel), Particle.GType.CRITICAL);
        }


        countTP = 0;
    }

    public GameAreaStream<Mob> streamPossibleTargets(Point base, TargetFinderDistance<MiniUnstableGelSlime> distance) {
        return distance.streamPlayersInRange(base, this).filter((m) -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map((m) -> m);
    }

    public Mob getRandomTarget(Point base, TargetFinderDistance<MiniUnstableGelSlime> distance) {
        ArrayList<Mob> list = new ArrayList<>();
        streamPossibleTargets(base, distance).forEach(list::add);
        return GameRandom.globalRandom.getOneOf(list);
    }

    public void executeTeleport() {
        if (!this.removed()) {
            Mob tpTarget = getRandomTarget(new Point((int) this.x, (int) this.y), new TargetFinderDistance<>(1024 * 32));

            if (tpTarget != null) {
                float distance = 200;
                float angle = GameRandom.globalRandom.getFloatBetween(0, 360);
                float xExtra = (float) (Math.cos(angle) * distance);
                float yExtra = (float) (Math.sin(angle) * distance);

                if (this.isClient()) {
                    this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.x, this.y, AphColors.unstableGel), Particle.GType.CRITICAL);
                }

                this.setPos(tpTarget.x + xExtra, tpTarget.y + yExtra, true);
            }
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; i++) {
            getLevel().entityManager.addParticle(new FleshParticle(
                    getLevel(), texture == null ? GameTexture.fromFile("mobs/miniunstablegelslime") : texture,
                    GameRandom.globalRandom.nextInt(5),
                    8,
                    32,
                    x, y, 20f,
                    knockbackX, knockbackY
            ), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;

        Point sprite = getAnimSprite(x, y, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 64)
                .light(light)
                .alpha(this.buffManager.hasBuff(AphBuffs.IMMORTAL) ? 0.6F : 1)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!this.isWaterWalking()) addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 500, this), true);

    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.getLevel().getWorldEntity().isNight()) {
            this.remove();
        } else {
            if (GameUtils.streamServerClients(this.getLevel()).anyMatch((c) -> !c.isDead() && !c.playerMob.removed() && c.playerMob.getDistance(this) < 1280.0F)) {
                this.escape = 0;
            } else if (this.escape >= 100) {
                this.remove();
            } else {
                this.escape++;
            }
        }
    }

    @Override
    public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
        if (buff.buff != AphBuffs.STICKY) super.addBuff(buff, sendUpdatePacket);
    }

}
