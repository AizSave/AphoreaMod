package aphorea.other.itemtype;

import aphorea.other.buffs.trinkets.AphBaseRuneActiveBuff;
import aphorea.other.buffs.trinkets.AphBaseRuneTrinketBuff;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class AphBaseRune extends Item {
    private final String buffID;
    private final int tooltipsNumber;
    public GameTexture validTexture;

    public AphBaseRune(String buffID, int tooltipsNumber) {
        super(1);
        this.buffID = buffID;
        this.tooltipsNumber = tooltipsNumber;
    }

    public AphBaseRune(int tooltipsNumber) {
        this(null, tooltipsNumber);
    }

    public AphBaseRuneTrinketBuff getBuff() {
        return (AphBaseRuneTrinketBuff) BuffRegistry.getBuff(buffID == null ? getStringID() : buffID);
    }

    public AphBaseRuneActiveBuff getActiveBuff() {
        return (AphBaseRuneActiveBuff) BuffRegistry.getBuff(getBuff().getBuff());
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/baserune");
        this.validTexture = GameTexture.fromFile("items/baserune_valid");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        addToolTips(tooltips, item, perspective, false);
        return tooltips;
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, boolean includeModifierRunes) {
        AphBaseRuneTrinketBuff buff = getBuff();
        float effectNumber = includeModifierRunes ? buff.getEffectNumber(perspective) : buff.getBaseEffectNumber();
        float healthCost = includeModifierRunes ? buff.getHealthCost(perspective) : buff.getBaseHealthCost();
        int cooldown;
        if(buff.isTemporary()) {
            AphBaseRuneActiveBuff activeBuff = getActiveBuff();
            cooldown = includeModifierRunes ? activeBuff.getCooldownDuration(perspective) : activeBuff.getBaseCooldownDuration();
        } else {
            cooldown = includeModifierRunes ? getBuff().getCooldownDuration(perspective) : getBuff().getBaseCooldown();
        }
        float cooldownSeconds = (float) cooldown / 1000;

        for(int i = 0; i < tooltipsNumber; i++) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i);
            tooltips.add(Localization.translate("itemtooltip", getStringID() + tooltipNumber, "effectNumber", effectNumber));
        }

        if(healthCost > 0) {
            tooltips.add(Localization.translate("itemtooltip", "runehealthcost", "health", healthCost));
        } else if(healthCost < 0) {
            tooltips.add(Localization.translate("itemtooltip", "runehealthhealing", "health", -healthCost));
        }

        tooltips.add(Localization.translate("itemtooltip", "runecooldown", "seconds", cooldownSeconds));
    }
}
