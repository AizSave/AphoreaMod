package aphorea.items.tools.weapons.range.greatbow;

import aphorea.items.vanillaitemtypes.weapons.AphGreatbowProjectileToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.utils.AphColors;
import aphorea.utils.AphMaths;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.Objects;

public class GelGreatbow extends AphGreatbowProjectileToolItem {
    public GelGreatbow() {
        super(200);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(700);
        this.attackDamage.setBaseValue(15.0F).setUpgradedValue(1.0F, 65.0F);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackXOffset = 10;
        this.attackYOffset = 34;
        this.particleColor = AphColors.gel;
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean dropItem, GNDItemMap mapContent) {
        for (int i = 0; i < 2; i++) {

            float endX;
            float endY;

            float[] vector = AphMaths.perpendicularVector(x, y, attackerMob.x, attackerMob.y);

            if (i == 0) {
                endX = x + vector[0] / 9;
                endY = y + vector[1] / 9;
            } else {
                endX = x - vector[0] / 9;
                endY = y - vector[1] / 9;
            }

            super.fireProjectiles(level, (int) endX, (int) endY, attackerMob, item, seed, arrow, i == 1, mapContent);
        }
    }

    @Override
    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        super.superOnAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();

        if (attackerMob.isServer()) {
            int strength = 50;
            Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
            level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, -dir.x, -dir.y, (float) strength), level);
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
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        ArrowItem shotArrow = arrow;
        if (Objects.equals(arrow.getStringID(), "stonearrow")) {
            shotArrow = (ArrowItem) ItemRegistry.getItem("gelarrow");
        }

        return super.getProjectile(level, x, y, attackerMob, item, seed, shotArrow, consumeAmmo, mapContent);
    }
}
