package aphorea.items.ammo;

import aphorea.items.vanillaitemtypes.AphBulletItem;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;

public class SpamBullet extends AphBulletItem {
    public SpamBullet() {
        this.rarity = Rarity.UNCOMMON;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        ToolItem toolItem = null;
        InventoryItem item = null;
        if (owner.isPlayer) {
            PlayerMob player = (PlayerMob) owner;
            item = player.attackSlot.getItem(player.getInv());
            toolItem = (ToolItem) item.item;
        }
        return new SpamBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, toolItem, item, owner);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate("itemtooltip", "spambullet"));
        tooltips.add(Localization.translate("itemtooltip", "spambullet1"));
        tooltips.add(Localization.translate("itemtooltip", "spambullet2"));
        tooltips.add(Localization.translate("itemtooltip", "spambullet3"));
        tooltips.add(Localization.translate("itemtooltip", "spambullet4"));
        tooltips.add(Localization.translate("itemtooltip", "spambullet5"));
        return tooltips;
    }

    @Override
    public boolean overrideProjectile() {
        return true;
    }
}