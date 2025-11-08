package aphorea.mobs.hostile;

import aphorea.projectiles.mob.RockyGelSlimeLootProjectile;
import aphorea.projectiles.mob.RockyGelSlimeProjectile;
import aphorea.registry.AphBiomes;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class RockyGelSlime extends HostileMob {

    public static GameDamage collision_damage = new GameDamage(30);
    public static int collision_knockback = 50;

    public static GameDamage rock_damage = new GameDamage(15);
    public static GameDamage rock_damage_if = new GameDamage(30);
    public static int rock_knockback = 25;

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return super.isValidSpawnLocation(server, client, targetX, targetY);
    }

    public static GameTexture texture;

    public static LootTable lootTable = new LootTable(
            new LootItem("rockygel", 0),
            ChanceLootItem.between(0.05f, "unstablecore", 1, 1)
    );

    public RockyGelSlime() {
        super(220);
        this.setSpeed(25);
        this.setFriction(3);

        collision = new Rectangle(-15, -6, 30, 14);
        hitBox = new Rectangle(-26, -16, 52, 28);
        selectBox = new Rectangle(-26, -27, 52, 39);
    }

    @Override
    public void init() {
        super.init();
        ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 12 * 32, collision_damage, collision_knockback, 40000));

        if (this.getLevel().baseBiome == AphBiomes.INFECTED_FIELDS) {
            this.setSpeed(this.getSpeed() * 1.4F);
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
                    getLevel(), texture,
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
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!this.isWaterWalking()) addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        MobWasHitEvent eventResult = super.isHit(event, attacker);
        if (isServer()) {
            if (eventResult != null && attacker != null && attacker.getAttackOwner() != null && !eventResult.wasPrevented && eventResult.damage < this.getHealth()) {
                Mob attackOwner = attacker.getAttackOwner();
                throwRock(attackOwner.getX(), attackOwner.getY(), false);
            }
            throwRock(GameRandom.globalRandom.getFloatBetween(0, (float) (2 * Math.PI)), false);
        }

        return eventResult;
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (isServer()) {
            float initialAngle = GameRandom.globalRandom.getFloatBetween(0, (float) (2 * Math.PI));
            int projectiles = 10;
            for (int i = 0; i < projectiles; i++) {
                float angle = initialAngle + i * 2 * (float) Math.PI / projectiles;
                boolean dropRockyGel = i == 0 || GameRandom.globalRandom.getChance(0.25F);
                throwRock(angle + GameRandom.globalRandom.getFloatBetween((float) -Math.PI / 36F, (float) Math.PI / 36F), dropRockyGel);
            }
        }
    }

    public void throwRock(float angle, boolean dropRockyGel) {
        int targetX = this.getX() + (int) (Math.cos(angle) * 100);
        int targetY = this.getY() + (int) (Math.sin(angle) * 100);
        throwRock(targetX, targetY, dropRockyGel);
    }


    public void throwRock(int targetX, int targetY, boolean dropRockyGel) {
        Projectile projectile;
        float speed = GameRandom.globalRandom.getFloatBetween(40F, 50F);
        GameDamage damage = this.getLevel().baseBiome == AphBiomes.INFECTED_FIELDS ? rock_damage_if : rock_damage;

        if (dropRockyGel) {
            projectile = new RockyGelSlimeLootProjectile(this, this.x, this.y, targetX, targetY, speed, 640, damage, rock_knockback);
        } else {
            projectile = new RockyGelSlimeProjectile(this, this.x, this.y, targetX, targetY, speed, 640, damage, rock_knockback);
        }
        this.getLevel().entityManager.projectiles.add(projectile);
    }
}