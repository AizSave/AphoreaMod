package aphorea.items.healingtools;

import aphorea.other.itemtype.healing.AphHealingProjectileToolItem;
import aphorea.projectiles.toolitem.WoodenWandProjectile;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class WoodenWand extends AphHealingProjectileToolItem {

    public WoodenWand() {
        super(200);
        rarity = Rarity.UNCOMMON;
        attackAnimTime.setBaseValue(800);

        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(200);
        manaCost.setBaseValue(5.0F);

        attackXOffset += 10;
        attackYOffset += 15;

        magicHealing.setBaseValue(2).setUpgradedValue(1, 3);
    }

    public Projectile[] getProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return new Projectile[]{
                new WoodenWandProjectile(this.getHealing(item), this, item, level, player,
                        player.x, player.y,
                        x, y,
                        getProjectileVelocity(item, player),
                        getAttackRange(item)
                )
        };
    }

    @Override
    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
        super.showAttack(level, x, y, mob, attackHeight, item, seed, contentReader);
        SoundManager.playSound(GameResources.magicbolt2, SoundEffect.effect(mob).volume(1.0F).pitch(1.0F));
    }
}
