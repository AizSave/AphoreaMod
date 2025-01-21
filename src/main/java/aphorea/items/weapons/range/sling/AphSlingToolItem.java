package aphorea.items.weapons.range.sling;

import aphorea.utils.AphColors;
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
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;

abstract public class AphSlingToolItem extends ProjectileToolItem {

    public String projectileID;

    abstract public Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item);

    public AphSlingToolItem(int enchantCost, String projectileID) {
        super(enchantCost);
        this.setItemCategory("equipment", "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "rangedweapons");
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.attackRange.setBaseValue(500);
        this.enchantCost.setUpgradedValue(1.0F, 2100);

        this.projectileID = projectileID;

        this.keyWords.add("sling");
    }

    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int ammoAmount = this.getAvailableAmmo(perspective);
            if (ammoAmount > 999) {
                ammoAmount = 999;
            }

            String amountString = String.valueOf(ammoAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString((float) (x + 28 - width), (float) (y + 16), amountString, tipFontOptions);
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(0, 0, -item.getGndData().getFloat("showAngle"));
    }

    public int getAvailableAmmo(PlayerMob player) {
        return player == null ? 0 : player.getInv().main.getAmount(player.getLevel(), player, ItemRegistry.getItem("stone"), "stone");
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        this.addAmmoTooltips(tooltips);
        int ammoAmount = this.getAvailableAmmo(perspective);
        tooltips.add(Localization.translate("itemtooltip", "ammotip", "value", ammoAmount));
        tooltips.add(Localization.translate("itemtooltip", "sling2"));
        return tooltips;
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "sling"));
    }

    public String canAttack(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return this.getAvailableAmmo(player) > 0 ? null : "";
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.startAttackHandler(new SlingAttackHandler(player, slot, item, this, seed, x, y));
        return item;
    }

    public void doAttack(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed) {

        Item stone = ItemRegistry.getItem("stone");

        if (player.getInv().main.removeItems(level, player, stone, 1, "stone") >= 1) {

            Projectile projectile = this.getProjectile(level, x, y, player, item);
            GameRandom random = new GameRandom(seed);
            projectile.resetUniqueID(random);

            level.entityManager.projectiles.addHidden(projectile);

            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
            }
        }
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "sling");
    }

    static public class SlingAttackHandler extends MouseAngleAttackHandler {
        private final long startTime;
        public AphSlingToolItem toolItem;
        public InventoryItem item;
        private final int seed;
        private boolean charged;

        private float angle_v;
        private float angle;

        public void restartAngle() {
            angle_v = 4;
            angle = 0;
        }

        public void moveAngle() {
            angle += angle_v;
            if (angle >= 360) {
                angle = angle - 360;
            }
        }

        public void increaseAngleM() {
            if (angle_v < 22.5F) {
                angle_v += 0.2F;
                if (angle_v > 22.5F) {
                    angle_v = 22.5F;
                }
            }
        }


        public SlingAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, AphSlingToolItem toolItem, int seed, int startTargetX, int startTargetY) {
            super(player, slot, 20, 1000.0F, startTargetX, startTargetY);
            this.item = item;
            this.toolItem = toolItem;
            this.seed = seed;
            this.startTime = player.getWorldEntity().getLocalTime();

            restartAngle();
        }

        public long getTimeSinceStart() {
            return this.player.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return Math.min((float) this.getTimeSinceStart() / this.getChargeTime(), 1.0F);
        }

        public float getChargeTime() {
            float multiplier = 1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player);
            return (float) ((int) (multiplier * 1000.0F));
        }

        public void onUpdate() {
            super.onUpdate();

            this.moveAngle();
            this.increaseAngleM();

            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.player.getX() + (int) (dir.x * 100.0F);
            int attackY = this.player.getY() + (int) (dir.y * 100.0F);
            if (this.toolItem.canAttack(this.player.getLevel(), attackX, attackY, this.player, this.item) == null) {
                Packet attackContent = new Packet();
                this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
                InventoryItem showItem = this.item.copy();
                showItem.getGndData().setFloat("showAngle", angle);

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
                        this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(AphColors.leather).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
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
                this.toolItem.doAttack(this.player.getLevel(), attackX, attackY, this.player, this.item, this.seed);
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
