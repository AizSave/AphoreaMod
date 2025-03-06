package aphorea.items.healingtools;

import aphorea.projectiles.toolitem.GoldenWandProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GoldenWand extends AphHealingProjectileToolItem {

    public GoldenWand() {
        super(200);
        rarity = Rarity.UNCOMMON;
        attackAnimTime.setBaseValue(800);

        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(200);
        manaCost.setBaseValue(5.0F);

        attackXOffset += 10;
        attackYOffset += 15;

        magicHealing.setBaseValue(8).setUpgradedValue(1, 10);
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
        SoundManager.playSound(GameResources.magicbolt1, SoundEffect.effect(attackerMob).volume(1.0F).pitch(1.0F));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "goldenwand"));
        return tooltips;
    }
}
