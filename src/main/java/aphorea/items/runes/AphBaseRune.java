package aphorea.items.runes;

import aphorea.buffs.Runes.AphBaseRuneActiveBuff;
import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.registry.AphItems;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class AphBaseRune extends Item {
    private final String buffID;
    private final int tooltipsNumber;
    private final String[] extraToolTips;

    public AphBaseRune(Rarity rarity, String buffID, int tooltipsNumber, String... extraToolTips) {
        super(1);
        this.buffID = buffID;
        this.tooltipsNumber = tooltipsNumber;
        this.rarity = rarity;
        this.extraToolTips = extraToolTips;

        this.setItemCategory("misc", "runes", "baserunes");
        this.setItemCategory(ItemCategory.craftingManager, "runes", "baserunes");
    }

    public AphBaseRune(String buffID, int tooltipsNumber, String... extraToolTips) {
        this(Rarity.COMMON, buffID, tooltipsNumber, extraToolTips);
    }

    public AphBaseRune(Rarity rarity, int tooltipsNumber, String... extraToolTips) {
        this(rarity, null, tooltipsNumber, extraToolTips);
    }

    public AphBaseRune(int tooltipsNumber, String... extraToolTips) {
        this(Rarity.COMMON, null, tooltipsNumber, extraToolTips);
    }

    public AphBaseRune setInitialRune() {
        AphItems.initialRunes.add(this);
        return this;
    }

    public AphBaseRuneTrinketBuff getBuff() {
        return (AphBaseRuneTrinketBuff) BuffRegistry.getBuff(buffID == null ? getStringID() : buffID);
    }

    public AphBaseRuneActiveBuff getActiveBuff() {
        return (AphBaseRuneActiveBuff) BuffRegistry.getBuff(getBuff().getBuff());
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "baserune"));
        String runeOwner = item.getGndData().getString("runeOwner", null);
        if (runeOwner != null) {
            tooltips.add(Localization.translate("itemtooltip", "linkedrune", "player", runeOwner));
        }
        addToolTips(tooltips, item, null, perspective, false);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        return item.getGndData().getString("runeOwner", null) == null ? super.getBrokerValue(item) : 0;
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, AphRunesInjector runesInjector, PlayerMob perspective, boolean isFinalBuff) {
        AphBaseRuneTrinketBuff buff = getBuff();
        float effectNumber = buff.getBaseEffectNumber() * (isFinalBuff ? buff.getEffectNumberVariation(item, runesInjector) : 1);
        float healthCost = isFinalBuff ? buff.getFinalHealthCost(item, runesInjector) : buff.getBaseHealthCost();
        int cooldown;
        if (buff.isTemporary()) {
            AphBaseRuneActiveBuff activeBuff = getActiveBuff();
            cooldown = (int) (activeBuff.getBaseCooldownDuration() * (isFinalBuff ? AphBaseRuneTrinketBuff.getCooldownVariation(item, runesInjector) : 1));
        } else {
            cooldown = (int) (getBuff().getBaseCooldown() * (isFinalBuff ? AphBaseRuneTrinketBuff.getCooldownVariation(item, runesInjector) : 1));

        }
        float cooldownSeconds = (float) cooldown / 1000;

        String baseRunePrefix = isFinalBuff ? "§i[B]§0 " : "";

        for (int i = 0; i < tooltipsNumber; i++) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
            tooltips.add(baseRunePrefix + Localization.translate("itemtooltip", getStringID() + tooltipNumber, "effectNumber", (float) Math.round(effectNumber * 100) / 100, "F0effectNumber", effectNumber));
        }
        for (String extraToolTip : extraToolTips) {
            tooltips.add(baseRunePrefix + Localization.translate("itemtooltip", extraToolTip));
        }

        float extraEffectNumberMod = buff.getExtraEffectNumberMod() - 1;
        if (extraEffectNumberMod != 0) {
            if (extraEffectNumberMod > 0) {
                tooltips.add(baseRunePrefix + Localization.translate("itemtooltip", "moreextraeffectmod", "mod", extraEffectNumberMod * 100));
            } else {
                tooltips.add(baseRunePrefix + Localization.translate("itemtooltip", "lessextraeffectmod", "mod", Math.abs(extraEffectNumberMod) * 100));
            }
        }

        if (healthCost > 0) {
            tooltips.add(Localization.translate("itemtooltip", "runehealthcost", "health", Math.round(healthCost * 100)));
        } else if (healthCost < 0) {
            tooltips.add(Localization.translate("itemtooltip", "runehealthhealing", "health", Math.round(-healthCost * 100)));
        }

        if (isFinalBuff) {
            for (int i = 0; i < runesInjector.getTooltipsNumber(); i++) {
                String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
                tooltips.add(Localization.translate("itemtooltip", runesInjector.getStringID() + "_mod" + tooltipNumber));
            }

            runesInjector.getModifierRunes(item).forEach(
                    b -> {
                        for (int i = 0; i < b.getTooltipsNumber(); i++) {
                            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
                            tooltips.add("§a[M]§0 " + Localization.translate("itemtooltip", b.getStringID() + tooltipNumber));
                        }
                        for (String extraToolTip : b.getExtraToolTips()) {
                            tooltips.add("§a[M]§0 " + Localization.translate("itemtooltip", extraToolTip));
                        }
                    }
            );
        }

        tooltips.add(Localization.translate("itemtooltip", "runecooldown", "seconds", cooldownSeconds));
    }

    @Override
    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/runes/" + this.getStringID());
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "baserune");
    }
}
