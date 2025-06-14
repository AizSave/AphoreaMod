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
import necesse.level.maps.Level;

public class SpinelStaff extends AphAreaToolItem {

    public SpinelStaff() {
        super(1300, false, true);
        rarity = Rarity.COMMON;
        attackAnimTime.setBaseValue(1000);

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
