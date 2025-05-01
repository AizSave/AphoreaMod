package aphorea.items.banners.logic;

import aphorea.buffs.Banners.AphBannerBuff;
import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.BannerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AphBanner extends BannerItem {
    public float[] baseEffect;
    public String[] extraToolTips;

    protected boolean addFloatReplacements;

    public AphBanner(Rarity rarity, int range, Function<Mob, Buff> buff, float[] baseEffect, String... extraToolTips) {
        super(rarity, range, buff);
        this.baseEffect = baseEffect;
        this.extraToolTips = extraToolTips;
        this.addFloatReplacements = false;
    }

    public AphBanner(Rarity rarity, int range, Function<Mob, Buff> buff, float baseEffect, String... extraToolTips) {
        this(rarity, range, buff, new float[]{baseEffect}, extraToolTips);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        addToolTips(tooltips, perspective);
        tooltips.add(Localization.translate("itemtooltip", "inspiration"));
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        String[] effectReplacements = getEffectReplacements(perspective);

        if (extraToolTips.length == 0) {
            tooltips.add(Localization.translate("itemtooltip", getStringID() + "effect", effectReplacements));
        } else {
            for (String extraToolTip : extraToolTips) {
                tooltips.add(Localization.translate("itemtooltip", extraToolTip, effectReplacements));
            }
        }
    }

    public String[] getEffectReplacements(PlayerMob perspective) {
        List<String> replacements = new ArrayList<>();
        for (int i = 0; i < baseEffect.length; i++) {
            replacements.add("effect" + (i == 0 ? "" : (i + 1)));
            float value = baseEffect[i] * (perspective == null ? AphModifiers.INSPIRATION_EFFECT.defaultBuffManagerValue : perspective.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT));
            replacements.add(String.format("%.0f", Math.floor(value)));
            if (addFloatReplacements) {
                replacements.add("effectfloat" + (i == 0 ? "" : (i + 1)));
                replacements.add(String.format("%.2f", value));
            }
        }
        return replacements.toArray(new String[0]);
    }

    @Override
    public void tickHolding(InventoryItem item, PlayerMob player) {
        if (player != null && player.isClient()) {
            this.refreshLight(player.getLevel(), player.x, player.y, item);
        }

        assert player != null;
        GameUtils.streamNetworkClients(player.getLevel()).filter((c) -> this.shouldBuffPlayer(item, player, c.playerMob)).filter((c) -> GameMath.diagonalMoveDistance(player.getX(), player.getY(), c.playerMob.getX(), c.playerMob.getY()) <= (double) getPlayerRange()).forEach((c) -> {
            this.applyBuffs(c.playerMob, player);
        });
        player.getLevel().entityManager.mobs.streamInRegionsInRange(player.x, player.y, getPlayerRange()).filter((m) -> !m.removed()).filter((m) -> this.shouldBuffMob(item, player, m)).filter((m) -> GameMath.diagonalMoveDistance(player.getX(), player.getY(), m.getX(), m.getY()) <= (double) getPlayerRange()).forEach((m) -> {
            this.applyBuffs(m, player);
        });
    }

    @Override
    public void applyBuffs(Mob mob) {
        Buff buff = this.buff.apply(mob);
        if (buff != null) {
            if (mob.buffManager.hasBuff(buff.getID())) {
                Attacker attacker = mob.buffManager.getBuff(buff.getID()).getAttacker();
                if (attacker != null && attacker.getAttackOwner() != null) {
                    return;
                }
            }
            mob.buffManager.addBuff(new ActiveBuff(buff, mob, 100, null), false);
        }
    }

    public void applyBuffs(Mob mob, PlayerMob player) {
        ActiveBuff newBuff = new ActiveBuff(this.buff.apply(mob), mob, 100, player);
        if (buff != null) {
            if (mob.buffManager.hasBuff(newBuff.buff.getID())) {
                ActiveBuff antBuff = mob.buffManager.getBuff(newBuff.buff.getID());
                if (antBuff != null && antBuff.buff instanceof AphBannerBuff) {
                    if (AphBannerBuff.shouldChange(antBuff, newBuff)) {
                        addBuff(newBuff, mob, player, true);
                    }
                } else {
                    addBuff(newBuff, mob, player, true);
                }
            } else {
                addBuff(newBuff, mob, player, false);
            }
        }
    }

    public void addBuff(ActiveBuff ab, Mob mob, PlayerMob player, boolean forceOverride) {
        mob.buffManager.addBuff(ab, false, forceOverride);
    }

    public int getPlayerRange() {
        return range * 2;
    }

    public AphBanner addFloatReplacements(boolean addFloatReplacements) {
        this.addFloatReplacements = addFloatReplacements;
        return this;
    }
}