package aphorea.items.tools.weapons.magic;

import aphorea.projectiles.toolitem.UnstableGelProjectile;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public class UnstableGelStaff extends AphMagicProjectileSecondaryAreaToolItem implements ItemInteractAction {

    public UnstableGelStaff() {
        super(400, 800, 6.0F);
        rarity = Rarity.COMMON;
        attackAnimTime.setBaseValue(800);
        attackDamage.setBaseValue(30).setUpgradedValue(1, 80);
        velocity.setBaseValue(100);
        knockback.setBaseValue(0);
        attackRange.setBaseValue(600);

        manaCost.setBaseValue(3.0F);

        attackXOffset = 12;
        attackYOffset = 22;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "unstablegelstaff"));
        tooltips.add(Localization.translate("itemtooltip", "stikybuff2"));
        tooltips.add(Localization.translate("itemtooltip", "areasecondaryattack", "mana", getSecondaryManaCost(item)));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
        AphAreaList.addAreasStatTip(list, getAreaList(perspective, currentItem), lastItem == null ? null : getAreaList(perspective, lastItem), perspective, forceAdd, currentItem, lastItem, this);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.slimeSplash1, SoundEffect.effect(attackerMob)
                    .volume(0.7f)
                    .pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = new UnstableGelProjectile(
                level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                getProjectileVelocity(item, attackerMob),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, attackerMob), 0, seed
        );
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 2000, null), true);
    }

    @Override
    public AphAreaList getAreaList(ItemAttackerMob attackerMob, InventoryItem item) {
        return new AphAreaList(
                new AphArea(200, AphColors.unstableGel).setDamageArea(getAttackDamage(item).modDamage(0.7F))
        );
    }
}
