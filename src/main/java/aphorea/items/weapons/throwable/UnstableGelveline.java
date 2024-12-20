package aphorea.items.weapons.throwable;


import aphorea.other.vanillaitemtypes.weapons.AphThrowToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.UnstableGelvelineProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

public class UnstableGelveline extends AphThrowToolItem {
    float topBaseDamage = 30;
    float topTier1Damage = 80;

    public UnstableGelveline() {
        super(500);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(250);
        this.attackCooldownTime.setBaseValue(500);
        this.attackDamage.setBaseValue(topBaseDamage).setUpgradedValue(1, topTier1Damage);
        this.velocity.setBaseValue(200);
    }

    @Override
    public int getAttackCooldownTime(InventoryItem item, Mob mob) {
        return super.getAttackCooldownTime(item, mob);
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        int strength = 60;
        Point2D.Float dir = GameMath.normalize((float) x - player.x, (float) y - player.y);
        AphCustomPushPacket.applyToPlayer(level, player, dir.x, dir.y, (float) strength);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, player, 0.15F, null), level.isServer());
        player.buffManager.forceUpdateBuffs();

        if (player.isServer()) {
            ServerClient serverClient = player.getServerClient();
            level.getServer().network.sendToClientsWithEntityExcept(new AphCustomPushPacket(serverClient.slot, dir.x, dir.y, (float) strength), serverClient.playerMob, serverClient);
        }

        Projectile projectile = new UnstableGelvelineProjectile(attackDamage, this.getKnockback(item, player), this, item, level, player, player.x, player.y, x, y, this.getProjectileVelocity(item, player), 1000);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        level.entityManager.projectiles.addHidden(projectile);
        if (level.isServer()) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
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
