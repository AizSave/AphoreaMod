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
import necesse.level.maps.Level;

import java.util.function.Function;

abstract public class AphBanner extends BannerItem {
    private final int abilityTicks;
    private float abilityCountTimer;

    public AphBanner(Rarity rarity, int range, Function<Mob, Buff> buff, int abilityTicks) {
        super(rarity, range, buff);
        this.abilityTicks = abilityTicks;
    }

    public AphBanner(Rarity rarity, int range, Function<Mob, Buff> buff) {
        this(rarity, range, buff, 0);
    }

    public int getAbilityTicks() {
        return abilityTicks;
    }

    public float getAbilityTicks(Mob mob) {
        float bannerAbilitySpeed = mob.buffManager.getModifier(AphModifiers.BANNER_ABILITY_SPEED);
        return abilityTicks / bannerAbilitySpeed;
    }

    public float getAbilityCountTimer() {
        return abilityCountTimer;
    }

    public float setAbilityCountTimer(float percent) {
        abilityCountTimer = getAbilityTicks() * percent;
        return abilityCountTimer;
    }

    public float setAbilityCountTimer(Mob mob, float percent) {
        abilityCountTimer = getAbilityTicks(mob) * percent;
        return abilityCountTimer;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        addToolTips(tooltips, perspective);
        tooltips.add(Localization.translate("global", "aphorea"));
        return tooltips;
    }

    abstract public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective);

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

        if (player.isServer() && abilityTicks != 0) {
            abilityCountTimer++;
            if (abilityCountTimer > getAbilityTicks(player)) {
                runServerAbility(player.getLevel(), item, player);
                abilityCountTimer = 0;
            }
        }
    }

    public void applyBuffs(Mob mob) {
        Buff buff = this.buff.apply(mob);
        if (buff != null) {
            if (mob.buffManager.hasBuff(buff.getID())) {
                Attacker attacker = mob.buffManager.getBuff(buff.getID()).getAttacker();
                if (attacker != null && attacker.getAttackOwner() != null) {
                    return;
                }
            }
            ActiveBuff ab = new ActiveBuff(buff, mob, 100, null);
            mob.buffManager.addBuff(ab, false);
        }
    }

    public void applyBuffs(Mob mob, PlayerMob player) {
        Buff buff = this.buff.apply(mob);
        if (buff != null) {
            if (mob.buffManager.hasBuff(buff.getID())) {
                ActiveBuff antBuff = mob.buffManager.getBuff(buff.getID());
                if(antBuff != null && antBuff.buff instanceof AphBannerBuff) {
                    AphBannerBuff bannerBuff = (AphBannerBuff) antBuff.buff;
                    if (bannerBuff.shouldRemove(antBuff) || bannerBuff.bannerEffect <= player.buffManager.getModifier(AphModifiers.BANNER_EFFECT)) {
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
        ActiveBuff ab = new ActiveBuff(buff, mob, 100, player);
        mob.buffManager.addBuff(ab, false, forceOverride);
    }

    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
    }

    public int getPlayerRange() {
        return range * 2;
    }
}