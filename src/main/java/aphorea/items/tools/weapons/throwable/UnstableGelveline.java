package aphorea.items.tools.weapons.throwable;


import aphorea.items.vanillaitemtypes.weapons.AphThrowToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.UnstableGelvelineProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

public class UnstableGelveline extends AphThrowToolItem {
    float topBaseDamage = 30;
    float topTier1Damage = 80;

    public UnstableGelveline() {
        super(400);
        this.rarity = Rarity.COMMON;
        this.attackAnimTime.setBaseValue(250);
        this.attackCooldownTime.setBaseValue(500);
        this.attackDamage.setBaseValue(topBaseDamage).setUpgradedValue(1, topTier1Damage);
        this.velocity.setBaseValue(200);
        this.attackRange.setBaseValue(1000);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();

        if (attackerMob.isServer()) {
            int strength = 60;
            Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
            level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, dir.x, dir.y, (float) strength), level);
        }

        Projectile projectile = new UnstableGelvelineProjectile(getAttackDamage(item), this.getKnockback(item, attackerMob), this, item, level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item));
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        level.entityManager.projectiles.addHidden(projectile);
        if (level.isServer()) {
            level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
        }
        return item;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff2"));
        tooltips.add(Localization.translate("itemtooltip", "projectilearea"));
        return tooltips;
    }

}
