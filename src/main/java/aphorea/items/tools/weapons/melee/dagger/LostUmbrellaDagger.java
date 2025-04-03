package aphorea.items.tools.weapons.melee.dagger;

import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;

public class LostUmbrellaDagger extends AphDaggerToolItem {
    protected GameTexture attackOpenTexture;

    public LostUmbrellaDagger() {
        super(300);
        this.rarity = Rarity.EPIC;
        this.attackAnimTime.setBaseValue(500);

        this.attackDamage.setBaseValue(100.0F).setUpgradedValue(1.0F, 100.0F);

        this.attackRange.setBaseValue(54);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.LostUmbrellaDaggerProjectile(level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                100 * throwingVelocity, projectileRange(),
                getAttackDamage(item),
                getKnockback(item, attackerMob),
                shouldDrop,
                item.item.getStringID(), item.getGndData()
        );
    }

    @Override
    public int projectileRange() {
        return 400;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.pink_witch;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.attackOpenTexture = GameTexture.fromFile("player/weapons/" + this.getStringID() + "_open");
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        if(attackProgress < 0.5F) {
            return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
        } else {
            ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(new GameSprite(attackOpenTexture));
            itemSprite.itemRotatePoint(this.attackXOffset, this.attackYOffset);
            if (itemColor != null) {
                itemSprite.itemColor(itemColor);
            }

            return itemSprite.itemEnd();
        }
    }

    @Override
    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        float attackProgress = mob.getAttackAnimProgress();

        if(attackProgress < 0.5F) {
            this.width = 8.0F;
        } else {
            this.width = 46.0F;
        }

        return super.getHitboxes(item, mob, aimX, aimY, event, forDebug);
    }
}
