package aphorea.items.weapons.melee.dagger;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketFireDeathRipper;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public abstract class AphDaggerToolItem extends SpearToolItem implements ItemInteractAction {

    public AphDaggerToolItem(int enchantCost) {
        super(enchantCost);
        this.keyWords.add("dagger");
        this.keyWords.remove("spear");
        this.width = 8.0F;
        this.attackXOffset = 12;
        this.attackYOffset = 2;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.buffManager.addBuff(new ActiveBuff(AphBuffs.DAGGER_ATTACK, player, this.getAttackAnimTime(item, player), null), false);
        return super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
    }

    @Override
    public InventoryItem onSettlerAttack(Level level, HumanMob mob, Mob target, int attackHeight, int seed, InventoryItem item) {
        mob.buffManager.addBuff(new ActiveBuff(AphBuffs.DAGGER_ATTACK, mob, this.getAttackAnimTime(item, mob), null), false);
        return super.onSettlerAttack(level, mob, target, attackHeight, seed, item);
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.addAll(this.getDisplayNameTooltips(item, perspective, blackboard));
        tooltips.addAll(this.getDebugTooltips(item, perspective, blackboard));
        tooltips.addAll(this.getCraftingMatTooltips(item, perspective, blackboard));
        return tooltips;
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);

        float animation;
        if (item.getGndData().getBoolean("isCharging")) {
            animation = item.getGndData().getFloat("chargePercent") / 2 + 0.5F;
        } else {
            animation = attackProgress;
            if (attackProgress < 0.25) {
                animation += 0.25F;
            } else if (attackProgress < 0.5) {
                animation += 0.5F;
            } else if (attackProgress < 0.75) {
                animation -= 0.25F;
            }
        }
        drawOptions.thrustOffsets(attackDirX, attackDirY, animation);
    }

    @Override
    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
        if (!item.getGndData().getBoolean("isCharging")) {
            super.showAttack(level, x, y, mob, attackHeight, item, seed, contentReader);
        }
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "dagger");
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.isRiding() && !player.isAttacking && !player.buffManager.hasBuff(AphBuffs.SPIN_ATTACK_COOLDOWN);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        player.startAttackHandler((new DaggerSecondaryAttackHandler(player, slot, item, this, seed, x, y, getAttackAnimTime(item, player) / 2)).startFromInteract());
        return item;
    }

    public void doSecondaryAttack(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed) {
        if (item != null) {
            InventorySlot slot = player.getInv().streamInventorySlots(false, false, false, false).filter(
                    inventorySlot -> inventorySlot.getItem() == item
            ).findFirst().orElse(null);
            if (slot != null) {
                boolean throwItem = !getEnchantment(item).getModifier(AphModifiers.LOYAL);

                if (slot.isItemLocked() && throwItem) {
                    player.getServerClient().sendChatMessage(Localization.translate("message", "cannottrhowlockeditem"));
                } else if (level.isServer()) {
                    Projectile projectile = this.getProjectile(level, x, y, player, item, throwItem);
                    GameRandom random = new GameRandom(seed);
                    projectile.resetUniqueID(random);
                    level.entityManager.projectiles.addHidden(projectile);

                    level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));

                    if (throwItem) {
                        slot.clearSlot();
                    }
                }
            }
        }
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        Set<Integer> enchantments = new HashSet<>(super.getValidEnchantmentIDs(item));
        enchantments.addAll(AphEnchantments.daggerItemEnchantments);
        return enchantments;
    }

    public abstract Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, boolean shouldDrop);

    public abstract Color getSecondaryAttackColor();

    static public class DaggerSecondaryAttackHandler extends MouseAngleAttackHandler {
        private final long startTime;
        public AphDaggerToolItem toolItem;
        public InventoryItem item;
        private final int seed;
        private boolean charged;

        public final int chargeTime;

        public DaggerSecondaryAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, AphDaggerToolItem toolItem, int seed, int startTargetX, int startTargetY, int chargeTime) {
            super(player, slot, 20, 1000, startTargetX, startTargetY);
            this.item = item;
            this.toolItem = toolItem;
            this.seed = seed;
            this.startTime = player.getWorldEntity().getLocalTime();

            this.chargeTime = chargeTime;
        }

        public long getTimeSinceStart() {
            return this.player.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return Math.min((float) this.getTimeSinceStart() / this.getChargeTime(), 1.0F);
        }

        public float getChargeTime() {
            return chargeTime;
        }

        public void onUpdate() {
            super.onUpdate();

            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.player.getX() + (int) (dir.x * 100.0F);
            int attackY = this.player.getY() + (int) (dir.y * 100.0F);
            if (this.toolItem.canAttack(this.player.getLevel(), attackX, attackY, this.player, this.item) == null) {
                Packet attackContent = new Packet();
                this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
                InventoryItem showItem = this.item.copy();
                showItem.getGndData().setBoolean("isCharging", true);
                showItem.getGndData().setFloat("chargePercent", getChargePercent());

                this.player.showAttack(showItem, attackX, attackY, this.seed, attackContent);
                if (this.player.isServer()) {
                    ServerClient client = this.player.getServerClient();
                    this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, showItem, attackX, attackY, this.seed, attackContent), this.player, client);
                } else if (this.getChargePercent() >= 1.0F && !this.charged) {
                    this.charged = true;
                    SoundManager.playSound(GameResources.tick, SoundEffect.effect(this.player).volume(1.0F).pitch(1.0F));
                    ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
                    float anglePerParticle = 18.0F;

                    for (int i = 0; i < 20; ++i) {
                        int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * 50.0F;
                        float dy = (float) Math.cos(Math.toRadians(angle)) * 50.0F * 0.8F;
                        this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(toolItem.getSecondaryAttackColor()).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                    }
                }
            }

        }

        public void onEndAttack(boolean bySelf) {
            if (this.getChargePercent() >= 1.0F) {
                Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                int attackX = this.player.getX() + (int) (dir.x * 100.0F);
                int attackY = this.player.getY() + (int) (dir.y * 100.0F);
                Packet attackContent = new Packet();
                this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
                this.toolItem.doSecondaryAttack(this.player.getLevel(), attackX, attackY, this.player, this.item, this.seed);
                if (this.player.isClient()) {
                    SoundManager.playSound(GameResources.run, SoundEffect.effect(this.player));
                } else if (this.player.isServer()) {
                    ServerClient client = this.player.getServerClient();
                    Server server = this.player.getLevel().getServer();
                    server.network.sendToClientsWithEntityExcept(new PacketFireDeathRipper(client.slot), this.player, client);
                }
            }

            this.player.stopAttack(false);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketPlayerStopAttack(client.slot), this.player, client);
            }

        }
    }

}
