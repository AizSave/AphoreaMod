package aphorea.items.weapons.melee.battleaxe;

import aphorea.utils.customchargeattacks.AphChargeAttackToolItem;
import aphorea.utils.customchargeattacks.AphCustomChargeAttackHandler;
import aphorea.utils.customchargeattacks.AphCustomChargeLevel;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

import java.awt.*;

abstract public class AphBattleaxeToolItem extends AphChargeAttackToolItem implements ItemInteractAction {
    public AphCustomChargeLevel<AphChargeAttackToolItem>[] rushChargeLevels;
    boolean isCharging;

    public AphBattleaxeToolItem(int enchantCost, AphCustomChargeLevel<AphChargeAttackToolItem>[] chargeLevels, AphCustomChargeLevel<AphChargeAttackToolItem>[] rushChargeLevels) {
        super(enchantCost, chargeLevels);
        this.rushChargeLevels = rushChargeLevels;
        if (rushChargeLevels.length == 0) {
            throw new IllegalArgumentException("Must have at least one charge level for battleaxes berserker rush");
        }
        this.keyWords.add("battleaxe");
        this.keyWords.remove("sword");
    }

    public static AphCustomChargeLevel<AphChargeAttackToolItem>[] getChargeLevel(int time, Color color) {
        return new AphCustomChargeLevel[]{new AphCustomChargeLevel<>(time, 1.0F, color)};
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "battleaxe"));
        tooltips.add(Localization.translate("itemtooltip", "battleaxe2"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        AphCustomChargeLevel[] charge = player.buffManager.hasBuff("berserkerrush") ? this.rushChargeLevels : this.chargeLevels;

        player.startAttackHandler(new BattleaxeAttackHandler(player, slot, item, this, seed, x, y, charge));

        return item;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "battleaxe");
    }

    @Override
    public int getLevelInteractAttackAnimTime(InventoryItem item, PlayerMob player) {
        return 1000;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.isRiding() && !player.isAttacking && !this.isCharging && !player.buffManager.hasBuff("berserkerrush") && !player.buffManager.hasBuff("berserkerrushcooldown");
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {

        ActiveBuff buff = new ActiveBuff(AphBuffs.BERSERKER_RUSH, player, 11.0F, null);
        player.addBuff(buff, true);

        return item;
    }

    public static class BattleaxeAttackHandler extends AphCustomChargeAttackHandler<AphBattleaxeToolItem> {

        public BattleaxeAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, AphBattleaxeToolItem toolItem, int seed, int startX, int startY, AphCustomChargeLevel<AphBattleaxeToolItem>[] chargeLevels) {
            super(player, slot, item, toolItem, seed, startX, startY, chargeLevels);
        }

        @Override
        public void drawWeaponParticles(InventoryItem showItem, Color color) {
        }

    }
}
