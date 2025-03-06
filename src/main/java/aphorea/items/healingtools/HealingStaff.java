package aphorea.items.healingtools;

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
import necesse.level.maps.Level;

public class HealingStaff extends AphAreaToolItem implements ItemInteractAction {

    static AphAreaList areaList = new AphAreaList(
            new AphArea(120, AphColors.palettePinkWitch[1]).setHealingArea(10, 12),
            new AphArea(120, AphColors.palettePinkWitch[0]).setHealingArea(6, 8)
    );

    public HealingStaff() {
        super(500, false, true, areaList);
        rarity = Rarity.UNCOMMON;
        attackAnimTime.setBaseValue(1400);

        manaCost.setBaseValue(6.0F);

        attackXOffset = 12;
        attackYOffset = 22;

        attackDamage.setBaseValue(1)
                .setUpgradedValue(1, 2);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        SoundManager.playSound(GameResources.magicbolt3, SoundEffect.effect(attackerMob).volume(1.0F).pitch(1.0F));
    }
}
