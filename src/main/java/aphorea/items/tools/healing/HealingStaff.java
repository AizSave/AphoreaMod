package aphorea.items.tools.healing;

import aphorea.items.AphAreaToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import org.jetbrains.annotations.Nullable;

public class HealingStaff extends AphAreaToolItem implements ItemInteractAction {
    protected IntUpgradeValue magicHealing2 = new IntUpgradeValue(0, 0.2F);

    public HealingStaff() {
        super(650, false, true);
        rarity = Rarity.COMMON;
        attackAnimTime.setBaseValue(1000);

        manaCost.setBaseValue(6.0F);

        attackXOffset = 12;
        attackYOffset = 22;

        magicHealing.setBaseValue(10).setUpgradedValue(1, 20);
        magicHealing2.setBaseValue(6).setUpgradedValue(1, 12);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt3, SoundEffect.effect(attackerMob).volume(1.0F).pitch(1.0F));
        }
    }

    public int getHealing2(@Nullable InventoryItem item) {
        return item == null ? magicHealing2.getValue(0) : magicHealing2.getValue(item.item.getUpgradeTier(item));
    }

    @Override
    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(
                new AphArea(120, AphColors.pink_witch_dark).setHealingArea(getHealing(item)),
                new AphArea(120, AphColors.pink_witch).setHealingArea(getHealing2(item))
        );
    }
}
