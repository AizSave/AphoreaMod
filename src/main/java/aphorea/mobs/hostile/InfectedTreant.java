package aphorea.mobs.hostile;

import aphorea.utils.AphColors;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.axeToolItem.AxeToolItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InfectedTreant extends HostileMob {
    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static GameDamage collisionDamage = new GameDamage(50.0F);
    public int doAlpha = 0;
    public float jump = 0;

    public static int weaveTime = 250;
    public static float weaveAmount = 0.02F;
    public static int leavesCenterWidth = 45;
    public static int leavesMinHeight = 60;
    public static int leavesMaxHeight = 110;
    public static String leavesTextureName = "infectedleaves";
    public static Supplier<GameTextureSection> leavesTexture;
    protected final GameRandom drawRandom;

    static public float jumpHeight = 20;
    static public float jumpDurationMod = 2;

    public boolean mirrored;
    public int spriteY;


    public static LootTable lootTable = new LootTable(
            LootItem.between("infectedlog", 4, 5),
            LootItem.between("infectedsapling", 1, 2)
    );

    public InfectedTreant() {
        super(100);
        this.setSpeed(60.0F);
        this.setFriction(5.0F);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-16, -22, 32, 32);
        this.selectBox = new Rectangle(-56, -96, 56 * 2, 104);
        this.drawRandom = new GameRandom();
        this.mirrored = drawRandom.getChance(0.5F);
        this.spriteY = drawRandom.getIntBetween(0, 3);
    }

    @Override
    public GameDamage getCollisionDamage(Mob target) {
        return collisionDamage;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 6 * 32, collisionDamage, 0, 40000 * 20));
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0F, 0.6F, 100);
        if (Settings.windEffects) {
            float windSpeed = getLevel().weatherLayer.getWindSpeed();
            if (windSpeed > 0.2F) {
                float windAmount = getLevel().weatherLayer.getWindAmount(x / 32, y / 32) * 3.0F;
                if (windAmount > 0.5F) {
                    Point2D.Double windDir = getLevel().weatherLayer.getWindDirNormalized();
                    float buffer = 0.016666668F * windAmount * windSpeed;

                    while(buffer >= 1.0F || GameRandom.globalRandom.getChance(buffer)) {
                        --buffer;
                        this.spawnLeafParticles(getLevel(), (int) (x / 32), (int) (y / 32), leavesMinHeight, 1, windDir, windAmount * windSpeed);
                    }
                }
            }

        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (dx == 0 && dy == 0 && this.getHealthPercent() == 1) {
            doAlpha = 2;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        if (this.getHealthPercent() != 1 && this.canTakeDamage() && this.getMaxHealth() > 1) {
            tooltips.add(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 110;
        int addedY;
        if (dx == 0 && dy == 0) {
            jump = 0;
        } else {
            jump += 0.1F;

            if (jump > jumpDurationMod) {
                jump = 0;
            }

            addedY = (int) (Math.sin(jump / jumpDurationMod * Math.PI) * jumpHeight);
            drawY -= addedY;
        }

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        float alpha = 1.0F;
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(x - 48, y - 100, 128, 100);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5F;
            } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                alpha = 0.5F;
            }
        }

        Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, x / 32, y / 32, weaveTime, weaveAmount, 2, this.drawRandom, GameObject.getTileSeed(x / 32, y / 32, 0), mirrored, 3.0F);
        DrawOptions drawOptions = texture.initDraw()
                .sprite(0, spriteY, 128)
                .light(light)
                .alpha(alpha)
                .addPositionMod(waveChange)
                .mirror(mirrored, false)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        if (!isWaterWalking()) addShadowDrawables(tileList, x, y, light, camera, alpha);
    }

    protected void addShadowDrawables(OrderableDrawables list, int x, int y, GameLight light, GameCamera camera, float alpha) {
        if (!(Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY) && !this.isRiding()) {
            TextureDrawOptions shadowOptions = this.getShadowDrawOptions(x, y, light, camera, alpha);
            if (shadowOptions != null) {
                list.add((tm) -> shadowOptions.draw());
            }

        }
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera, float alpha) {
        GameTexture shadowTexture = texture_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 8 + 4;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(0, spriteY, shadowTexture.getWidth(), shadowTexture.getHeight() / 4).alpha(alpha).light(light)                .mirror(mirrored, false).pos(drawX, drawY);
    }

    @Override
    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        getLevel().makeGrassWeave((int) x, (int) y, weaveTime, true);

        int leaves = GameRandom.globalRandom.getIntBetween(0, 2);
        this.spawnLeafParticles(getLevel(), (int) x, (int) y, leavesMinHeight, leaves, new Point2D.Double(), 0.0F);
        return super.isHit(event, attacker);
    }

    @Override
    protected void doBeforeHitCalculatedLogic(MobBeforeHitCalculatedEvent event) {
        super.doBeforeHitCalculatedLogic(event);


    }

    @Override
    protected void doBeforeHitLogic(MobBeforeHitEvent event) {
        boolean prevent = true;
        if (event.attacker.getAttackOwner().isPlayer) {
            PlayerMob player = (PlayerMob) event.attacker.getAttackOwner();
            if (player.isAttacking && player.attackSlot.getItem(player.getInv()).item instanceof AxeToolItem) {
                prevent = false;
            }
        }

        if (prevent) {
            event.prevent();
        }

        super.doBeforeHitLogic(event);
    }

    @Override
    public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
        if (buff.buff == BuffRegistry.Debuffs.ON_FIRE) super.addBuff(buff, sendUpdatePacket);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int leaves = GameRandom.globalRandom.getIntBetween(15, 20);
        this.spawnLeafParticles(getLevel(), (int) x, (int) y, 20, leaves, new Point2D.Double(), 0.0F);
    }

    public void spawnLeafParticles(Level level, int x, int y, int minStartHeight, int amount, Point2D.Double windDir, float windSpeed) {
        if (leavesTexture != null) {
            TreeObject.spawnLeafParticles(level, x, y, leavesCenterWidth, minStartHeight, leavesMaxHeight, amount, windDir, windSpeed, leavesTexture);
        }
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int width = (int) tileScale;
        int height = (int) tileScale;
        Renderer.initQuadDraw(width, height).color(AphColors.infected_dark).draw(x - width / 2, y - height / 2);
    }

}