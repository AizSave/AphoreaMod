package aphorea.utils.magichealing;

import aphorea.items.tools.healing.AphMagicHealingToolItem;
import aphorea.registry.AphModifiers;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AphMagicHealing {

    static Map<Mob, Long> cooldowns = new HashMap<>();

    public static boolean canHealMob(Mob healer, @NotNull Mob target) {
        return target.getHealthPercent() != 1 && (healer == target || !target.canBeTargeted(healer, healer.isPlayer ? ((PlayerMob) healer).getNetworkClient() : null)) && (!cooldowns.containsKey(target) || target.getWorldTime() >= cooldowns.get(target));
    }

    public static void healMob(Mob healer, Mob target, int healing, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        int magicalHealing = AphMagicHealing.getMagicHealing(healer, target, healing, toolItem, item);
        int realHealing = Math.min(magicalHealing, target.getMaxHealth() - target.getHealth());
        if (realHealing > 0) {
            target.getLevel().entityManager.addLevelEvent(new MobHealthChangeEvent(target, realHealing));

            healer.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingFunctions).forEach(buff -> ((AphMagicHealingFunctions) buff.buff).onMagicalHealing(healer, target, healing, realHealing, toolItem, item));
            if (toolItem instanceof AphMagicHealingFunctions) {
                ((AphMagicHealingFunctions) toolItem).onMagicalHealing(healer, target, healing, realHealing, toolItem, item);
            }

            cooldowns.put(target, target.getWorldTime() + 50);

            if (healer.getID() != target.getID()) {
                float healGrace = healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_GRACE) + (toolItem == null || item == null ? 0 : toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_GRACE));
                int magicalHealingGrace = (int) Math.floor(realHealing * healGrace);
                int realHealingGrace = Math.min(magicalHealingGrace, healer.getMaxHealth() - healer.getHealth());
                if (realHealingGrace > 0) {
                    target.getLevel().entityManager.addLevelEvent(new MobHealthChangeEvent(healer, realHealingGrace));
                }
            }
        }
    }


    public static int getMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing) {
        return getMagicHealing(healer, target, healing, null, null);
    }

    public static int getMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        return (int) (getFlatMagicHealing(healer, target, healing) * ((healer == null ? 1.0F : healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING) + (toolItem == null || item == null ? 0 : toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING))) + (target == null ? 1.0F : target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED) + (healer == target ? (toolItem == null || item == null ? 0 : toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_RECEIVED)) : 0)) - 1));
    }


    protected static int getFlatMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing) {
        return healing + (healer == null ? 0 : healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_FLAT)) + (target == null ? 0 : target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED_FLAT));
    }

    @NotNull
    public static String getMagicHealingToolTip(@Nullable Mob healerMob, int healing, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        int received = getMagicHealing(healerMob, healerMob, healing, toolItem, item);
        int normal = getMagicHealing(healerMob, null, healing, toolItem, item);
        if (received == normal) {
            return String.valueOf(normal);
        } else {
            return received + " | " + normal;
        }
    }

    public static String getMagicHealingToolTipPercent(@Nullable Mob healer, @Nullable Mob target, float healingPercent, int healing) {
        return getMagicHealingToolTipPercent(healer, target, healingPercent, healing, null, null);
    }

    public static String getMagicHealingToolTipPercent(@Nullable Mob healer, @Nullable Mob target, float healingPercent, int healing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        String toolTip;
        healingPercent *= ((healer == null ? 1.0F : healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING) + (toolItem == null || item == null ? 0 : toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING))) + (target == null ? 1.0F : target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED) + (healer == target ? (toolItem == null || item == null ? 0 : toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_RECEIVED)) : 0)) - 1);

        if (healingPercent < 0) {
            toolTip = "0%";
        } else {
            toolTip = (int) healingPercent + "%";
        }

        healing += (healer == null ? 0 : healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_FLAT)) + (target == null ? 0 : target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED_FLAT));
        if (healing > 0) {
            toolTip += " +" + healing;
        } else if (healing < 0) {
            toolTip += " -" + healing;
        }
        return toolTip;
    }

    public static void addMagicHealingTip(AphMagicHealingToolItem aphoreaMagicHealingToolItem, ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective) {
        int healing = getMagicHealing(perspective, null, aphoreaMagicHealingToolItem.getHealing(currentItem), aphoreaMagicHealingToolItem, currentItem);
        DoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", healing, 0);

        if (lastItem != null) {
            int lastHealing = getMagicHealing(perspective, null, aphoreaMagicHealingToolItem.getHealing(lastItem), aphoreaMagicHealingToolItem, lastItem);
            tip.setCompareValue(lastHealing);
        }

        list.add(100, tip);
    }
}
