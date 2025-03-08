package aphorea.items.ammo;

import aphorea.items.vanillaitemtypes.AphArrowItem;
import aphorea.projectiles.arrow.UnstableGelArrowProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public class UnstableGelArrowItem extends AphArrowItem {
    public UnstableGelArrowItem() {
        this.damage = 10;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, ItemAttackerMob owner) {
        return new UnstableGelArrowProjectile(damage, knockback, damage.damage / 2, owner.getLevel(), owner, x, y, targetX, targetY, velocity, range);
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff2"));
        tooltips.add(Localization.translate("itemtooltip", "projectilearea"));
        return tooltips;
    }
}
