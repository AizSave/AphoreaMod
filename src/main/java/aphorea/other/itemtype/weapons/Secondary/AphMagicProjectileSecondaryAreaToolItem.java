package aphorea.other.itemtype.weapons.Secondary;

import aphorea.other.area.AphAreaList;
import aphorea.other.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

import java.util.HashSet;
import java.util.Set;

abstract public class AphMagicProjectileSecondaryAreaToolItem extends AphMagicProjectileToolItem implements ItemInteractAction {

    AphAreaList areaList;

    int secondaryAttackAnimTime;
    float consumeManaSecondary;

    public AphMagicProjectileSecondaryAreaToolItem(int enchantCost, AphAreaList areaList, int secondaryAttackAnimTime, float consumeManaSecondary) {
        super(enchantCost);
        this.areaList = areaList;
        this.secondaryAttackAnimTime = secondaryAttackAnimTime;
        this.consumeManaSecondary = consumeManaSecondary;
    }


    public float getSecondaryManaCost(InventoryItem item) {
        return (consumeManaSecondary * this.getManaUsageModifier(item));
    }

    @Override
    public int getLevelInteractAttackAnimTime(InventoryItem item, PlayerMob player) {
        return Math.round(secondaryAttackAnimTime * (1.0F / this.getAttackSpeedModifier(item, player)));
    }

    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.isAttacking;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        this.consumeManaSecondary(player, item);

        float attackDamage0 = attackDamage.getValue(0);
        float attackDamage1 = attackDamage.getValue(1);

        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);

        usePacket(level, player, rangeModifier);

        if (player.isServer()) {
            areaList.executeAreas(player, rangeModifier, x, y, false, item, this);

            ServerClient serverClient = player.getServerClient();
            level.getServer().network.sendToClientsWithEntityExcept(getPacket(serverClient.slot, rangeModifier), serverClient.playerMob, serverClient);
        }

        attackDamage.setBaseValue(attackDamage0).setUpgradedValue(1, attackDamage1);

        return item;
    }

    public abstract Packet getPacket(int slot, float rangeModifier);

    public abstract void usePacket(Level level, PlayerMob player, float rangeModifier);

    public void consumeManaSecondary(PlayerMob player, InventoryItem item) {
        float manaCost = getSecondaryManaCost(item);
        if (manaCost > 0.0F) {
            player.useMana(manaCost, player.isServerClient() ? player.getServerClient() : null);
        }
    }

    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        Set<Integer> enchantments = new HashSet<>(super.getValidEnchantmentIDs(item));
        enchantments.addAll(AphEnchantments.areaItemEnchantments);
        return enchantments;
    }
}
