package aphorea.items.healingtools;

import aphorea.projectiles.toolitem.SpinelWandProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class SpinelWand extends AphHealingProjectileToolItem {

    public SpinelWand() {
        super(400);
        rarity = Rarity.UNCOMMON;
        attackAnimTime.setBaseValue(600);

        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(200);
        manaCost.setBaseValue(4.0F);

        attackXOffset += 10;
        attackYOffset += 15;

        magicHealing.setBaseValue(6).setUpgradedValue(1, 8);
    }

    public Projectile[] getProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return new Projectile[]{
                new SpinelWandProjectile(this.getHealing(item), this, item, level, player,
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
        SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(mob).volume(1.0F).pitch(1.0F));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "spinelwand"));
        return tooltips;
    }
}
