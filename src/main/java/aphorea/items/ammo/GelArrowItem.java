package aphorea.items.ammo;

import aphorea.items.vanillaitemtypes.AphArrowItem;
import aphorea.projectiles.arrow.GelArrowProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;

public class GelArrowItem extends AphArrowItem {
    public GelArrowItem() {
        this.damage = 2;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, ItemAttackerMob owner) {
        ToolItem toolItem = null;
        InventoryItem item = null;
        if (owner.isPlayer) {
            PlayerMob player = (PlayerMob) owner;
            item = player.attackSlot.getItem(player.getInv());
            toolItem = (ToolItem) item.item;
        }
        return new GelArrowProjectile(damage, knockback, toolItem, item, owner.getLevel(), owner, x, y, targetX, targetY, velocity, range);
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stikybuff1"));
        tooltips.add(Localization.translate("itemtooltip", "projectilearea"));
        return tooltips;
    }
}
