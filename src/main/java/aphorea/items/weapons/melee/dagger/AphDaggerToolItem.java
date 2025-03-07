package aphorea.items.weapons.melee.dagger;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.level.maps.Level;

import java.util.HashSet;
import java.util.Set;

public abstract class AphDaggerToolItem extends SpearToolItem implements ItemInteractAction {

    public AphDaggerToolItem(int enchantCost) {
        super(enchantCost);
        this.keyWords.add("dagger");
        this.keyWords.remove("spear");
        this.width = 8.0F;
        this.attackXOffset = 12;
        this.attackYOffset = 2;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.buffManager.addBuff(new ActiveBuff(AphBuffs.DAGGER_ATTACK, attackerMob, this.getAttackAnimTime(item, attackerMob), null), false);
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.addAll(this.getDisplayNameTooltips(item, perspective, blackboard));
        tooltips.addAll(this.getDebugTooltips(item, perspective, blackboard));
        tooltips.addAll(this.getCraftingMatTooltips(item, perspective, blackboard));
        return tooltips;
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);

        float animation;
        if (item.getGndData().getBoolean("isCharging")) {
            animation = item.getGndData().getFloat("chargePercent") / 2 + 0.5F;
        } else {
            animation = attackProgress;
            if (attackProgress < 0.25) {
                animation += 0.25F;
            } else if (attackProgress < 0.5) {
                animation += 0.5F;
            } else if (attackProgress < 0.75) {
                animation -= 0.25F;
            }
        }
        drawOptions.thrustOffsets(attackDirX, attackDirY, animation);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (!item.getGndData().getBoolean("isCharging")) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "dagger");
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding() && !attackerMob.isAttacking && !attackerMob.buffManager.hasBuff(AphBuffs.SPIN_ATTACK_COOLDOWN);
    }

    public void doSecondaryAttack(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed) {
        if (item != null) {
            InventorySlot slot = player.getInv().streamInventorySlots(false, false, false, false).filter(
                    inventorySlot -> inventorySlot.getItem() == item
            ).findFirst().orElse(null);
            if (slot != null) {
                boolean throwItem = !getEnchantment(item).getModifier(AphModifiers.LOYAL);

                if (slot.isItemLocked() && throwItem) {
                    player.getServerClient().sendChatMessage(Localization.translate("message", "cannottrhowlockeditem"));
                } else if (level.isServer()) {
                    Projectile projectile = this.getProjectile(level, x, y, player, item, throwItem);
                    GameRandom random = new GameRandom(seed);
                    projectile.resetUniqueID(random);
                    level.entityManager.projectiles.addHidden(projectile);

                    level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));

                    if (throwItem) {
                        slot.clearSlot();
                    }
                }
            }
        }
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, this.getValidEnchantmentIDs(item), this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        Set<Integer> enchantments = new HashSet<>(super.getValidEnchantmentIDs(item));
        enchantments.addAll(AphEnchantments.daggerItemEnchantments);
        return enchantments;
    }

    public abstract Projectile getProjectile(Level level, int x, int y, PlayerMob player, InventoryItem item, boolean shouldDrop);

}