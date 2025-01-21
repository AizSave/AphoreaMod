package aphorea.items.runes;

import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class AphModifierRune extends Item {
    private final String buffID;
    private final int tooltipsNumber;
    private final String[] extraToolTips;

    public AphModifierRune(Rarity rarity, String buffID, int tooltipsNumber, String... extraToolTips) {
        super(1);
        this.buffID = buffID;
        this.tooltipsNumber = tooltipsNumber;
        this.rarity = rarity;
        this.extraToolTips = extraToolTips;

        this.setItemCategory("misc", "runes", "modifierrunes");
        this.setItemCategory(ItemCategory.craftingManager, "runes", "modifierrunes");

    }

    public AphModifierRune(String buffID, int tooltipsNumber, String... extraToolTips) {
        this(Rarity.COMMON, buffID, tooltipsNumber, extraToolTips);
    }

    public AphModifierRune(Rarity rarity, int tooltipsNumber, String... extraToolTips) {
        this(rarity, null, tooltipsNumber, extraToolTips);
    }

    public AphModifierRune(int tooltipsNumber, String... extraToolTips) {
        this(Rarity.COMMON, null, tooltipsNumber, extraToolTips);
    }


    public AphModifierRuneTrinketBuff getBuff() {
        return (AphModifierRuneTrinketBuff) BuffRegistry.getBuff(buffID == null ? getStringID() : buffID);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "modifierrune"));
        addToolTips(tooltips, item, perspective);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective) {
        AphModifierRuneTrinketBuff buff = getBuff();
        float effectNumberVariation = buff.getEffectNumberVariation();
        float effectCooldownVariation = buff.getCooldownVariation();
        float healthCost = buff.getHealthCost();

        if (effectNumberVariation > 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaseruneeffectnumber", "variation", Math.round(effectNumberVariation * 100)));
        } else if (effectNumberVariation < 0) {
            tooltips.add(Localization.translate("itemtooltip", "decreaseruneeffectnumber", "variation", Math.round(-effectNumberVariation * 100)));
        }

        if (effectCooldownVariation > 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaserunecooldown", "variation", Math.round(effectCooldownVariation * 100)));
        } else if (effectCooldownVariation < 0) {
            tooltips.add(Localization.translate("itemtooltip", "decreaserunecooldown", "variation", Math.round(-effectCooldownVariation * 100)));
        }

        if (healthCost > 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaserunehealthcost", "health", Math.round(healthCost * 100)));
        } else if (healthCost < 0) {
            tooltips.add(Localization.translate("itemtooltip", "increaserunehealthhealing", "health", Math.round(-healthCost * 100)));
        }

        for (int i = 0; i < tooltipsNumber; i++) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
            tooltips.add(Localization.translate("itemtooltip", getStringID() + tooltipNumber));
        }
        for (String extraToolTip : this.getExtraToolTips()) {
            tooltips.add(Localization.translate("itemtooltip", extraToolTip));
        }
    }

    public int getTooltipsNumber() {
        return tooltipsNumber;
    }

    public String[] getExtraToolTips() {
        return extraToolTips;
    }

    @Override
    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/runes/" + this.getStringID());
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "modifierrune");
    }
}
