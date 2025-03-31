package aphorea.items.banners;

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

import java.util.function.Function;

public class AphMightyBanner extends BannerItem {
    public float baseEffect;
    public String[] extraToolTips;

    public AphMightyBanner(Rarity rarity, int range, Function<Mob, Buff> buff, float baseEffect, String... extraToolTips) {
        super(rarity, range, buff);
        this.baseEffect = baseEffect;
        this.extraToolTips = extraToolTips;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        addToolTips(tooltips, perspective);
        tooltips.add(Localization.translate("itemtooltip", "inspiration"));
        tooltips.add(Localization.translate("global", "aphoreamightybanner"));
        return tooltips;
    }

    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        float bannerEffect = baseEffect * (perspective == null ? AphModifiers.INSPIRATION_EFFECT.defaultBuffManagerValue : perspective.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT));
        for (String extraToolTip : extraToolTips) {
            tooltips.add(Localization.translate("itemtooltip", extraToolTip, "effect",  String.format("%.0f", bannerEffect), "effectfloat", bannerEffect));
        }
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
        Buff buff = this.buff.apply(mob);
        if (buff != null) {
            if (mob.buffManager.hasBuff(buff.getID())) {
                ActiveBuff antBuff = mob.buffManager.getBuff(buff.getID());
                if (antBuff != null && antBuff.buff instanceof AphBannerBuff) {
                    AphBannerBuff bannerBuff = (AphBannerBuff) antBuff.buff;
                    if (bannerBuff.shouldRemove(antBuff) || bannerBuff.bannerEffect <= player.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)) {
                        addBuff(buff, mob, player, true);
                    }
                } else {
                    addBuff(buff, mob, player, true);
                }
            } else {
                addBuff(buff, mob, player, false);
            }
        }
    }

    public void addBuff(Buff buff, Mob mob, PlayerMob player, boolean forceOverride) {
        mob.buffManager.addBuff(new ActiveBuff(buff, mob, 100, player), false, forceOverride);
    }

    public int getPlayerRange() {
        return range * 2;
    }
}