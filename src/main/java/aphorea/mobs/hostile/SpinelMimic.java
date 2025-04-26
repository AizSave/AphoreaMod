package aphorea.mobs.hostile;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CaveChestLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class SpinelMimic extends HostileMob {
    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static GameDamage damage = new GameDamage(60.0F, 20F);
    public int jump = 0;

    static public float jumpHeight = 30;
    static public int jumpDuration = 12;

    public static LootTable lootTable = new LootTable(
            new LootItem("spinelchest"),
            RotationLootItem.globalLootRotation(
                    new LootItem("brokenkora"),
                    new LootItem("ninjascarf"),
                    new LootItem("adrenalinecharm"),
                    new LootItem("shotgunsaber"),
                    new LootItem("cursedmedallion")
            ),
            RotationLootItem.globalLootRotation(
                    CaveChestLootTable.potions,
                    CaveChestLootTable.bars,
                    CaveChestLootTable.extraItems
            )
    );

    int adjustY = 22;

    public SpinelMimic() {
        super(300);
        this.setArmor(20);
        this.setSpeed(60.0F);
        this.setFriction(5.0F);
        this.collision = new Rectangle(-10, 14 - adjustY, 20, 10);
        this.hitBox = new Rectangle(-14, 10 - adjustY, 28, 14);
        this.selectBox = new Rectangle(-16, -6 - adjustY, 32, 32);
        this.setKnockbackModifier(0);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 6 * 32, damage, 0, 40000 * 20));
        this.jump = 0;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 7; i++) {
            getLevel().entityManager.addParticle(new FleshParticle(
                    getLevel(), texture,
                    i, 5, 16,
                    x, y, 20f,
                    knockbackX, knockbackY
            ), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (dx == 0 && dy == 0) {
            jump = 0;
        } else {
            jump++;

            if (jump > jumpDuration) {
                jump = 0;
            }
        }
        if (jump == 0) {
            this.setFriction(20);
        } else {
            this.setFriction(0.1F);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (dx == 0 && dy == 0) {
            jump = 0;
        } else {
            jump++;

            if (jump > jumpDuration) {
                jump = 0;
            }
        }
        if (jump == 0) {
            this.setFriction(20);
        } else {
            this.setFriction(0.1F);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 32 - adjustY;

        drawY -= (int) (Math.sin((float) jump / jumpDuration * Math.PI) * jumpHeight);
        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        Point sprite = getAnimSprite(x, y, getDir());
        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 32, 64)
                .light(light)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!isWaterWalking()) addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    protected void addShadowDrawables(OrderableDrawables list, int x, int y, GameLight light, GameCamera camera) {
        if (!(Boolean) this.buffManager.getModifier(BuffModifiers.INVISIBILITY) && !this.isRiding()) {
            TextureDrawOptions shadowOptions = this.getShadowDrawOptions(x, y, light, camera);
            if (shadowOptions != null) {
                list.add((tm) -> shadowOptions.draw());
            }
        }
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = texture_shadow;
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 32 - adjustY;
        drawY += this.getBobbing(x, y);

        Point sprite = getAnimSprite(x, y, getDir());
        return shadowTexture.initDraw().sprite(sprite.x, sprite.y, 32, 64).light(light).pos(drawX, drawY);
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(dir, jump > 0 ? 1 : 0);
    }
}
