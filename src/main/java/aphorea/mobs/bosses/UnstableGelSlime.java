package aphorea.mobs.bosses;

import aphorea.projectiles.mob.MiniUnstableGelSlimeProjectile;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UnstableGelSlime extends FlyingBossMob {

    public static int baseSpeed = 20;
    public static int speedPerAnger = 2;

    public static GameTexture icon;
    public static GameTexture texture;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(1200, 1400, 1600, 1800, 2000);

    public final CoordinateMobAbility teleportAbility;

    public static LootTable lootTable = new LootTable(
            LootItem.between("unstablegel", 10, 18)
    );

    public static LootTable privateLootTable = new LootTable(
            RotationLootItem.globalLootRotation(
                    new LootItem("unstablegelsword", (new GNDItemMap())),
                    new LootItem("unstablegelgreatbow", (new GNDItemMap())),
                    new LootItem("unstablegelstaff", (new GNDItemMap())),
                    new LootItem("volatilegelstaff", (new GNDItemMap()))
            ),
            new LootItem("unstableperiapt"),
            new LootItem("runeofunstablegelslime"),
            new ChanceLootItem(0.01F, "unstablegelsaber"),
            new ConditionLootItem("gelslimenullifier", (r, o) -> {
                ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
                return client != null && client.playerMob.getInv().getAmount(ItemRegistry.getItem("gelslimenullifier"), false, false, true, true, "have") == 0;
            })
    );

    protected MobHealthScaling scaling = new MobHealthScaling(this);

    public UnstableGelSlime() {
        super(1500);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);

        this.setArmor(10);
        this.setSpeed(baseSpeed);
        this.setFriction(3);
        this.setKnockbackModifier(0.0F);

        staySmoothSnapped = true;

        this.collision = new Rectangle(-45, -40 - 5, 90, 55);
        this.hitBox = new Rectangle(-50, -50 - 5, 100, 70);
        this.selectBox = new Rectangle(-55, -80 - 9, 110, 100);

        this.teleportAbility = this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                if (UnstableGelSlime.this.isClient()) {
                    UnstableGelSlime.this.getLevel().entityManager.addParticle(new SmokePuffParticle(UnstableGelSlime.this.getLevel(), UnstableGelSlime.this.x, UnstableGelSlime.this.y, 92, AphColors.unstableGel), Particle.GType.CRITICAL);
                    UnstableGelSlime.this.getLevel().entityManager.addParticle(new SmokePuffParticle(UnstableGelSlime.this.getLevel(), (float) x, (float) y, 92, AphColors.unstableGel), Particle.GType.CRITICAL);
                }

                UnstableGelSlime.this.setPos((float) x, (float) y, true);
            }
        });

    }


    @Override
    public void init() {
        super.init();

        SoundManager.playSound(GameResources.roar, SoundEffect.effect(this)
                .volume(0.7f)
                .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));

        ai = new BehaviourTreeAI<>(this, new UnstableGelSlimeAI<>(), new FlyingAIMover());
    }


    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; i++) {
            getLevel().entityManager.addParticle(new FleshParticle(
                    getLevel(), texture == null ? GameTexture.fromFile("mobs/unstablegelslime") : texture,
                    GameRandom.globalRandom.nextInt(5),
                    8,
                    96,
                    x, y, 20f,
                    knockbackX, knockbackY
            ), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 96;
        int drawY = camera.getDrawY(y) - 153 - 9;

        Point sprite = getAnimSprite(x, y, getDir());

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 192)
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
    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.ancientVulture_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 - 9;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(0, 0, shadowTexture.getWidth(), shadowTexture.getHeight()).light(light).pos(drawX, drawY);
    }

    @Override
    public int getRockSpeed() {
        return 20;
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
        icon.initDraw().sprite(0, 0, 64).size(32, 32).draw(drawX, drawY);
    }

    @Override
    public boolean isHealthBarVisible() {
        return super.isHealthBarVisible();
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

        attackers.stream().map(Attacker::getAttackOwner).filter((m) -> m != null && m.isPlayer).distinct().forEach((m) -> this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), ((PlayerMob) m).getServerClient()));

        for (int i = 0; i < 4; i++) {
            Mob invocar = MobRegistry.getMob("miniunstablegelslime", this.getLevel());

            this.getLevel().entityManager.addMob(invocar, randomPositionClose(this.x), randomPositionClose(this.y));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        SoundManager.setMusic(MusicRegistry.TheFirstTrial, SoundManager.MusicPriority.EVENT, 1.5F);
        EventStatusBarManager.registerMobHealthStatusBar(this);
        BossNearbyBuff.applyAround(this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
    }

    public float randomPositionClose(float n) {
        return n + GameRandom.globalRandom.getFloatBetween(5, 10);
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, this), true);
    }

    @Override
    public void addBuff(ActiveBuff buff, boolean sendUpdatePacket) {
        if (buff.buff != AphBuffs.STICKY) super.addBuff(buff, sendUpdatePacket);
    }

    public static class UnstableGelSlimeAI<T extends UnstableGelSlime> extends SelectorAINode<T> {
        static GameDamage collisionAttackDamage = new GameDamage(50);
        static int collisionAttackKnockback = 300;

        static int defaultMaxAngerTeleportCooldownDuration = 3000;

        static GameDamage unstableGelSlimeProjectileDamage = new GameDamage(30);
        static int unstableGelSlimeProjectileKnockback = 200;

        static int defaultThrowSlimesNumber = 2;
        static int defaultThrowSlimesCooldownDuration = 10000;

        int maxAngerTeleportCooldownTimer = defaultMaxAngerTeleportCooldownDuration / 50;

        int throwSlimesTimer;
        int throwSlimesCooldownTimer = defaultThrowSlimesCooldownDuration / 50;
        int throwSlimesNumber;

        int anger;
        int inactiveTimer;

        public UnstableGelSlimeAI() {
            // Despawn
            this.addChild(new AINode<T>() {
                @Override
                protected void onRootSet(AINode<T> aiNode, T t, Blackboard<T> blackboard) {

                }

                @Override
                public void init(T t, Blackboard<T> blackboard) {

                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (streamPossibleTargets(mob).count() == 0) {
                        inactiveTimer++;
                        if (inactiveTimer > 100) {
                            mob.remove();
                        }
                        return AINodeResult.SUCCESS;
                    } else {
                        if (mob.getWorldEntity().isNight()) {
                            mob.remove();

                            PacketChatMessage menssage = new PacketChatMessage(Localization.translate("message", "unstablegelslime_night"));
                            GameUtils.streamServerClients(mob.getLevel()).forEach((j) -> j.sendPacket(menssage));

                            return AINodeResult.SUCCESS;
                        } else {
                            inactiveTimer = 0;
                            return AINodeResult.FAILURE;
                        }
                    }
                }
            });

            // Increase Anger & Spawn Mini Unstable Gel Slimes & Teleport
            this.addChild(new AINode<T>() {
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                public void init(T mob, Blackboard<T> blackboard) {
                }

                public AINodeResult tick(T mob, Blackboard<T> blackboard) {

                    if (anger == 10) {
                        if (!mob.buffManager.hasBuff("unstablegelslimerush")) {
                            int targets = (int) streamPossibleTargets(mob).count();

                            SoundManager.playSound(GameResources.roar, SoundEffect.effect(mob)
                                    .volume(0.7f)
                                    .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));

                            maxAngerTeleportCooldownTimer = defaultMaxAngerTeleportCooldownDuration / 50;

                            spawnMiniUnstableGelSlimes(mob, Math.min(targets, 4));

                            blackboard.put("currentTarget", mob);

                            mob.addBuff(new ActiveBuff(BuffRegistry.getBuff("unstablegelslimerush"), mob, 3000, mob), true);
                            mob.addBuff(new ActiveBuff(AphBuffs.IMMORTAL, mob, 1000, mob), true);

                            Point point = getTeleportPoint(mob);
                            if (point != null) {
                                mob.teleportAbility.runAndSend(point.x, point.y);
                            }
                            return AINodeResult.SUCCESS;
                        } else {
                            return AINodeResult.FAILURE;
                        }
                    } else if (mob.getHealthPercent() < 1 - (0.09 * (1 + anger))) {
                        anger++;

                        mob.buffManager.applyModifiers(BuffModifiers.SPEED_FLAT, (float) anger * speedPerAnger);

                        SoundManager.playSound(GameResources.roar, SoundEffect.effect(mob)
                                .volume(0.7f)
                                .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
                        PacketChatMessage mensaje = new PacketChatMessage(Localization.translate("message", anger == 10 ? "unstablegelslime_fullanger" : "unstablegelslime_anger", "anger", anger));
                        GameUtils.streamServerClients(mob.getLevel()).forEach((j) -> j.sendPacket(mensaje));

                        int targets = (int) streamPossibleTargets(mob).count();

                        int number;
                        if (anger >= 8) {
                            number = Math.min(targets * 3, 12);
                        } else if (anger >= 5) {
                            number = Math.min(targets * 2, 8);
                        } else if (anger >= 2) {
                            number = Math.min(targets, 4);
                        } else {
                            number = Math.min(targets - 1, 2);
                        }
                        if (number > 0) {
                            spawnMiniUnstableGelSlimes(mob, number);
                        }

                        blackboard.put("currentTarget", mob);

                        mob.addBuff(new ActiveBuff(BuffRegistry.getBuff("unstablegelslimerush"), mob, 3000, mob), true);
                        mob.addBuff(new ActiveBuff(AphBuffs.IMMORTAL, mob, 1000, mob), true);

                        Point point = getTeleportPoint(mob);
                        if (point == null) {
                            return AINodeResult.FAILURE;
                        } else {
                            mob.teleportAbility.runAndSend(point.x, point.y);
                            return AINodeResult.SUCCESS;
                        }
                    } else {
                        return AINodeResult.FAILURE;
                    }
                }
            });

            // Throw Mini Unstable Gel Slimes
            this.addChild(new AINode<T>() {

                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                public void init(T mob, Blackboard<T> blackboard) {
                }

                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (anger >= 10) {
                        return AINodeResult.FAILURE;
                    }
                    if (!mob.buffManager.hasBuff("unstablegelslimerush")) {
                        if (throwSlimesNumber > 0) {
                            processSlimeThrow(mob);
                            return AINodeResult.SUCCESS;
                        } else if (throwSlimesCooldownTimer <= 0) {
                            prepareSlimeThrow(mob);
                            return AINodeResult.SUCCESS;
                        } else {
                            throwSlimesCooldownTimer--;
                            return AINodeResult.FAILURE;
                        }
                    } else {
                        throwSlimesCooldownTimer--;
                        return AINodeResult.FAILURE;
                    }
                }

                private void processSlimeThrow(T mob) {
                    throwSlimesTimer--;

                    if (shouldThrowSlime()) {
                        throwSlimesNumber--;
                        playSlimeSound(mob);
                        launchSlimeProjectile(mob);

                        if (throwSlimesTimer == 0 && throwSlimesNumber > 0) {
                            applyTemporaryBuffs(mob, throwSlimesNumber * 100 + 100);
                        }
                    }
                }

                private boolean shouldThrowSlime() {
                    return (throwSlimesTimer < 0 && throwSlimesTimer % 2 == 0) || (throwSlimesTimer > 0 && throwSlimesTimer % 10 == 0);
                }

                private void playSlimeSound(T mob) {
                    SoundManager.playSound(GameResources.slimesplash, SoundEffect.effect(mob)
                            .volume(0.7f)
                            .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
                }

                private void applyTemporaryBuffs(T mob, int duration) {
                    mob.addBuff(new ActiveBuff(AphBuffs.STUN, mob, duration, mob), true);
                    mob.addBuff(new ActiveBuff(AphBuffs.IMMORTAL, mob, duration, mob), true);
                }

                private void prepareSlimeThrow(T mob) {
                    int targets = (int) streamPossibleTargets(mob).count();
                    throwSlimesNumber = defaultThrowSlimesNumber + Math.min(targets - 1, 5);
                    if (anger >= 8) throwSlimesNumber += 2;
                    else if (anger >= 4) throwSlimesNumber += 1;

                    throwSlimesTimer = Math.min(throwSlimesNumber, 3) * 10;
                    throwSlimesCooldownTimer = defaultThrowSlimesCooldownDuration / 50 + throwSlimesTimer;

                    applyTemporaryBuffs(mob, throwSlimesTimer * 50 + 500);
                }
            });

            // Attack target
            this.addChild(new UnstableGelSlimeChasePlayerAI<>(1024 * 32, collisionAttackDamage, collisionAttackKnockback));
        }

        public void spawnMiniUnstableGelSlimes(T mob, int number) {
            for (int i = 0; i < number; i++) {
                MiniUnstableGelSlime summoned = (MiniUnstableGelSlime) MobRegistry.getMob("miniunstablegelslime", mob.getLevel());
                summoned.setInitialTP(true);
                mob.getLevel().entityManager.addMob(summoned, mob.randomPositionClose(mob.x), mob.randomPositionClose(mob.y));
            }
        }

        public void launchSlimeProjectile(T mob) {
            Mob target = getRandomCloseTarget(mob);
            if (target == null) {
                target = getRandomTarget(mob);
            }
            if (target != null) {
                mob.getLevel().entityManager.projectiles.add(new MiniUnstableGelSlimeProjectile(
                        mob.getLevel(), mob, mob.x, mob.y,
                        target.x, target.y,
                        120.0F, 500,
                        unstableGelSlimeProjectileDamage,
                        unstableGelSlimeProjectileKnockback
                ));
            }
        }

        public Point getTeleportPoint(T mob) {
            if (!mob.removed()) {
                Mob tpTarget = getRandomTarget(mob);
                if (tpTarget != null) {
                    float distance = 150 + 150 * mob.getHealthPercent();
                    float angle = (float) Math.random() * 360;
                    float xExtra = (float) (Math.cos(angle) * distance);
                    float yExtra = (float) (Math.sin(angle) * distance);

                    return new Point((int) (tpTarget.x + xExtra), (int) (tpTarget.y + yExtra));
                }
            }
            return null;
        }

        public Mob getRandomTarget(T mob) {
            ArrayList<Mob> list = new ArrayList<>();
            streamPossibleTargets(mob).forEach(list::add);
            return GameRandom.globalRandom.getOneOf(list);
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob) {
            return new TargetFinderDistance<>(1024 * 32).streamPlayersInRange(new Point((int) mob.x, (int) mob.y), mob).filter((m) -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map((m) -> m);
        }

        public Mob getRandomCloseTarget(T mob) {
            ArrayList<Mob> list = new ArrayList<>();
            streamPossibleCloseTargets(mob).forEach(list::add);
            if (list.isEmpty()) {
                streamPossibleTargets(mob).forEach(list::add);
            }
            return GameRandom.globalRandom.getOneOf(list);
        }

        public GameAreaStream<Mob> streamPossibleCloseTargets(T mob) {
            return new TargetFinderDistance<>(24 * 32).streamPlayersInRange(new Point((int) mob.x, (int) mob.y), mob).filter((m) -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map((m) -> m);
        }
    }

    public static class UnstableGelSlimeChasePlayerAI<T extends UnstableGelSlime> extends SequenceAINode<T> {
        CollisionPlayerChaserAI<T> collisionPlayerChaserAI;

        public UnstableGelSlimeChasePlayerAI(int searchDistance, GameDamage damage, int knockback) {
            this.addChild(new SucceederAINode<>(new LooseTargetTimerAINode<>()));
            this.addChild(new TargetFinderAINode<T>(searchDistance) {
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return distance.streamPlayersInRange(base, mob).filter((m) -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map((m) -> m);
                }
            });
            this.addChild(collisionPlayerChaserAI = new CollisionPlayerChaserAI<T>(searchDistance, damage, knockback) {
                public boolean attackTarget(T mob, Mob target) {
                    return UnstableGelSlimeChasePlayerAI.this.attackTarget(mob, target);
                }
            });
        }

        public boolean attackTarget(T mob, Mob target) {
            return CollisionChaserAINode.simpleAttack(mob, target, this.collisionPlayerChaserAI.damage, this.collisionPlayerChaserAI.knockback);
        }
    }
}


