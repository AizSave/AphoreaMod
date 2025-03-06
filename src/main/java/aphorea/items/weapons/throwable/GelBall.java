package aphorea.items.weapons.throwable;

import aphorea.items.vanillaitemtypes.weapons.AphThrowToolItem;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GelBall extends AphThrowToolItem {
    boolean infinity;

    public GelBall() {
        super(500);
        rarity = Rarity.COMMON;
        attackAnimTime.setBaseValue(500);
        attackDamage.setBaseValue(5);
        velocity.setBaseValue(100);
        knockback.setBaseValue(0);
        attackRange.setBaseValue(200);

        attackXOffset = 12;
        attackYOffset = 22;

        this.dropsAsMatDeathPenalty = true;
        this.stackSize = 500;

        infinity = false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "gelball"));
        tooltips.add(Localization.translate("itemtooltip", "stikybuff1"));
        return tooltips;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.slimesplash, SoundEffect.effect(attackerMob)
                    .volume(0.7f)
                    .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = new GelProjectile(
                level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                getProjectileVelocity(item, attackerMob),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, attackerMob)
        );
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);

        level.entityManager.projectiles.addHidden(projectile);

        if (level.isServer() && attackerMob.isPlayer) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, ((PlayerMob) attackerMob).getServerClient());
        }

        if (!infinity) item.setAmount(item.getAmount() - 1);

        return item;
    }

}
