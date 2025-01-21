package aphorea.items.weapons.range.greatbow;

import aphorea.utils.AphColors;
import aphorea.utils.AphMaths;
import aphorea.items.vanillaitemtypes.weapons.AphGreatbowProjectileToolItem;
import aphorea.packets.AphCustomPushPacket;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.Objects;

public class UnstableGelGreatbow extends AphGreatbowProjectileToolItem {
    public UnstableGelGreatbow() {
        super(200);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(700);
        this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 60.0F);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackXOffset = 10;
        this.attackYOffset = 34;
        this.particleColor = AphColors.unstableGel;
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, PacketReader contentReader) {
        for (int i = 0; i < 3; i++) {

            float endX = x;
            float endY = y;

            float[] vector = AphMaths.perpendicularVector(x, y, player.x, player.y);

            if (i == 1) {
                endX = x + vector[0] / 4;
                endY = y + vector[1] / 4;
            } else if (i == 2) {
                endX = x - vector[0] / 4;
                endY = y - vector[1] / 4;
            }

            super.fireProjectiles(level, (int) endX, (int) endY, player, item, seed, arrow, i == 2, contentReader);
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
        return item;
    }

    @Override
    public InventoryItem superOnAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        super.superOnAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
        int strength = 50;
        Point2D.Float dir = GameMath.normalize((float) x - player.x, (float) y - player.y);
        AphCustomPushPacket.applyToPlayer(level, player, -dir.x, -dir.y, (float) strength);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, player, 0.15F, null), level.isServer());
        player.buffManager.forceUpdateBuffs();

        if (player.isServer()) {
            ServerClient serverClient = player.getServerClient();
            level.getServer().network.sendToClientsWithEntityExcept(new AphCustomPushPacket(serverClient.slot, -dir.x, -dir.y, (float) strength), serverClient.playerMob, serverClient);
        }

        return item;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff2"));
        tooltips.add(Localization.translate("itemtooltip", "unstablegelgreatbow"));
        tooltips.add(Localization.translate("itemtooltip", "threearrows"));
        return tooltips;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, Mob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, PacketReader contentReader) {
        if (Objects.equals(arrow.getStringID(), "stonearrow") || Objects.equals(arrow.getStringID(), "gelarrow")) {
            return super.getProjectile(level, x, y, owner, item, seed, (ArrowItem) ItemRegistry.getItem("unstablegelarrow"), consumeAmmo, velocity, range, damage, knockback, resilienceGain, contentReader);
        } else {
            return super.getProjectile(level, x, y, owner, item, seed, arrow, consumeAmmo, velocity, range, damage, knockback, resilienceGain, contentReader);
        }
    }
}
