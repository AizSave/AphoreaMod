package aphorea.items;

import aphorea.items.healingtools.AphMagicHealingToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphAreaType;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

import java.util.HashSet;
import java.util.Set;

abstract public class AphAreaToolItem extends AphMagicHealingToolItem {

    AphAreaList areaList;
    public boolean isMagicWeapon;
    public boolean isHealingTool;

    float rotationOffset;

    public AphAreaToolItem(int enchantCost, boolean isMagicWeapon, boolean isHealingTool, AphAreaList areaList) {
        super(enchantCost);

        this.isMagicWeapon = isMagicWeapon;
        this.isHealingTool = isHealingTool;

        this.areaList = areaList;
        damageType = DamageTypeRegistry.MAGIC;

        if (isMagicWeapon) {
            this.setItemCategory("equipment", "weapons", "magicweapons");
            this.setItemCategory(ItemCategory.equipmentManager, "weapons", "magicweapons");
            this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "magicweapons");
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (areaList.someType(AphAreaType.HEALING)) {
            onHealingToolItemUsed(attackerMob, item);
        }

        if (this.getManaCost(item) > 0) {
            this.consumeMana(attackerMob, item);
        }

        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);

        areaList.executeServer(attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, this);

        return item;
    }

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item);
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        if(areaList.someType(AphAreaType.DAMAGE) || areaList.someType(AphAreaType.DEBUFF)) {
            return null;
        } else {
            return super.getItemAttackerCanUseError(mob, item);
        }
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);

        areaList.executeClient(level, attackerMob.x, attackerMob.y, rangeModifier);
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        areaList.addAreasStatTip(list, this, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        Set<Integer> enchantments = new HashSet<>();
        if (isMagicWeapon) {
            enchantments.addAll(EnchantmentRegistry.magicItemEnchantments);
        }
        if (isHealingTool) {
            enchantments.addAll(AphEnchantments.healingItemEnchantments);
        }
        enchantments.addAll(AphEnchantments.areaItemEnchantments);

        return enchantments;
    }

    @Override
    public String getTranslatedTypeName() {
        if (isMagicWeapon) {
            return Localization.translate("item", "magicweapon");
        } else {
            return super.getTranslatedTypeName();
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(0 + this.rotationOffset);
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target, 0);
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        AphArea firstArea = null;
        for (AphArea area : areaList.areas) {
            if(area.areaTypes.contains(AphAreaType.DAMAGE) || area.areaTypes.contains(AphAreaType.DEBUFF)) {
                firstArea = area;
                break;
            }
        }
        if(firstArea == null) {
            firstArea = areaList.areas[0];
        }
        return (int) (firstArea.range * 0.5);
    }
}
