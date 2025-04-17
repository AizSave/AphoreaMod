package aphorea.items.tools.weapons.summoner;

import aphorea.items.vanillaitemtypes.weapons.AphSummonToolItem;
import necesse.entity.mobs.itemAttacker.FollowPosition;

public class VolatileGelStaff extends AphSummonToolItem {
    public VolatileGelStaff() {
        super("volatilegelslime", FollowPosition.WALK_CLOSE, 1.0F, 400);
        this.summonType = "summonedmobtemp";
        this.rarity = Rarity.COMMON;
        this.attackDamage.setBaseValue(60.0F).setUpgradedValue(1.0F, 120.0F);
    }
}
