package aphorea.items.tools.weapons.melee.saber;

import aphorea.projectiles.toolitem.BlueBerryProjectile;
import aphorea.projectiles.toolitem.HoneyProjectile;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.util.ArrayList;

public class HoneySaber extends AphSaberToolItem {

    public HoneySaber() {
        super(850);
        rarity = Rarity.UNCOMMON;
        attackDamage.setBaseValue(34)
                .setUpgradedValue(1, 70);
        knockback.setBaseValue(75);

        this.attackRange.setBaseValue(60);
    }

    static float angleStep = 0.19635F;

    @Override
    public Projectile[] getProjectiles(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        float precision = powerPercent / 1.6F + 0.375F;
        int extraProjectiles = Math.round((precision - 0.5F) * 6);

        ArrayList<Projectile> projectiles = new ArrayList<>();

        float baseAngle = (float) Math.atan2(targetY - y, targetX - x);

        float centralAngle = baseAngle + (float) Math.toRadians(GameRandom.globalRandom.getFloatOffset(0, 3));

        int centralX = (int) (Math.cos(centralAngle) * 100) + x;
        int centralY = (int) (Math.sin(centralAngle) * 100) + y;
        projectiles.add(getProjectile(level, x, y, centralX, centralY, attackerMob, item, powerPercent, true));

        for (int i = 0; i < extraProjectiles; i++) {
            boolean isHoney = (i % 2 == 1);

            float angleLeft = baseAngle - (i + 1) * angleStep + (float) Math.toRadians(GameRandom.globalRandom.getFloatOffset(0, 3));
            float angleRight = baseAngle + (i + 1) * angleStep + (float) Math.toRadians(GameRandom.globalRandom.getFloatOffset(0, 3));

            int leftX = (int) (Math.cos(angleLeft) * 100) + x;
            int leftY = (int) (Math.sin(angleLeft) * 100) + y;
            projectiles.add(getProjectile(level, x, y, leftX, leftY, attackerMob, item, powerPercent, isHoney));

            int rightX = (int) (Math.cos(angleRight) * 100) + x;
            int rightY = (int) (Math.sin(angleRight) * 100) + y;
            projectiles.add(getProjectile(level, x, y, rightX, rightY, attackerMob, item, powerPercent, isHoney));
        }

        for (Projectile projectile : projectiles) {
            projectile.moveDist(GameRandom.globalRandom.getFloatBetween(0, 10));
        }

        return projectiles.toArray(new Projectile[0]);
    }


    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, boolean isHoney) {
        if (isHoney) {
            return new HoneyProjectile(level, attackerMob, x, y, targetX, targetY,
                    150 * powerPercent,
                    (int) (300 * powerPercent),
                    this.getAttackDamage(item).modDamage(powerPercent * 0.25F),
                    (int) (getKnockback(item, attackerMob) * powerPercent * 0.25F)
            );
        } else {
            return new BlueBerryProjectile(level, attackerMob, x, y, targetX, targetY,
                    150 * powerPercent,
                    (int) (300 * powerPercent),
                    this.getAttackDamage(item).modDamage(powerPercent * 0.3F),
                    (int) (getKnockback(item, attackerMob) * powerPercent * 0.3F)
            );
        }
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return getProjectile(level, x, y, targetX, targetY, attackerMob, item, powerPercent, true);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.removeLast();
        tooltips.add(Localization.translate("itemtooltip", "honeysaber"));
        tooltips.add(Localization.translate("itemtooltip", "saberdash"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.HONEYED, target, 2000, attacker), true);
    }
}
