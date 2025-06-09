package aphorea.items.tools.weapons.melee.battleaxe;

import aphorea.buffs.AdrenalineBuff;
import aphorea.items.tools.weapons.melee.battleaxe.logic.BattleaxeAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphGreatswordToolItem;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
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
    boolean isCharging;

    public AphBattleaxeToolItem(int enchantCost, GreatswordChargeLevel[] chargeLevels) {
        super(enchantCost, chargeLevels);
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
        tooltips.add(Localization.translate("itemtooltip", "battleaxe3"));
        tooltips.add(Localization.translate("itemtooltip", "adrenaline"));
        return tooltips;
    }


    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canLevelInteract(level, x, y, attackerMob, item)) {
            onLevelInteract(level, x, y, attackerMob, attackHeight, item, slot, getLevelInteractAttackAnimTime(item, attackerMob), mapContent);
        } else {
            attackerMob.startAttackHandler(new BattleaxeAttackHandler(attackerMob, slot, item, this, seed, x, y, attackerMob.buffManager.hasBuff(AphBuffs.BERSERKER_RUSH) ? (1 + 0.1F * AdrenalineBuff.getAdrenalineLevel(attackerMob)) : 1, this.chargeLevels));
        }

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
    public int getLevelInteractAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1000;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding() && !attackerMob.isAttacking && !this.isCharging && !attackerMob.buffManager.hasBuff(AphBuffs.BERSERKER_RUSH) && !attackerMob.buffManager.hasBuff(AphBuffs.BERSERKER_RUSH_COOLDOWN);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        attackerMob.addBuff(new ActiveBuff(AphBuffs.BERSERKER_RUSH, attackerMob, 11.0F, null), true);

        AdrenalineBuff.giveAdrenaline(attackerMob, 3, 4000, false);

        return item;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        AdrenalineBuff.giveAdrenaline(attacker, 3000, true);

        super.hitMob(item, event, level, target, attacker);
    }
}
