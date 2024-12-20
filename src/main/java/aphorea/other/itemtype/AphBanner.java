package aphorea.other.itemtype;

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

    private PlayerMob playerOwner = null;

    public AphBanner(Rarity rarity, int range, Function<Mob, Buff> buff, int abilityTicks) {
        super(rarity, range, buff);
        this.abilityTicks = abilityTicks;
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
        GameUtils.streamNetworkClients(player.getLevel()).filter((c) -> {
            return this.shouldBuffPlayer(item, player, c.playerMob);
        }).filter((c) -> {
            return GameMath.diagonalMoveDistance(player.getX(), player.getY(), c.playerMob.getX(), c.playerMob.getY()) <= (double)this.range;
        }).forEach((c) -> {
            this.applyBuffs(c.playerMob, player);
        });
        player.getLevel().entityManager.mobs.streamInRegionsInRange(player.x, player.y, this.range).filter((m) -> {
            return !m.removed();
        }).filter((m) -> {
            return this.shouldBuffMob(item, player, m);
        }).filter((m) -> {
            return GameMath.diagonalMoveDistance(player.getX(), player.getY(), m.getX(), m.getY()) <= (double)this.range;
        }).forEach((m) -> {
            this.applyBuffs(m, player);
        });

        this.playerOwner = player;
        if (player.isServer()) {
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
            if(mob.buffManager.hasBuff(buff.getID())) {
                Attacker attacker = mob.buffManager.getBuff(buff.getID()).getAttacker();
                if(attacker != null && attacker.getAttackOwner() != null) {
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
            if(mob.buffManager.hasBuff(buff.getID())) {
                Attacker attacker = mob.buffManager.getBuff(buff.getID()).getAttacker();
                if(attacker != null) {
                    Mob otherMob = attacker.getAttackOwner();
                    if(otherMob != player) {
                        if(otherMob != null && otherMob.buffManager.getModifier(AphModifiers.BANNER_EFFECT) >= player.buffManager.getModifier(AphModifiers.BANNER_EFFECT)) {
                            return;
                        }
                        mob.buffManager.removeBuff(buff.getID(), false);
                    }
                } else {
                    mob.buffManager.removeBuff(buff.getID(), false);
                }
            }
            ActiveBuff ab = new ActiveBuff(buff, mob, 100, player);
            mob.buffManager.addBuff(ab, false);
        }
    }


    abstract public void runServerAbility(Level level, InventoryItem item, PlayerMob player);
}