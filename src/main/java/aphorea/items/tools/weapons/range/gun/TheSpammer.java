package aphorea.items.tools.weapons.range.gun;

import aphorea.items.vanillaitemtypes.weapons.AphGunProjectileToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.level.maps.Level;

import java.awt.*;

public class TheSpammer extends AphGunProjectileToolItem {
    public TheSpammer() {
        super("spambullet", 1300);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(30).setUpgradedValue(1.0F, 45.0F);
        this.attackXOffset = 14;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(150);
        this.knockback.setBaseValue(25);
        this.resilienceGain.setBaseValue(0.5F);
        this.addGlobalIngredient("bulletuser");
    }

    @Override
    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate("itemtooltip", "thespammer"));
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        GameRandom spreadRandom = new GameRandom(seed + 10);
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom(seed + 10), attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int) attackerMob.getDistance((float) x, (float) y);
        } else {
            range = this.getAttackRange(item);
        }

        Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, x, y, range, attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom(seed));
        projectile.setAngle((float) Math.toDegrees(Math.atan2(y - attackerMob.y, x - attackerMob.x)) + spreadRandom.getFloatOffset(0, 6) + 90);

        attackerMob.addAndSendAttackerProjectile(projectile, GameRandom.globalRandom.getIntBetween(10, 20));
    }
}