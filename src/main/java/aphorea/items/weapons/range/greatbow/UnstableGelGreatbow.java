package aphorea.items.weapons.range.greatbow;

import aphorea.items.vanillaitemtypes.weapons.AphGreatbowProjectileToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.utils.AphColors;
import aphorea.utils.AphMaths;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
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
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean dropItem, GNDItemMap mapContent) {
        for (int i = 0; i < 3; i++) {

            float endX = x;
            float endY = y;

            float[] vector = AphMaths.perpendicularVector(x, y, attackerMob.x, attackerMob.y);

            if (i == 1) {
                endX = x + vector[0] / 4;
                endY = y + vector[1] / 4;
            } else if (i == 2) {
                endX = x - vector[0] / 4;
                endY = y - vector[1] / 4;
            }

            super.fireProjectiles(level, (int) endX, (int) endY, attackerMob, item, seed, arrow, i == 2, mapContent);
        }
    }

    @Override
    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        super.superOnAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);

        int strength = 50;
        Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();

        if (attackerMob.isServer()) {
            level.getServer().network.sendToAllClients(new AphCustomPushPacket(attackerMob, -dir.x, -dir.y, (float) strength));
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
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        if (Objects.equals(arrow.getStringID(), "stonearrow") || Objects.equals(arrow.getStringID(), "gelarrow")) {
            return super.getProjectile(level, x, y, owner, item, seed, (ArrowItem) ItemRegistry.getItem("unstablegelarrow"), consumeAmmo, velocity, range, damage, knockback, resilienceGain, mapContent);
        } else {
            return super.getProjectile(level, x, y, owner, item, seed, arrow, consumeAmmo, velocity, range, damage, knockback, resilienceGain, mapContent);
        }
    }
}
