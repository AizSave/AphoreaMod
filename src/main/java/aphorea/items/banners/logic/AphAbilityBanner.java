package aphorea.items.banners.logic;

import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.util.function.Function;

public class AphAbilityBanner extends AphBanner {
    private final int abilityTicks;
    private float abilityCountTimer;

    public AphAbilityBanner(Rarity rarity, int range, Function<Mob, Buff> buff, int abilityTicks, int baseEffect, String... extraToolTips) {
        super(rarity, range, buff, baseEffect, extraToolTips);
        this.abilityTicks = abilityTicks;
    }

    public AphAbilityBanner(Rarity rarity, int range, Function<Mob, Buff> buff, int abilityTicks, int baseEffect, String tooltips) {
        super(rarity, range, buff, baseEffect, tooltips + "effect", tooltips + "ability");
        this.abilityTicks = abilityTicks;
    }

    public int getAbilityTicks() {
        return abilityTicks;
    }

    public float getAbilityTicks(Mob mob) {
        float abilitySpeed = mob.buffManager.getModifier(AphModifiers.INSPIRATION_ABILITY_SPEED);
        return abilityTicks / abilitySpeed;
    }

    @Override
    public void tickHolding(InventoryItem item, PlayerMob player) {
        super.tickHolding(item, player);

        if (player.isServer() && abilityTicks != 0) {
            abilityCountTimer++;
            if (abilityCountTimer > getAbilityTicks(player)) {
                runServerAbility(player.getLevel(), item, player);
                abilityCountTimer = 0;
            }
        }
    }

    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
    }

    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        String[] effectReplacements = getEffectReplacements(perspective);
        String abilitySeconds = String.format("%.1f", (float) getAbilityTicks() / 20);

        if (extraToolTips.length == 0) {
            tooltips.add(Localization.translate("itemtooltip", getStringID() + "effect", effectReplacements));
            tooltips.add(Localization.translate("itemtooltip", getStringID() + "ability", "time", abilitySeconds));
            addExtraTooltips(tooltips, perspective);
        } else {
            for (String extraToolTip : extraToolTips) {
                tooltips.add(Localization.translate("itemtooltip", extraToolTip, effectReplacements, "time", abilitySeconds));
            }
        }
    }

    public void addExtraTooltips(ListGameTooltips tooltips, PlayerMob perspective) {
    }
}