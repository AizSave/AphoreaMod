package aphorea.mobs.hostile;

import aphorea.data.AphSwampLevelData;
import aphorea.projectiles.mob.PinkWitchProjectile;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class PinkWitch extends FlyingHostileMob {

    public static GameDamage attack = new GameDamage(DamageTypeRegistry.TRUE, 20);
    public static int attack_knockback = 50;

    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(400, 500, 600, 700, 800);

    public int threeAttack = 5;

    public static GameTexture texture;

    public static LootTable lootTable = new LootTable(
            LootItem.between("stardust", 2, 3),
            RotationLootItem.globalLootRotation(
                    ChanceLootItem.between("healthpotion", 1, 2, (new GNDItemMap())),
                    ChanceLootItem.between("manapotion", 1, 2, (new GNDItemMap()))
            ),
            RotationLootItem.globalLootRotation(
                    new LootItem("broom", (new GNDItemMap())), // 20% | 40%
                    new LootItem("broom", (new GNDItemMap())), // 20% |
                    new LootItem("witchmedallion", (new GNDItemMap())), // 20%
                    new LootItem("pinkwitchhat", (new GNDItemMap())), // 20%
                    new ChanceLootItem(0.25F, "magicalvial", (new GNDItemMap())) // 5%
            )
    );

    public static AphSwampLevelData aphoreaSwampLevelData = new AphSwampLevelData();

    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        AphSwampLevelData currentData = aphoreaSwampLevelData.getData(client.getLevel());
        if (currentData.witchesnulled) {
            return false;
        } else {
            return super.isValidSpawnLocation(server, client, targetX, targetY);
        }
    }

    public PinkWitch() {
        super(600);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        setSpeed(60);
        setFriction(1);

        collision = new Rectangle(-18, -18, 36, 36);
        hitBox = new Rectangle(-26, -26, 52, 48);
        selectBox = new Rectangle(-26, -42, 52, 69);
    }

    @Override
    public void init() {
        super.init();
        PlayerChaserWandererAI<PinkWitch> playerChaserAI = new PlayerChaserWandererAI<PinkWitch>(null, 24 * 32, 320, 4000, true, true) {
            public boolean attackTarget(PinkWitch mob, Mob target) {
                if (mob.canAttack()) {
                    mob.attack(target.getX(), target.getY(), false);
                    mob.getLevel().entityManager.projectiles.add(new PinkWitchProjectile(mob.getLevel(), mob, mob.x, mob.y, target.x, target.y, 120.0F, 640, attack, attack_knockback));
                    if (mob.isServer()) {
                        threeAttack--;
                    }
                    if (threeAttack >= 0) {
                        attackCooldown = (int) (500 + 1000 * mob.getHealthPercent());
                    } else {
                        attackCooldown = (int) (100 + 200 * mob.getHealthPercent());
                        if (threeAttack == -2 && mob.isServer()) {
                            threeAttack = 5;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        };
        this.ai = new BehaviourTreeAI<>(this, playerChaserAI);
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
                    GameRandom.globalRandom.nextInt(8),
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

        addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }
}
