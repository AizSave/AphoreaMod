package aphorea.items.tools.weapons.range.bow;

import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.SpinelArrowProjectile;
import aphorea.utils.AphColors;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;

public class SpinelCrossbow extends BowProjectileToolItem implements ItemInteractAction {
    public GameTexture arrowlessAttackTexture;

    public SpinelCrossbow() {
        super(1300);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(800);
        this.attackDamage.setBaseValue(65.0F).setUpgradedValue(1.0F, 120.0F);
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(200);
        this.attackXOffset = 12;
        this.attackYOffset = 10;
    }

    @Override
    protected void loadAttackTexture() {
        super.loadAttackTexture();
        try {
            this.arrowlessAttackTexture = GameTexture.fromFileRaw("player/weapons/" + this.getStringID() + "_arrowless");
        } catch (FileNotFoundException var2) {
            this.arrowlessAttackTexture = null;
        }
    }

    public GameSprite getArrowlessAttackSprite(InventoryItem item, PlayerMob player) {
        return this.arrowlessAttackTexture != null ? new GameSprite(this.arrowlessAttackTexture) : new GameSprite(this.getItemSprite(item, player), 24);
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return item.getGndData().getBoolean("charging") ? super.getAttackSprite(item, player) : this.getArrowlessAttackSprite(item, player);
    }

