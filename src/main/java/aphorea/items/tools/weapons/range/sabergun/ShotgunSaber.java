package aphorea.items.tools.weapons.range.sabergun;

import aphorea.projectiles.bullet.ShotgunBulletProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class ShotgunSaber extends AphSaberGunToolItem {

    public ShotgunSaber() {
        super(1300);
        rarity = Rarity.RARE;
        attackDamage.setBaseValue(14)
                .setUpgradedValue(1, 32);
        knockback.setBaseValue(20);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        float spreadPercent = spreadPercent(item.getGndData().getFloat("chargePercent"));
        GameDamage baseDamage = this.getAttackDamage(item);
        GameDamage damage;
        int spriteX;

        float projectileSpeed = getProjectileVelocity(item, attackerMob);
        int range = getAttackRange(item);
        int knockback = getKnockback(item, attackerMob);

        if (spreadPercent <= 0.2F) {
            damage = baseDamage.setDamage(baseDamage.damage * 1.25F);
            spriteX = 1;
        } else {
            float statsMod = (1 - spreadPercent + 0.1F) * 0.4F + 0.6F;

            damage = baseDamage.setDamage(baseDamage.damage * statsMod);
            spriteX = 0;

            projectileSpeed *= statsMod;
            range = (int) (range * statsMod);
            knockback = (int) (knockback * statsMod);
        }

        return new ShotgunBulletProjectile(attackerMob.x, attackerMob.y, x, y, projectileSpeed, range, damage, getArmorPenPercent(level, attackerMob, item), knockback, attackerMob, spriteX);
    }

    @Override
    public int getProjectilesNumber(InventoryItem item) {
        return 6;
    }

    @Override
    public float getProjectilesMaxSpread(InventoryItem item) {
        return 4 + 26 * spreadPercent(item.getGndData().getFloat("chargePercent"));
    }

    @Override
    public float getDashDamageMultiplier(InventoryItem item) {
        return 5;
    }

    @Override
    public void doAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        boolean shouldFire;
        if (attackerMob instanceof AmmoUserMob) {
            AmmoConsumed consumed = ((AmmoUserMob) attackerMob).removeAmmo(ItemRegistry.getItem("simplebullet"), 1, "bulletammo");
            shouldFire = consumed.amount >= 1;
        } else {
            shouldFire = true;
        }

        if (shouldFire) {
            int projectilesNumber = this.getProjectilesNumber(item);
            float maxSpread = this.getProjectilesMaxSpread(item);
            for (int i = 0; i < projectilesNumber; i++) {
                Projectile projectile = this.getProjectile(level, x, y, attackerMob, item);
                projectile.moveDist(GameRandom.globalRandom.getFloatOffset(10, 20));
                projectile.height -= 2;
                GameRandom random = new GameRandom(seed);
                projectile.resetUniqueID(random);

                level.entityManager.projectiles.addHidden(projectile);

                projectile.setAngle((float) (Math.toDegrees(Math.atan2(y - attackerMob.y, x - attackerMob.x)) + 90 + GameRandom.globalRandom.getFloatOffset(0.0F, maxSpread)));
                if (level.isServer()) {
                    level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
                }
            }
        }
    }

    @Override
    public void addLeftClickTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "shotgunsaber"));
    }

    @Override
    public float getBaseArmorPenPercent() {
        return 0.5F;
    }
}
