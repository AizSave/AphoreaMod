package aphorea.items.tools.healing;

import aphorea.projectiles.toolitem.WoodenWandProjectile;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class WoodenWand extends AphHealingProjectileToolItem {

    public WoodenWand() {
        super(100);
        rarity = Rarity.NORMAL;
        attackAnimTime.setBaseValue(800);

        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(200);
        manaCost.setBaseValue(5.0F);

        attackXOffset += 10;
        attackYOffset += 15;

        magicHealing.setBaseValue(6).setUpgradedValue(1, 22);
    }

    @Override
    protected Projectile[] getProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new Projectile[]{
                new WoodenWandProjectile(this.getHealing(item), this, item, level, attackerMob,
                        attackerMob.x, attackerMob.y,
                        x, y,
                        getProjectileVelocity(item, attackerMob),
                        getAttackRange(item)
                )
        };
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt2, SoundEffect.effect(attackerMob).volume(1.0F).pitch(1.0F));
        }
    }
}
