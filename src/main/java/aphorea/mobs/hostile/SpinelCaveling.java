package aphorea.mobs.hostile;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SpinelCaveling extends HostileMob {
    public static GameDamage collision_damage = new GameDamage(40);
    public static int collision_knockback = 50;

    public static LootTable lootTable = new LootTable(
            new OneOfTicketLootItems(
                    5, new ChanceLootItem(0.25F, "spinel"),
                    1, new ChanceLootItem(0.25F, "lifespinel")
            )
    );
    public static HumanTexture texture;
    public InventoryItem item;

    public SpinelCaveling() {
        super(120);
        this.setArmor(10);
        this.setSpeed(60);
        this.setFriction(3.0F);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -40, 32, 50);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 4;
        this.swimSinkOffset = 0;
        this.item = new InventoryItem("spinel", 0);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void init() {
        super.init();
        ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 12 * 32, collision_damage, collision_knockback, 40000));
        ArrayList<InventoryItem> items = lootTable.getNewList(new GameRandom(this.getUniqueID()), 1F);
        if (!items.isEmpty()) {
            this.item = items.get(0);
        }
        dropsLoot = false;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), texture.body, i, 8, 32, this.x, this.y, 20.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (item.getAmount() > 0) {
            getLevel().entityManager.pickups.add(new ItemPickupEntity(getLevel(), item, x, y, 0, 0));
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 48;
        int dir = this.getDir();
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        boolean hasSpelunker = perspective != null && perspective.buffManager.getModifier(BuffModifiers.SPELUNKER);

        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd rightArmOptions = texture.rightArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, this.getID(), this).pos(drawX, drawY);

        TextureDrawOptionsEnd bodyOptions = texture.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, this.getID(), this).pos(drawX, drawY);
        Color drawColor = this.item.item.getDrawColor(this.item, perspective);
        int itemBobbing = sprite.x != 1 && sprite.x != 3 ? 0 : 2;
        GameLight itemLight = hasSpelunker ? light.minLevelCopy(100.0F) : light;
        final TextureDrawOptionsEnd itemOptions = this.item.item.getItemSprite(this.item, perspective).initDraw().colorLight(drawColor, itemLight).mirror(sprite.y < 2, false).size(32).posMiddle(drawX + 32, drawY + 16 + itemBobbing + swimMask.drawYOffset);

        final TextureDrawOptionsEnd leftArmOptions = texture.leftArms.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).spelunkerLight(light, hasSpelunker, this.getID(), this).pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                boolean hasObject = item.getAmount() > 0;

                swimMask.use();
                if (hasObject) rightArmOptions.draw();
                bodyOptions.draw();
                swimMask.stop();

                if (hasObject) itemOptions.draw();

                swimMask.use();
                if (hasObject) leftArmOptions.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptions shadow = MobRegistry.Textures.caveling_shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add((tm) -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    public boolean isLavaImmune() {
        return true;
    }

    @Override
    public boolean isSlimeImmune() {
        return true;
    }

    @Override
    public int getTileWanderPriority(TilePosition pos) {
        return 0;
    }

    @Override
    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotSolidTile().checkNotOnSurfaceInsideOnFloor().checkNotLevelCollides().checkTile((x, y) -> this.getLevel().getLightLevel(x, y).getLevel() <= 50.0F);
    }

}
