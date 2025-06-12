package aphorea.items.tools.healing;

import aphorea.items.AphAreaToolItem;
import aphorea.projectiles.toolitem.SpinelWandProjectile;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
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
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import org.jetbrains.annotations.Nullable;

public class SpinelStaff extends AphAreaToolItem {

    public SpinelStaff() {
        super(1300, false, true);
        rarity = Rarity.COMMON;
        attackAnimTime.setBaseValue(500);

        manaCost.setBaseValue(6.0F);

        attackXOffset = 12;
        attackYOffset = 22;

        magicHealing.setBaseValue(10).setUpgradedValue(1, 16);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt3, SoundEffect.effect(attackerMob).volume(1.0F).pitch(1.0F));
        }
    }

    @Override
    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(
                new AphArea(300, AphColors.spinel).setHealingArea(getHealing(item))
        );
    }
}
