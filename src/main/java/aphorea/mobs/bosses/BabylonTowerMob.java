package aphorea.mobs.bosses;

import aphorea.levelevents.babylon.BabylonTowerFallingCrystalAttackEvent;
import aphorea.mobs.bosses.minions.HearthCrystalMob;
import aphorea.objects.BabylonEntranceObject;
import aphorea.objects.BabylonTowerObject;
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
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.List;
import java.util.*;

public class BabylonTowerMob extends BossMob {
    public static int BOSS_AREA_RADIUS = 1024;
    private static final AphAreaList searchArea = new AphAreaList(
            new AphArea(BOSS_AREA_RADIUS, AphColors.spinel)
    );
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(5000, 6000, 7000, 8500, 10000);
    private int aliveTimer;
    public static GameTexture icon;

    protected MobHealthScaling scaling = new MobHealthScaling(this);


    public BabylonTowerMob() {
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

    public void keepAlive(BabylonTowerObject.BabylonTowerObjectEntity entity) {
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
        if (object != null) {
            ObjectEntity objectEntity = object.getCurrentObjectEntity(getLevel(), x, y);
            if (objectEntity instanceof BabylonTowerObject.BabylonTowerObjectEntity) {
                objectEntity.remove();
                getServer().network.sendToClientsAtEntireLevel(new AphRemoveObjectEntity(x, y), getLevel());

                boolean openingStaircase = false;
                if (!(this.getLevel() instanceof IncursionLevel)) {
                    Point entrancePosition = new Point(x + 1, y + 2);
                    if (!this.getLevel().getLevelObject(entrancePosition.x, entrancePosition.y).getMultiTile().getMasterObject().getStringID().equals("babylonentrance")) {
                        this.getLevel().entityManager.addLevelEvent(new BabylonEntranceObject.BabylonEntranceEvent(x + 1, y + 2));
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

    public int getParts() {
        return (int) Math.ceil(this.getHealthPercent() / 0.2F) - 1;
    }

    public int projectileRate() {
        return getParts() + 3;
    }

    public boolean hearthCrystalClose() {
        return getLevel().entityManager.mobs.stream().anyMatch(m -> Objects.equals(m.getStringID(), "hearthcrystal") && m.getDistance(this) < BOSS_AREA_RADIUS);
    }

    @Override
    protected void doBeforeHitLogic(MobBeforeHitEvent event) {
        if (hearthCrystalClose()) {
            event.damage = event.damage.setDamage(event.damage.damage / 2);
        }
        super.doBeforeHitLogic(event);
    }

    @Override
    public void init() {
        super.init();

        SoundManager.playSound(GameResources.roar, SoundEffect.effect(this)
                .volume(0.7f)
                .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));

        ai = new BehaviourTreeAI<>(this, new BabylonTowerAI<>(), new FlyingAIMover());
    }

    public static class BabylonTowerAI<T extends BabylonTowerMob> extends SelectorAINode<T> {
        static GameDamage projectileDamage = new GameDamage(20F, 60F);

        public ArrayList<BabylonTowerActionAiNode> stages = new ArrayList<>();
        public int stagesUntilNow = 0;
        public int currentStage = 0;
        public int currentStageTick = 0;
        public int currentStageTickDuration = 6000 / 50;
        public boolean dragonSummoned = false;

        public Map<String, Object> saveData = new HashMap<>();

        public BabylonTowerAI() {
            this.addChild(
                    new AINode<T>() {

                        @Override
                        protected void onRootSet(AINode<T> aiNode, T mob, Blackboard<T> blackboard) {
                        }

                        @Override
                        public void init(T mob, Blackboard<T> blackboard) {
                        }

                        @Override
                        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                            if (mob.hearthCrystalClose() && mob.isServer() && currentStageTick % 20 == 0) {
                                mob.setHealth((int) (mob.getHealth() + mob.getMaxHealth() * 0.002F * streamPossibleTargets(mob).count() - 1));
                            }
                            if (!dragonSummoned && mob.getHealthPercent() <= 0.6F) {
                                dragonSummoned = true;
                            }
                            return AINodeResult.FAILURE;
                        }
                    }
            );
            this.addChild(
                    new BabylonTowerActionAiNode() {
                        @Override
                        public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                            if (time % (50 * mob.projectileRate()) == 0) summonRandomCrystal(mob);
                        }

                        @Override
                        public int getStageDuration(T mob) {
                            return 5000;
                        }
                    }
            );
            this.addChild(
                    new BabylonTowerActionAiNode() {
                        @Override
                        public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                            if (time % (100 * mob.projectileRate()) == 0)
                                summonCrystalToAllTargets(mob, GameRandom.globalRandom.getFloatBetween(0F, 2F), 80);
                        }

                        @Override
                        public int getStageDuration(T mob) {
                            return 5000;
                        }
                    }
            );
            this.addChild(
                    new BabylonTowerActionAiNode() {
                        @Override
                        public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                            if (time % (200 * mob.projectileRate()) == 0)
                                summonCrystalToAllTargets(mob, GameRandom.globalRandom.getFloatBetween(0F, 2F), 80);
                            if (time % (300 * mob.projectileRate()) == 0) {
                                float angle = GameRandom.globalRandom.getFloatBetween(0, (float) (Math.PI * 2));
                                float prediction = GameRandom.globalRandom.getFloatBetween(0F, 0.5F);
                                for (int i = 0; i < 12; i++) {
                                    summonCrystalAroundAllTargets(mob, prediction, 5, angle + (float) Math.PI * i / 6, 100);
                                }
                            }
                        }

                        @Override
                        public int getStageDuration(T mob) {
                            return 5000;
                        }
                    }
            );
            this.addChild(
                    new BabylonTowerActionAiNode() {
                        @Override
                        public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                            int health = 50 + (int) (25 * streamPossibleTargets(mob).count());
                            boolean clockWise = GameRandom.globalRandom.getChance(0.5F);
                            switch (GameRandom.globalRandom.getIntBetween(0, 5)) {
                                case 0: {
                                    float angle = GameRandom.globalRandom.getFloatBetween(0, (float) (Math.PI * 2));
                                    summonHearthCrystalMoved(mob, 0.15F, angle, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, 0.15F, angle + (float) Math.PI * 2 / 3, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, 0.15F, angle + (float) Math.PI * 4 / 3, health, 0, 0.15F, 0.5F, clockWise);
                                    break;
                                }
                                case 1: {
                                    float angle = GameRandom.globalRandom.getFloatBetween(0, (float) (Math.PI * 2));

                                    summonHearthCrystalMoved(mob, 0.15F, angle, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, 0.15F, angle + (float) Math.PI * 2 / 3, health, (float) Math.PI * 2 / 3, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, 0.15F, angle + (float) Math.PI * 4 / 3, health, (float) Math.PI * 4 / 3, 0.15F, 0.5F, clockWise);
                                    break;
                                }
                                case 2: {
                                    summonHearthCrystalCenter(mob, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalCenter(mob, health, (float) Math.PI * 2 / 3, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalCenter(mob, health, (float) Math.PI * 4 / 3, 0.15F, 0.5F, clockWise);
                                    break;
                                }
                                case 3: {
                                    summonHearthCrystalCenter(mob, health / 2, 0, 0.25F, 0.35F, !clockWise);
                                    summonHearthCrystalCenter(mob, health, 0, 0.15F, 0.65F, clockWise);
                                    summonHearthCrystalCenter(mob, health, (float) Math.PI, 0.15F, 0.65F, clockWise);
                                    break;
                                }
                                case 4: {
                                    float angle = GameRandom.globalRandom.getFloatBetween(0, (float) (Math.PI * 2));
                                    float distanceFromCenter = GameRandom.globalRandom.getFloatBetween(0.05F, 0.2F);

                                    summonHearthCrystalMoved(mob, distanceFromCenter, angle, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, distanceFromCenter, angle + (float) Math.PI * 2 / 3, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, distanceFromCenter, angle + (float) Math.PI * 4 / 3, health, 0, 0.15F, 0.5F, clockWise);
                                    break;
                                }
                                case 5: {
                                    float angle = GameRandom.globalRandom.getFloatBetween(0, (float) (Math.PI * 2));
                                    float distanceFromCenter = GameRandom.globalRandom.getFloatBetween(0.05F, 0.2F);

                                    summonHearthCrystalMoved(mob, distanceFromCenter, angle, health, 0, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, distanceFromCenter, angle + (float) Math.PI * 2 / 3, health, (float) Math.PI * 2 / 3, 0.15F, 0.5F, clockWise);
                                    summonHearthCrystalMoved(mob, distanceFromCenter, angle + (float) Math.PI * 4 / 3, health, (float) Math.PI * 4 / 3, 0.15F, 0.5F, clockWise);
                                    break;
                                }

                            }
                        }

                        @Override
                        public int getStageDuration(T mob) {
                            return 50;
                        }
                    }
            );

            this.addChild(
                    new BabylonTowerActionAiNode() {
                        @Override
                        public void startStage(T mob) {
                            super.startStage(mob);
                            saveData.put("startAngle", GameRandom.globalRandom.getFloatBetween(0, (float) Math.PI * 2));
                            saveData.put("clockWise", GameRandom.globalRandom.getChance(0.5F));
                        }

                        @Override
                        public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                            if (time % (100 * mob.projectileRate()) == 0) summonRandomCrystal(mob);
                            float startAngle = (float) saveData.get("startAngle");
                            boolean clockWise = (boolean) saveData.get("clockWise");
                            float angleProgress = startAngle + progress * (float) Math.PI * 4 * (clockWise ? 1 : -1);
                            float distance = BOSS_AREA_RADIUS * (0.05F + progress * 0.9F);
                            for (int i = 0; i < 12; i++) {
                                float targetX = mob.x + distance * (float) Math.cos(angleProgress + (float) Math.PI * i / 6);
                                float targetY = mob.y + distance * (float) Math.sin(angleProgress + (float) Math.PI * i / 6);
                                summonFallingCrystal(mob, targetX, targetY, 4);
                            }
                        }

                        @Override
                        public int getStageDuration(T mob) {
                            return 10000;
                        }
                    }
            );
        }

        public static ArrayList<Integer> projectileStages = new ArrayList<>();
        public static ArrayList<Integer> projectileBulkStages = new ArrayList<>();
        public static int hearthPilarStage = 3;

        static {
            Collections.addAll(projectileStages, 0, 1, 2);
            Collections.addAll(projectileBulkStages, 4);
        }

        public void selectNextStage(T mob) {
            stagesUntilNow++;

            int selected;
            if (stagesUntilNow == 5 || stagesUntilNow % 10 == 0) {
                selected = hearthPilarStage;
            } else if (stagesUntilNow % 5 == 0) {
                selected = selectStage(projectileBulkStages);
            } else {
                selected = selectStage(projectileStages);
            }

            currentStage = selected;
            currentStageTick = 0;
            currentStageTickDuration = stages.get(currentStage).getStageDuration(mob) / 50;
            stages.get(currentStage).startStage(mob);
        }

        public int selectStage(ArrayList<Integer> stages) {
            int selected;
            do {
                selected = GameRandom.globalRandom.getOneOf(stages);
            } while (selected == currentStage);
            return selected;
        }

        abstract public class BabylonTowerActionAiNode extends AINode<T> {
            public final int stageNumber;

            public BabylonTowerActionAiNode() {
                this.stageNumber = stages.size();
                stages.add(this);
            }

            @Override
            protected void onRootSet(AINode<T> aiNode, T t, Blackboard<T> blackboard) {

            }

            @Override
            public void init(T t, Blackboard<T> blackboard) {

            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (currentStage != stageNumber) {
                    return AINodeResult.FAILURE;
                } else if (currentStageTickDuration <= currentStageTick) {
                    selectNextStage(mob);
                    return AINodeResult.SUCCESS;
                } else {
                    currentStageTick++;
                    doTickAction(mob, currentStageTick * 50, currentStageTickDuration * 50, (float) currentStageTick / currentStageTickDuration, blackboard);
                    if (currentStageTickDuration <= currentStageTick) {
                        selectNextStage(mob);
                    }
                    return AINodeResult.SUCCESS;
                }
            }

            abstract public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard);

            abstract public int getStageDuration(T mob);

            public void startStage(T mob) {
            }
        }

        public Mob getRandomTarget(T mob) {
            ArrayList<Mob> list = new ArrayList<>();
            streamPossibleTargets(mob).forEach(list::add);
            return GameRandom.globalRandom.getOneOf(list);
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob) {
            return new TargetFinderDistance<>(BOSS_AREA_RADIUS).streamPlayersInRange(new Point((int) mob.x, (int) mob.y), mob).filter((m) -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map((m) -> m);
        }

        public void summonCrystalToAllTargets(T mob, float prediction, int imprecision) {
            streamPossibleTargets(mob).forEach(target -> summonCrystalToTarget(mob, target, target.dx * prediction, target.dy * prediction, imprecision));
        }

        public void summonCrystalAroundAllTargets(T mob, float prediction, int imprecision, float angle, float distance) {
            streamPossibleTargets(mob).forEach(target -> summonCrystalToTarget(mob, target, target.dx * prediction + (float) Math.cos(angle) * distance, target.dy * prediction + (float) Math.sin(angle) * distance, imprecision));
        }

        public void summonCrystalToTarget(T mob, Mob target, float extraX, float extraY, int imprecision) {
            summonFallingCrystal(mob, target.x + extraX, target.y + extraY, imprecision);
        }

        public void summonRandomCrystal(T mob) {
            summonRandomCrystalBetweenDistances(mob, 0, 1);
        }

        public void summonRandomCrystalBetweenDistances(T mob, float minDistance, float maxDistance) {
            summonRandomCrystalAtDistance(mob, minDistance + GameRandom.globalRandom.getFloatBetween(0, maxDistance - minDistance));
        }

        public void summonRandomCrystalAtDistance(T mob, float distance) {
            float angle = GameRandom.globalRandom.getFloatBetween(0, (float) (Math.PI * 2));
            int targetX = (int) (mob.x + Math.cos(angle) * BOSS_AREA_RADIUS * distance);
            int targetY = (int) (mob.y + Math.sin(angle) * BOSS_AREA_RADIUS * distance);
            summonFallingCrystal(mob, targetX, targetY);
        }

        public void summonFallingCrystal(T mob, float targetX, float targetY, int imprecision) {
            summonFallingCrystal(mob, GameRandom.globalRandom.getFloatOffset(targetX, imprecision), GameRandom.globalRandom.getFloatOffset(targetY, imprecision));
        }

        public void summonFallingCrystal(T mob, float targetX, float targetY) {
            if (mob.getDistance(targetX, targetY) <= BOSS_AREA_RADIUS) {
                mob.getLevel().entityManager.addLevelEvent(new BabylonTowerFallingCrystalAttackEvent(mob, (int) targetX, (int) targetY, GameRandom.globalRandom, projectileDamage));
            }
        }

        public void summonHearthCrystalCenter(T mob, int health, float angleOffset, float radius, float constantTime, boolean clockWise) {
            if (mob.isServer()) {
                HearthCrystalMob hearthCrystalMob = (HearthCrystalMob) MobRegistry.getMob("hearthcrystal", mob.getLevel());
                hearthCrystalMob.setMaxHealth(health);
                hearthCrystalMob.setHealth(health);
                hearthCrystalMob.setCircularMovement(mob.getX(), mob.getY(), angleOffset, (float) BOSS_AREA_RADIUS * radius, constantTime, clockWise);
                mob.getLevel().entityManager.addMob(hearthCrystalMob, mob.x, mob.y);
            }
        }

        public void summonHearthCrystalMoved(T mob, float distanceFromCenter, float distanceAngle, int health, float angleOffset, float radius, float constantTime, boolean clockWise) {
            if (mob.isServer()) {
                HearthCrystalMob hearthCrystalMob = (HearthCrystalMob) MobRegistry.getMob("hearthcrystal", mob.getLevel());
                hearthCrystalMob.setMaxHealth(health);
                hearthCrystalMob.setHealth(health);
                hearthCrystalMob.setCircularMovement(mob.getX() + (int) (BOSS_AREA_RADIUS * distanceFromCenter * Math.cos(distanceAngle)), mob.getY() + (int) (BOSS_AREA_RADIUS * distanceFromCenter * Math.sin(distanceAngle)), angleOffset, (float) BOSS_AREA_RADIUS * radius, constantTime, clockWise);
                mob.getLevel().entityManager.addMob(hearthCrystalMob, mob.x, mob.y);
            }
        }

    }

}
