package aphorea.items.weapons.magic;

import aphorea.other.area.AphArea;
import aphorea.other.area.AphAreaList;
import aphorea.other.itemtype.weapons.Secondary.AphMagicProjectileSecondaryAreaToolItem;
import aphorea.projectiles.toolitem.UnstableGelProjectile;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

import java.awt.*;

public class UnstableGelStaff extends AphMagicProjectileSecondaryAreaToolItem implements ItemInteractAction {

    static AphAreaList areaList = new AphAreaList(
            new AphArea(200, new Color(191, 60, 255)).setDamageArea(20, 90).setArmorPen(10)
    ).setDamageType(DamageTypeRegistry.MAGIC);

    public UnstableGelStaff() {
        super(500, areaList, 800, 6.0F);
        rarity = Rarity.UNCOMMON;
        attackAnimTime.setBaseValue(800);
        attackDamage.setBaseValue(30).setUpgradedValue(1, 100);
        velocity.setBaseValue(100);
        knockback.setBaseValue(0);
        attackRange.setBaseValue(600);

        manaCost.setBaseValue(3.0F);

        attackXOffset = 12;
        attackYOffset = 22;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "unstablegelstaff"));
        tooltips.add(Localization.translate("itemtooltip", "stikybuff2"));
        tooltips.add(Localization.translate("itemtooltip", "areasecondaryattack", "mana", getSecondaryManaCost(item)));
        areaList.addAreasToolTip(tooltips, perspective, true, null, null);
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.slimesplash, SoundEffect.effect(mob)
                    .volume(0.7f)
                    .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {

        Projectile projectile = new UnstableGelProjectile(
                level, player,
                player.x, player.y,
                x, y,
                getProjectileVelocity(item, player),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, player), 0, seed
        );
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);

        level.entityManager.projectiles.addHidden(projectile);

        if (level.isServer()) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
        }

        this.consumeMana(player, item);

        return item;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        ActiveBuff buff = new ActiveBuff(AphBuffs.STICKY, target, 2000, event.owner);
        target.addBuff(buff, true);
    }

    @Override
    public Packet getPacket(int slot, float rangeModifier) {
        return new UnstableGelStaffAreaParticlesPacket(slot, rangeModifier);
    }

    @Override
    public void usePacket(Level level, PlayerMob player, float rangeModifier) {
        UnstableGelStaffAreaParticlesPacket.applyToPlayer(level, player, rangeModifier);
    }

    public static class UnstableGelStaffAreaParticlesPacket extends Packet {
        public final int slot;
        public final float rangeModifier;

        public UnstableGelStaffAreaParticlesPacket(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            this.slot = reader.getNextByteUnsigned();
            this.rangeModifier = reader.getNextFloat();
        }

        public UnstableGelStaffAreaParticlesPacket(int slot, float rangeModifier) {
            this.slot = slot;
            this.rangeModifier = rangeModifier;
            PacketWriter writer = new PacketWriter(this);
            writer.putNextByteUnsigned(slot);
            writer.putNextFloat(rangeModifier);
        }

        public void processClient(NetworkPacket packet, Client client) {
            if (client.getLevel() != null) {
                ClientClient target = client.getClient(this.slot);
                if (target != null && target.isSamePlace(client.getLevel())) {
                    applyToPlayer(target.playerMob.getLevel(), target.playerMob, rangeModifier);
                } else {
                    client.network.sendPacket(new PacketRequestPlayerData(this.slot));
                }

            }
        }

        public static void applyToPlayer(Level level, Mob mob, float rangeModifier) {

            if (level != null && level.isClient()) {
                areaList.showAllAreaParticles(mob, rangeModifier);
            }

        }
    }
}
