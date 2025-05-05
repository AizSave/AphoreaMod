package aphorea.items.tools.weapons.magic;

import aphorea.AphResources;
import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.projectiles.toolitem.MusicalNoteProjectile;
import aphorea.registry.AphDamageType;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphAreaType;
import aphorea.utils.magichealing.AphMagicHealingFunctions;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

import java.util.HashSet;
import java.util.Set;

public class HarpOfArmony extends AphMagicProjectileToolItem implements ItemInteractAction {
    protected FloatUpgradeValue attackDamage2 = new FloatUpgradeValue(0.0F, 0.2F);
    protected FloatUpgradeValue healing = new FloatUpgradeValue(0, 0.2F);

    public HarpOfArmony() {
        super(1000);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(500);

        manaCost.setBaseValue(2.0F);
        attackRange.setBaseValue(500);

        this.attackXOffset = 22;
        this.attackYOffset = 22;

        attackDamage.setBaseValue(30).setUpgradedValue(1, 60);
        attackDamage2.setBaseValue(3).setUpgradedValue(1, 6);
        healing.setBaseValue(3).setUpgradedValue(1, 6);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        AphAreaList areaList = getAreaList(item);

        Projectile projectile = new MusicalNoteProjectile(
                level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                getProjectileVelocity(item, attackerMob),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, attackerMob)
        );
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);

        if (areaList.someType(AphAreaType.HEALING)) {
            onHealingToolItemUsed(attackerMob, item);
        }

        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);
        areaList.execute(attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, this, true);

        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            float distance = attackerMob.getDistance(x, y);
            GameSound[] notes = AphResources.SOUNDS.HARP.All;
            int noteIndex = Math.min(notes.length - 1, (int) (distance / (400F / notes.length)));
            SoundManager.playSound(notes[noteIndex], SoundEffect.effect(attackerMob));
        }
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "harpofharmony"));
        tooltips.add(Localization.translate("itemtooltip", "inspiration"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
        AphAreaList.addAreasStatTip(list, getAreaList(currentItem), lastItem == null ? null : getAreaList(lastItem), perspective, forceAdd, currentItem, lastItem, this);
    }

    public void onHealingToolItemUsed(Mob mob, InventoryItem item) {
        mob.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingFunctions).forEach(buff -> ((AphMagicHealingFunctions) buff.buff).onMagicalHealingItemUsed(mob, this, item));

        if (this instanceof AphMagicHealingFunctions) {
            ((AphMagicHealingFunctions) this).onMagicalHealingItemUsed(mob, this, item);
        }
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        Set<Integer> enchantments = new HashSet<>();
        enchantments.addAll(EnchantmentRegistry.magicItemEnchantments);
        enchantments.addAll(AphEnchantments.healingItemEnchantments);
        enchantments.addAll(AphEnchantments.areaItemEnchantments);

        return enchantments;
    }

    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(
                new AphArea(200, 0.3F, AphColors.spinel)
                        .setDamageArea(new GameDamage(AphDamageType.INSPIRATION, attackDamage2.getValue(item.item.getUpgradeTier(item))))
                        .setHealingArea((int) (float) healing.getValue(item.item.getUpgradeTier(item)))
                        .setBuffArea(5000, "harmonybuff")
        );
    }
}
