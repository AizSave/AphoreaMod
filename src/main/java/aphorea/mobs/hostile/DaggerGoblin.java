package aphorea.mobs.hostile;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public abstract class DaggerGoblin extends GoblinMob {
    public static LootTable lootTable;
    public static HumanTexture humanTexture;
    public final String daggerType;

    public GameDamage gameDamage;

    public DaggerGoblin(String daggerType) {
        this.daggerType = daggerType != null ? daggerType : "copperdagger";

        if (Objects.equals(daggerType, "golddagger")) {
            gameDamage = new GameDamage(30.0F);
            this.setMaxHealth(80);
            this.setHealthHidden(80);
        } else if (Objects.equals(daggerType, "irondagger")) {
            gameDamage = new GameDamage(25.0F);
            this.setMaxHealth(70);
            this.setHealthHidden(70);
        } else {
            gameDamage = new GameDamage(20.0F);
            this.setMaxHealth(60);
            this.setHealthHidden(60);
        }
    }


    @Override
    public void init() {
        super.init();

        this.ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<GoblinMob>(
                () -> !this.getLevel().isCave && !this.getServer().world.worldEntity.isNight(),
                384, gameDamage, 25, 40000
        ) {
            public boolean attackTarget(GoblinMob mob, Mob target) {
                if (target != null) {
                    DaggerGoblin.this.attack((int) target.x, (int) target.y, true);
                }

                return super.attackTarget(mob, target);
            }
        });
    }

    @Override
    public LootTable getLootTable() {
        return new LootTable(
                lootTable,
                new LootItem(daggerType)
        );
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 6 - 26;
        int drawY = camera.getDrawY(y) - 28 - 26;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        float attackProgress = this.getAttackAnimProgress() / 2;
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = (new HumanDrawOptions(level, humanTexture)).sprite(sprite).dir(dir).mask(swimMask).light(light);
        if (this.isAttacking) {
            this.setupHumanAttackOptions(humanDrawOptions, new InventoryItem(daggerType), attackProgress);
        }

        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public void setupHumanAttackOptions(HumanDrawOptions humanDrawOptions, InventoryItem dagger, float attackProgress) {
        humanDrawOptions.itemAttack(dagger, null, attackProgress, this.attackDir.x, this.attackDir.y);
    }

    static {
        lootTable = new LootTable(
                GoblinMob.lootTable
        );
    }

    public static class CopperDaggerGoblin extends DaggerGoblin {

        public CopperDaggerGoblin() {
            super("copperdagger");
        }

    }

    public static class IronDaggerGoblin extends DaggerGoblin {

        public IronDaggerGoblin() {
            super("irondagger");
        }

    }

    public static class GoldDaggerGoblin extends DaggerGoblin {

        public GoldDaggerGoblin() {
            super("golddagger");
        }


        @Override
        protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
            super.onDeath(attacker, attackers);
        }
    }
}