package aphorea.items.weapons.melee.battleaxe;

import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import aphorea.items.weapons.melee.battleaxe.logic.BattleaxeAttackHandler;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

import java.awt.*;

abstract public class AphBattleaxeToolItem extends AphGreatswordToolItem implements ItemInteractAction {
    public GreatswordChargeLevel[] rushChargeLevels;
    boolean isCharging;

    public AphBattleaxeToolItem(int enchantCost, GreatswordChargeLevel[] chargeLevels, GreatswordChargeLevel[] rushChargeLevels) {
        super(enchantCost, chargeLevels);
        this.rushChargeLevels = rushChargeLevels;
        if (rushChargeLevels.length == 0) {
            throw new IllegalArgumentException("Must have at least one charge level for battleaxes berserker rush");
        }
        this.keyWords.add("battleaxe");
        this.keyWords.remove("sword");
    }

    public static GreatswordChargeLevel[] getChargeLevel(int time, Color color) {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(time, 1.0F, color)};
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "battleaxe"));
        tooltips.add(Localization.translate("itemtooltip", "battleaxe2"));
        return tooltips;
    }


    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GreatswordChargeLevel[] charge = attackerMob.buffManager.hasBuff("berserkerrush") ? this.rushChargeLevels : this.chargeLevels;

        attackerMob.startAttackHandler(new BattleaxeAttackHandler(attackerMob, slot, item, this, seed, x, y, charge));

        return item;
    }


    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "battleaxe");
    }

    @Override
    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1000;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding() && !attackerMob.isAttacking && !this.isCharging && !attackerMob.buffManager.hasBuff("berserkerrush") && !attackerMob.buffManager.hasBuff("berserkerrushcooldown");
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        attackerMob.addBuff(new ActiveBuff(AphBuffs.BERSERKER_RUSH, attackerMob, 11.0F, null), true);

        return item;
    }

}