    public Item getArrowItem(Level level, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        return ItemRegistry.getItem("stonearrow");
    }

    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        Item arrow = this.getArrowItem(level, attackerMob, seed, item);
        map.setShortUnsigned("arrowID", arrow == null ? '\uffff' : arrow.getID());
    }

    @Override
    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{
                ItemRegistry.getItem("stonearrow")
        }, "arrowammo");
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        item.getGndData().setBoolean("charging", false);
        int arrowID = mapContent.getShortUnsigned("arrowID", 65535);
        if (arrowID != 65535) {
            Item arrow = ItemRegistry.getItem(arrowID);
            if (arrow != null && arrow.type == Type.ARROW) {
                GameRandom random = new GameRandom(seed + 5);
                float ammoConsumeChance = ((ArrowItem) arrow).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                boolean dropItem;
                boolean shouldFire;
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob) attackerMob).removeAmmo(arrow, 1, "arrowammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }

                if (shouldFire) {
                    this.fireProjectiles(level, x, y, attackerMob, item, seed, (ArrowItem) arrow, dropItem, mapContent);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (arrow == null ? arrowID : arrow.getStringID()) + " as arrow.");
            }
        }

        return item;
    }

    public void tripleAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, GNDItemMap mapContent) {
        int arrowID = mapContent.getShortUnsigned("arrowID", 65535);
        if (arrowID != 65535) {
            Item arrow = ItemRegistry.getItem(arrowID);
            if (arrow != null && arrow.type == Type.ARROW) {
                GameRandom random = new GameRandom(seed + 5);
                float ammoConsumeChance = ((ArrowItem) arrow).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                boolean dropItem;
                boolean shouldFire;
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob) attackerMob).removeAmmo(arrow, 1, "arrowammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }

                if (shouldFire) {
                    float ax = attackerMob.x, ay = attackerMob.y;
                    float dx = x - ax, dy = y - ay;
                    double angle = Math.atan2(dy, dx), dist = Math.hypot(dx, dy);

                    for (int offset : new int[]{-10, 0, 10}) {
                        double a = angle + Math.toRadians(offset);
                        int tx = (int) (ax + Math.cos(a) * dist);
                        int ty = (int) (ay + Math.sin(a) * dist);
                        fireProjectiles(level, tx, ty, attackerMob, item, seed, (ArrowItem) arrow, dropItem, mapContent);
                    }

                    attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
                    attackerMob.buffManager.forceUpdateBuffs();

                    if (attackerMob.isServer()) {
                        int strength = 50;
                        Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
                        level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, -dir.x, -dir.y, (float) strength), level);
                    }
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (arrow == null ? arrowID : arrow.getStringID()) + " as arrow.");
            }
        }

    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return getProjectile(level, x, y, owner, item);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        return getProjectile(level, x, y, attackerMob, item);
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new SpinelArrowProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, getProjectileVelocity(item, attackerMob), getAttackRange(item), getAttackDamage(item), getKnockback(item, attackerMob));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "spinelcrossbow"));
        tooltips.add(Localization.translate("itemtooltip", "spinelcrossbow2"));
        return tooltips;
    }

    // INTERACTION

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return canAttack(level, x, y, attackerMob, item) == null && (!attackerMob.isPlayer || getAvailableAmmo((PlayerMob) attackerMob) > 0);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob) * 2;
        attackerMob.startAttackHandler(new SpinelCrossbowInteractionAttackHandler(attackerMob, slot, item, this, animTime, seed).startFromInteract());
        return item;
    }

    public static class SpinelCrossbowInteractionAttackHandler extends MousePositionAttackHandler {
        public int chargeTime;
        public boolean fullyCharged;
        public SpinelCrossbow toolItem;
        public long startTime;
        public InventoryItem item;
        public int seed;
        public boolean endedByInteract;
        protected int endAttackBuffer;


        public SpinelCrossbowInteractionAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, SpinelCrossbow toolItem, int chargeTime, int seed) {
            super(attackerMob, slot, 20);
            this.item = item;
            this.toolItem = toolItem;
            this.chargeTime = chargeTime;
            this.seed = seed;
            this.startTime = attackerMob.getWorldEntity().getLocalTime();
        }

        public long getTimeSinceStart() {
            return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return (float) this.getTimeSinceStart() / this.chargeTime;
        }

        public Point getNextItemAttackerLevelPos(Mob currentTarget) {
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("skillPercent", 1.0F);
            return ((ItemAttackerWeaponItem) attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, attackItem);
        }

        public void onUpdate() {
            super.onUpdate();
            Point2D.Float dir = GameMath.normalize((float) this.lastX - this.attackerMob.x, (float) this.lastY - this.attackerMob.y);
            float chargePercent = this.getChargePercent();
            InventoryItem showItem = this.item.copy();
            showItem.getGndData().setBoolean("charging", true);
            showItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);

            if (chargePercent >= 1.0F) {
                if (!this.attackerMob.isPlayer) {
                    this.endAttackBuffer += this.updateInterval;
                    if (this.endAttackBuffer >= 350) {
                        this.endAttackBuffer = 0;
                        this.attackerMob.endAttackHandler(true);
                        return;
                    }
                }

                if (this.attackerMob.isClient()) {
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + dir.x * 16.0F + (float) (GameRandom.globalRandom.nextGaussian() * 6.0), this.attackerMob.y + 4.0F + (float) (GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx / 10.0F, this.attackerMob.dy / 10.0F).color(AphColors.spinel).height(20.0F - dir.y * 16.0F);
                }

                if (!this.fullyCharged) {
                    this.fullyCharged = true;
                    if (this.attackerMob.isClient()) {
                        int particles = 35;
                        float anglePerParticle = 360.0F / (float) particles;
                        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

                        for (int i = 0; i < particles; ++i) {
                            int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                            float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                            float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                            this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(AphColors.spinel).heightMoves(0.0F, 30.0F).lifeTime(500);
                        }

                        SoundManager.playSound(GameResources.tick, SoundEffect.effect(this.attackerMob).volume(0.1F).pitch(2.5F));
                    }
                }
            }
        }

        public void onMouseInteracted(int levelX, int levelY) {
            this.endedByInteract = true;
            this.attackerMob.endAttackHandler(false);
        }

        public void onControllerInteracted(float aimX, float aimY) {
            this.endedByInteract = true;
            this.attackerMob.endAttackHandler(false);
        }

        public void onEndAttack(boolean bySelf) {
            float chargePercent = this.getChargePercent();
            if (!this.endedByInteract && chargePercent >= 1F) {
                if (this.attackerMob.isPlayer) {
                    ((PlayerMob) this.attackerMob).constantAttack = true;
                }

                InventoryItem attackItem = this.item.copy();
                attackItem.getGndData().setFloat("chargePercent", chargePercent);
                attackItem.getGndData().setBoolean("charged", true);
                if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                    Point attackPos = ((ItemAttackerWeaponItem) attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                    this.lastX = attackPos.x;
                    this.lastY = attackPos.y;
                }

                if (this.attackerMob.isClient()) {
                    SoundManager.playSound(GameResources.run, SoundEffect.effect(this.attackerMob));
                }

                GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
                this.toolItem.tripleAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, attackItem, this.seed, attackMap);
            }

            this.attackerMob.doAndSendStopAttackAttacker(false);
        }
    }

}
