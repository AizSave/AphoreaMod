package aphorea.other.itemtype;

import aphorea.other.area.AphAreaList;
import aphorea.other.area.AphAreaType;
import aphorea.other.itemtype.healing.AphMagicHealingToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
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
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {

        if (areaList.someType(AphAreaType.HEALING)) {
            onHealingToolItemUsed(player, item);
        }

        if (this.getManaCost(item) > 0) {
            this.consumeMana(player, item);
        }

        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);

        usePacket(level, player, rangeModifier);

        if (level.isServer()) {
            areaList.executeAreas(player, rangeModifier, x, y, false, item, this);

            ServerClient serverClient = player.getServerClient();
            level.getServer().network.sendToClientsWithEntityExcept(getPacket(serverClient.slot, rangeModifier), serverClient.playerMob, serverClient);
        }

        return item;
    }

    public abstract Packet getPacket(int slot, float rangeModifier);

    public abstract void usePacket(Level level, PlayerMob player, float rangeModifier);

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item);
    }

    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
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
}
