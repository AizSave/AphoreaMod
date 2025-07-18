package aphorea.items.tools.healing;

import aphorea.projectiles.toolitem.GoldenWandProjectile;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GoldenWand extends AphHealingProjectileToolItem {

    public GoldenWand() {
        super(350);
        rarity = Rarity.NORMAL;
        attackAnimTime.setBaseValue(800);

        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(200);
        manaCost.setBaseValue(5.0F);

        attackXOffset += 10;
        attackYOffset += 15;

        magicHealing.setBaseValue(8).setUpgradedValue(1, 20);
    }

    @Override
    protected Projectile[] getProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new Projectile[]{
                new GoldenWandProjectile(this.getHealing(item), this, item, level, attackerMob,
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
            SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(attackerMob).volume(1.0F).pitch(1.0F));
        }
    }
}
