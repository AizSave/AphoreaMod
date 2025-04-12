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

    public static void healMob(Mob healer, Mob target, int healing) {
        healMob(healer, target, healing, null, null);
    }

    public static void healMob(Mob healer, Mob target, int healing, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        healMobExecute(healer, target, AphMagicHealing.getMagicHealing(healer, target, healing, toolItem, item), item, toolItem);
    }

    public static void healMobExecute(Mob healer, Mob target, int healing) {
        healMobExecute(healer, target, healing, null, null);
    }

    public static void healMobExecute(Mob healer, Mob target, int healing, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        int realHealing = Math.min(healing, target.getMaxHealth() - target.getHealth());
        if (realHealing > 0) {
            target.getLevel().entityManager.addLevelEvent(new MobHealthChangeEvent(target, realHealing));

            healer.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingFunctions)
                    .forEach(buff -> ((AphMagicHealingFunctions) buff.buff).onMagicalHealing(healer, target, healing, realHealing, toolItem, item));

            if (toolItem instanceof AphMagicHealingFunctions) {
                ((AphMagicHealingFunctions) toolItem).onMagicalHealing(healer, target, healing, realHealing, toolItem, item);
            }

            cooldowns.put(target, target.getWorldTime() + 50);

            if (healer.getID() != target.getID()) {
                int magicalHealingGrace = (int) (realHealing * getHealingGrace(healer, toolItem, item));
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
        return (int) (getFlatMagicHealing(healer, target, healing) * getMagicHealingMod(healer, target, toolItem, item));
    }

    public static float getMagicHealingMod(@Nullable Mob healer, @Nullable Mob target, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        float mod = 1F;
        if(healer != null) {
            mod += healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING);
        }
        if(target != null) {
            mod += target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED);
        }
        if(toolItem != null && item != null) {
            mod += toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING);
            if(healer == target) {
                mod += toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_RECEIVED);
            }
        }

        return mod;
    }


    public static int getFlatMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing) {
        return healing + getFlatMagicHealingMod(healer, target);
    }

    public static int getFlatMagicHealingMod(@Nullable Mob healer, @Nullable Mob target) {
        int mod = 0;
        if(healer != null) {
            mod += healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_FLAT);
        }
        if(target != null) {
            mod += target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED_FLAT);
        }

        return mod;
    }

    public static float getHealingGrace(@Nullable Mob healer, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        float grace = 0;
        if(healer != null) {
            grace += healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_GRACE);
        }
        if(toolItem != null && item != null) {
            grace += toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_GRACE);
        }
        return grace;
    }

    public static String getMagicHealingToolTipPercent(@Nullable Mob healer, @Nullable Mob target, float healingPercent) {
        return getMagicHealingToolTipPercent(healer, target, healingPercent, null, null);
    }

    public static String getMagicHealingToolTipPercent(@Nullable Mob healer, @Nullable Mob target, float healingPercent, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        final float finalHealingPercent = healingPercent * getMagicHealingMod(healer, target, toolItem, item);

        if (finalHealingPercent < 0) {
            return "0%";
        } else {
            String value = String.format("%.2f", finalHealingPercent * 100);
            return (value.endsWith(".00") ? value.substring(0, value.length() - 3) : value) + "%";
        }
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
