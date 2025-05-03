package aphorea.items.tools.weapons.magic;

import aphorea.AphResources;
import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.projectiles.toolitem.MusicalNoteProjectile;
import aphorea.registry.AphDamageType;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphAreaType;
import aphorea.utils.area.AphFlatArea;
import aphorea.utils.magichealing.AphMagicHealingFunctions;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MagicHarp extends AphMagicProjectileToolItem implements ItemInteractAction {
    static int range = 200;
    static Color color = AphColors.spinel;

    static AphAreaList areaList = new AphAreaList(
            new AphFlatArea(range, 0.3F, color)
                    .setDamageArea(3)
                    .setHealingArea(3)
                    .setBuffArea(5000, "harpbuff")
    ).setDamageType(AphDamageType.INSPIRATION);

    public MagicHarp() {
        super(1000);
        rarity = Rarity.RARE;
        attackAnimTime.setBaseValue(500);

        manaCost.setBaseValue(2.0F);
        attackRange.setBaseValue(500);

        this.attackXOffset = 22;
        this.attackYOffset = 22;

        attackDamage.setBaseValue(30).setUpgradedValue(1, 60);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = new MusicalNoteProjectile(
                level, attackerMob,
                attackerMob.x, attackerMob.y,
                x, y,
                getProjectileVelocity(item, attackerMob),
                getAttackRange(item),
                getAttackDamage(item),
                getKnockback(item, attackerMob)
        );
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);

        level.entityManager.projectiles.addHidden(projectile);

        if (level.isServer()) {
            level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
        }

        if (areaList.someType(AphAreaType.HEALING)) {
            onHealingToolItemUsed(attackerMob, item);
        }

        if (this.getManaCost(item) > 0) {
            this.consumeMana(attackerMob, item);
        }

        float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);

        areaList.executeServer(attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, this);

        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            float rangeModifier = 1 + this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE);
            areaList.executeClient(level, attackerMob.x, attackerMob.y, rangeModifier);

            float distance = attackerMob.getDistance(x, y);
            GameSound[] notes = AphResources.SOUNDS.HARP.All;
            int noteIndex = Math.min(notes.length - 1, (int) (distance / (400F / notes.length)));
            SoundManager.playSound(notes[noteIndex], SoundEffect.effect(attackerMob));
        }
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        areaList.addAreasToolTip(tooltips, perspective, true, null, null);
        tooltips.add(Localization.translate("itemtooltip", "magicharp"));
        tooltips.add(Localization.translate("itemtooltip", "inspiration"));
        return tooltips;
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
}
