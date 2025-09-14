package aphorea.items.tools.weapons.magic;

import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.projectiles.toolitem.BabylonCandleProjectile;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.level.maps.Level;

public class BabylonCandle extends AphMagicProjectileToolItem {
    public BabylonCandle() {
        super(1550);
        this.rarity = Rarity.EPIC;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(50.0F).setUpgradedValue(1.0F, 70.0F);
        this.velocity.setBaseValue(50);
        this.attackXOffset = 6;
        this.attackYOffset = 20;
        this.attackRange.setBaseValue(400);
        this.manaCost.setBaseValue(2F);
        this.resilienceGain.setBaseValue(0.5F);
        this.itemAttackerProjectileCanHitWidth = 25.0F;
        this.itemAttackerPredictionDistanceOffset = -20.0F;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt2, SoundEffect.effect(attackerMob).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 0.9F)));
        }
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = new BabylonCandleProjectile(level, attackerMob.x, attackerMob.y, (float) x, (float) y, this.getAttackRange(item), this.getAttackDamage(item), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }
}
