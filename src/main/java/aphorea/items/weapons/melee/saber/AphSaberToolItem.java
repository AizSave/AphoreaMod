package aphorea.items.weapons.melee.saber;

import aphorea.utils.customchargeattacks.AphChargeAttackToolItem;
import aphorea.utils.customchargeattacks.AphCustomChargeAttackHandler;
import aphorea.utils.customchargeattacks.AphCustomChargeLevel;
import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;


abstract public class AphSaberToolItem extends AphChargeAttackToolItem implements ItemInteractAction {

    public IntUpgradeValue dashRange;
    boolean isCharging;

    public AphSaberToolItem(int enchantCost, SaberChargeLevel... chargeLevels) {
        super(enchantCost, (AphCustomChargeLevel[]) chargeLevels);

        this.enchantCost.setUpgradedValue(1.0F, 500);

        this.dashRange = new IntUpgradeValue(200, 0.0F);
        this.dashRange.setBaseValue(200);

        this.attackRange.setBaseValue(60);

        this.keyWords.add("saber");
    }

    public static SaberChargeLevel[] getChargeLevels() {
        return new SaberChargeLevel[]{
                new SaberChargeLevel(80, 1.0F, new Color(255, 255, 255)),
                new SaberChargeLevel(90, 1.25F, new Color(255, 255, 0)),
                new SaberChargeLevel(100, 1.5F, new Color(255, 128, 0)),
                new SaberChargeLevel(120, 1.75F, new Color(255, 0, 0)),
                new SaberChargeLevel(120, 2F, new Color(47, 0, 0)),
                new SaberChargeLevel(120, 1.75F, new Color(255, 0, 0)),
                new SaberChargeLevel(100, 1.5F, new Color(255, 128, 0)),
                new SaberChargeLevel(90, 1.25F, new Color(255, 255, 0)),
                new SaberChargeLevel(80, 1.0F, new Color(255, 255, 255))
        };
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "saber"));
        tooltips.add(Localization.translate("itemtooltip", "saber2"));
        tooltips.add(Localization.translate("itemtooltip", "saber3"));
        return tooltips;
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        AphCustomChargeLevel[] charge = this.chargeLevels;
        player.startAttackHandler(new SaberAttackHandler(player, slot, item, this, seed, x, y, charge));

        return item;
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "saber");
    }

    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.isRiding() && !player.isAttacking && !this.isCharging && !player.buffManager.hasBuff(AphBuffs.SABER_DASH_COOLDOWN);
    }

    public int getLevelInteractCooldownTime(InventoryItem item, PlayerMob player) {
        return 0;
    }

    public boolean getConstantInteract(InventoryItem item) {
        return true;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        int animTime = (int) ((float) this.getAttackAnimTime(item, player));
        player.startAttackHandler((new SaberDashAttackHandler(player, slot, item, this, animTime, new Color(190, 220, 220), seed)).startFromInteract());
        return item;
    }

    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y) {
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    public class SaberAttackHandler extends AphCustomChargeAttackHandler<AphSaberToolItem> {

        public SaberAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, AphSaberToolItem toolItem, int seed, int startX, int startY, AphCustomChargeLevel<AphSaberToolItem>[] chargeLevels) {
            super(player, slot, item, toolItem, seed, startX, startY, chargeLevels);
        }

        @Override
        public float getChargePercent() {
            int chargeTime = this.timeSpentUpToCurrentChargeLevel + Math.round((float) this.chargeTimeRemaining * (1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player)));
            float linearPercent = (float) Math.min(this.getTimeSinceStart(), chargeTime) / (float) chargeTime;
            return -4 * (float) Math.pow(linearPercent - 0.5f, 2) + 1;
        }

        @Override
        public void drawWeaponParticles(InventoryItem showItem, Color color) {
            float chargePercent = showItem.getGndData().getFloat("chargePercent");
            float angle = this.toolItem.getSwingRotation(showItem, this.player.getDir(), chargePercent);
            int attackDir = this.player.getDir();
            int offsetX = 0;
            int offsetY = 0;
            if (attackDir == 0) {
                angle = -angle - 90.0F;
                offsetY = -8;
            } else if (attackDir == 1) {
                angle = -angle + 180.0F + 45.0F;
                offsetX = 8;
            } else if (attackDir == 2) {
                angle = -angle + 90.0F;
                offsetY = 12;
            } else {
                angle = angle + 90.0F + 45.0F;
                offsetX = -8;
            }

            float dx = GameMath.sin(angle);
            float dy = GameMath.cos(angle);
            int range = GameRandom.globalRandom.getIntBetween(0, (int) (this.toolItem.getAttackRange(this.item) * 0.1));
            this.player.getLevel().entityManager.addParticle(this.player.x + (float) offsetX + dx * (float) range + GameRandom.globalRandom.floatGaussian() * 3.0F, this.player.y + 4.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.player.dx, this.player.dy).color(color).height(20.0F - dy * (float) range - (float) offsetY);
        }

        @Override
        public void drawParticleExplosion(int particleCount, Color color, int minForce, int maxForce) {
            particleCount = particleCount / 10;

            ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
            float anglePerParticle = 360.0F / (float) particleCount;

            for (int i = 0; i < particleCount; ++i) {
                int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(minForce, maxForce);
                float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(minForce, maxForce) * 0.8F;
                this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(color).heightMoves(0.0F, 30.0F).lifeTime(500);
            }

        }

        @Override
        public void onEndAttack(boolean bySelf) {
            super.onEndAttack(bySelf);
            if (!this.endedByInteract) {
                if (this.currentChargeLevel == 4) {
                    Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                    launchSaberProjectile(dir, 0);
                } else if ((this.currentChargeLevel == 3 || this.currentChargeLevel == 5)) {
                    Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                    launchSaberProjectile(dir, 1);
                }
            }
        }

        private void launchSaberProjectile(Point2D.Float dir, int t) {
            float velocity = t == 0 ? 300.0F : 200.0F;
            int distanceExtra = t == 0 ? 7 : 3;
            GameDamage originalDamage = this.toolItem.getAttackDamage(this.item);
            GameDamage damage = t == 0 ? originalDamage : originalDamage.modDamage(0.5F);
            float finalVelocity = (float) Math.round(this.toolItem.getEnchantment(this.item).applyModifierLimited(ToolItemModifiers.VELOCITY, ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * velocity * (Float) this.player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
            Projectile projectile = getProjectile(this.player.getLevel(), this.player, this.player.x, this.player.y, this.player.x + dir.x * 100.0F, this.player.y + dir.y * 100.0F, finalVelocity, (int) ((float) this.toolItem.getAttackRange(this.item)) * distanceExtra, damage, 0);
            if (projectile != null) {
                GameRandom random = new GameRandom(this.seed);
                projectile.resetUniqueID(random);
                this.player.getLevel().entityManager.projectiles.addHidden(projectile);
                if (this.player.isServer()) {
                    this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), this.player, this.player.getServerClient());
                }
            }

        }
    }

    abstract public Projectile getProjectile(Level level, PlayerMob player, float x, float y, float targetX, float targetY, float finalVelocity, int distance, GameDamage damage, int knockback);

    public static class SaberChargeLevel extends AphCustomChargeLevel<AphSaberToolItem> {
        public SaberChargeLevel(int timeToCharge, float damageModifier, Color color) {
            super(timeToCharge, damageModifier, color);
        }

        @Override
        public void onReachedLevel(AphCustomChargeAttackHandler<AphSaberToolItem> attackHandler) {
            if (attackHandler.player.isClient()) {
                if (this.particleColors != null) {
                    attackHandler.drawParticleExplosion(30, this.particleColors, 30, 50);
                }

                int totalLevels = attackHandler.chargeLevels.length;

                float currentLevelPercent = (float) (attackHandler.currentChargeLevel + 1) / (float) totalLevels;
                float minPitch = Math.max(0.7F, 1.0F - (float) totalLevels * 0.1F);
                float pitch = GameMath.lerp(currentLevelPercent, 1.0F, minPitch);
                SoundManager.playSound(GameResources.cling, SoundEffect.effect(attackHandler.player).volume(attackHandler.currentChargeLevel % 4 == 0 ? 0.5F : 0.1F).pitch(pitch));

            }

        }
    }

    public static class SaberDashAttackHandler extends MousePositionAttackHandler {
        public int chargeTime;
        public boolean fullyCharged;
        public AphSaberToolItem saberItem;
        public long startTime;
        public InventoryItem item;
        public int seed;
        public Color particleColors;
        public boolean endedByInteract;
        protected HudDrawElement hudDrawElement;

        public SaberDashAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, AphSaberToolItem saberItem, int chargeTime, Color particleColors, int seed) {
            super(player, slot, 20);
            this.item = item;
            this.saberItem = saberItem;
            this.chargeTime = chargeTime;
            this.particleColors = particleColors;
            this.seed = seed;
            this.startTime = player.getWorldEntity().getLocalTime();
        }

        public long getTimeSinceStart() {
            return this.player.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return (float) this.getTimeSinceStart() / (float) this.chargeTime;
        }

        public void onUpdate() {
            super.onUpdate();
            if (this.player.isClient() && this.hudDrawElement == null) {
                this.hudDrawElement = this.player.getLevel().hudManager.addElement(new HudDrawElement() {
                    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                        if (SaberDashAttackHandler.this.player.getAttackHandler() != SaberDashAttackHandler.this) {
                            this.remove();
                        } else {
                            float distance = SaberDashAttackHandler.this.getChargeDistance(SaberDashAttackHandler.this.getChargePercent());
                            if (distance > 0.0F) {
                                Point2D.Float dir = GameMath.normalize((float) SaberDashAttackHandler.this.lastX - SaberDashAttackHandler.this.player.x, (float) SaberDashAttackHandler.this.lastY - SaberDashAttackHandler.this.player.y);
                                final DrawOptions drawOptions = HUD.getArrowHitboxIndicator(SaberDashAttackHandler.this.player.x, SaberDashAttackHandler.this.player.y, dir.x, dir.y, (int) distance, 50, new Color(0, 0, 0, 0), new Color(220, 255, 255, 100), new Color(0, 0, 0, 100), camera);
                                list.add(new SortedDrawable() {
                                    public int getPriority() {
                                        return 1000;
                                    }

                                    public void draw(TickManager tickManager) {
                                        drawOptions.draw();
                                    }
                                });
                            }

                        }
                    }
                });
            }

            float chargePercent = this.getChargePercent();
            InventoryItem showItem = this.item.copy();
            showItem.getGndData().setFloat("chargePercent", chargePercent);
            showItem.getGndData().setBoolean("chargeUp", true);
            Packet attackContent = new Packet();
            this.player.showAttack(showItem, this.lastX, this.lastY, this.seed, attackContent);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, showItem, this.lastX, this.lastY, this.seed, attackContent), this.player, client);
            }

            if (chargePercent >= 1.0F && !this.fullyCharged) {
                this.fullyCharged = true;
                if (this.player.isClient()) {
                    int particles = 35;
                    float anglePerParticle = 360.0F / (float) particles;
                    ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

                    for (int i = 0; i < particles; ++i) {
                        int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                        this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(this.particleColors).heightMoves(0.0F, 30.0F).lifeTime(500);
                    }

                    SoundManager.playSound(GameResources.magicbolt4, SoundEffect.effect(this.player).volume(0.1F).pitch(2.5F));
                }
            }

        }

        public void onMouseInteracted(int levelX, int levelY) {
            this.endedByInteract = true;
            this.player.endAttackHandler(false);
        }

        public void onControllerInteracted(float aimX, float aimY) {
            this.endedByInteract = true;
            this.player.endAttackHandler(false);
        }

        public void onEndAttack(boolean bySelf) {
            float chargePercent = this.getChargePercent();
            if (!this.endedByInteract && chargePercent >= 0.5F) {
                this.player.constantAttack = true;
                InventoryItem attackItem = this.item.copy();
                attackItem.getGndData().setBoolean("sliceDash", true);
                attackItem.getGndData().setFloat("chargePercent", chargePercent);
                Packet attackContent = new Packet();
                this.player.showAttack(attackItem, this.lastX, this.lastY, this.seed, attackContent);
                if (this.player.isServer()) {
                    ServerClient client = this.player.getServerClient();
                    this.player.getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, attackItem, this.lastX, this.lastY, this.seed, attackContent), this.player, client);
                }

                Point2D.Float dir = GameMath.normalize((float) this.lastX - this.player.x, (float) this.lastY - this.player.y);
                chargePercent = Math.min(chargePercent, 1.0F);
                SaberDashLevelEvent event = new SaberDashLevelEvent(this.player, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int) (200.0F * chargePercent), this.saberItem.getAttackDamage(this.item), 0);
                this.player.getLevel().entityManager.addLevelEventHidden(event);
                if (this.player.isServer()) {
                    ServerClient serverClient = this.player.getServerClient();
                    this.player.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(event), event, serverClient);
                }

                ActiveBuff buff = new ActiveBuff(AphBuffs.SABER_DASH_COOLDOWN, this.player, 5.0F, null);
                this.player.addBuff(buff, false);
            }

            if (this.hudDrawElement != null) {
                this.hudDrawElement.remove();
            }

        }

        public float getChargeDistance(float chargePercent) {
            chargePercent = Math.min(chargePercent, 1.0F);
            return chargePercent > 0.5F ? (chargePercent - 0.5F) * 2.0F * (float) this.saberItem.dashRange.getValue(this.saberItem.getUpgradeTier(this.item)) : 0.0F;
        }
    }

    public static class SaberDashLevelEvent extends MobDashLevelEvent {
        private int maxStacks = 10;
        private boolean addedStack = false;

        public SaberDashLevelEvent() {
        }

        public SaberDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage, int maxStacks) {
            super(owner, seed, dirX, dirY, distance, animTime, damage);
            this.maxStacks = maxStacks;
        }

        public void setupSpawnPacket(PacketWriter writer) {
            super.setupSpawnPacket(writer);
            writer.putNextShortUnsigned(this.maxStacks);
            writer.putNextBoolean(this.addedStack);
        }

        public void applySpawnPacket(PacketReader reader) {
            super.applySpawnPacket(reader);
            this.maxStacks = reader.getNextShortUnsigned();
            this.addedStack = reader.getNextBoolean();
        }

        public void init() {
            super.init();
            if (this.level != null && this.level.isClient() && this.owner != null) {
                float forceMod = Math.min((float) this.animTime / 700.0F, 1.0F);
                float forceX = this.dirX * this.distance * forceMod;
                float forceY = this.dirY * this.distance * forceMod;

                for (int i = 0; i < 30; ++i) {
                    this.level.entityManager.addParticle(this.owner.x + (float) GameRandom.globalRandom.nextGaussian() * 15.0F + forceX / 5.0F, this.owner.y + (float) GameRandom.globalRandom.nextGaussian() * 20.0F + forceY / 5.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 5.0F, forceY * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 5.0F).color(new Color(200, 200, 220)).height(18.0F).lifeTime(700);
                }

                SoundManager.playSound(GameResources.swoosh, SoundEffect.effect(this.owner).volume(0.2F).pitch(2.5F));
            }

            if (this.owner != null) {
                ActiveBuff buff = new ActiveBuff(AphBuffs.SABER_DASH_ACTIVE, this.owner, this.animTime, null);
                this.owner.addBuff(buff, false);
            }

        }

        public Shape getHitBox() {
            Point2D.Float dir;
            if (this.owner.getDir() == 3) {
                dir = GameMath.getPerpendicularDir(-this.dirX, -this.dirY);
            } else {
                dir = GameMath.getPerpendicularDir(this.dirX, this.dirY);
            }

            float width = 40.0F;
            float frontOffset = 20.0F;
            float range = 80.0F;
            float rangeOffset = -40.0F;
            return new LineHitbox(this.owner.x + dir.x * rangeOffset + this.dirX * frontOffset, this.owner.y + dir.y * rangeOffset + this.dirY * frontOffset, dir.x, dir.y, range, width);
        }
    }

}
