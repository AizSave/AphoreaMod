package aphorea.items.weapons.range.greatbow;

import aphorea.items.vanillaitemtypes.weapons.AphGreatbowProjectileToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.utils.AphColors;
import aphorea.utils.AphMaths;
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

public class GelGreatbow extends AphGreatbowProjectileToolItem {
    public GelGreatbow() {
        super(200);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(700);
        this.attackDamage.setBaseValue(10.0F).setUpgradedValue(1.0F, 45.0F);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackXOffset = 10;
        this.attackYOffset = 34;
        this.particleColor = AphColors.gel;
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, PacketReader contentReader) {
        for (int i = 0; i < 2; i++) {

            float endX;
            float endY;

            float[] vector = AphMaths.perpendicularVector(x, y, player.x, player.y);

            if (i == 0) {
                endX = x + vector[0] / 9;
                endY = y + vector[1] / 9;
            } else {
                endX = x - vector[0] / 9;
                endY = y - vector[1] / 9;
            }

            super.fireProjectiles(level, (int) endX, (int) endY, player, item, seed, arrow, i == 1, contentReader);
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
        tooltips.add(Localization.translate("itemtooltip", "stikybuff1"));
        tooltips.add(Localization.translate("itemtooltip", "twoarrows"));
        tooltips.add(Localization.translate("itemtooltip", "gelgreatbow"));
        return tooltips;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, Mob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, PacketReader contentReader) {
        ArrowItem shotArrow = arrow;
        if (Objects.equals(arrow.getStringID(), "stonearrow")) {
            shotArrow = (ArrowItem) ItemRegistry.getItem("gelarrow");
        }

        return super.getProjectile(level, x, y, owner, item, seed, shotArrow, consumeAmmo, velocity, range, damage, knockback, resilienceGain, contentReader);
    }
}
