package aphorea.items.tools.weapons.magic;

import aphorea.items.vanillaitemtypes.AphToolItem;
import aphorea.packets.AphCustomPushPacket;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

public class MagicalBroom extends AphToolItem {

    public static GameTexture worldTexture;

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        return new GameSprite(worldTexture);
    }

    int currentA;

    public MagicalBroom() {
        super(650);
        this.setItemCategory("equipment", "weapons", "magicweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "magicweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "magicweapons");
        this.keyWords.add("broom");
        damageType = DamageTypeRegistry.MAGIC;
        this.width = 15.0F;
        this.showAttackAllDirections = true;
        this.resilienceGain.setBaseValue(2.0F);

        rarity = Rarity.COMMON;
        attackAnimTime.setBaseValue(300);
        attackDamage.setBaseValue(26)
                .setUpgradedValue(1, 82);
        attackRange.setBaseValue(160);
        knockback.setBaseValue(250);
        manaCost.setBaseValue(1.0F);

        attackYOffset = 155; // 140
        attackXOffset = 30; // 30

        currentA = 0;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addKnockbackTip(list, currentItem, lastItem, perspective);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public int getFlatItemCooldownTime(InventoryItem item) {
        return (int) ((float) this.getFlatAttackAnimTime(item) * 1.5F);
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {

        int n;
        if (attackProgress < 0.3F) {
            n = currentA == 0 ? 9 : 4;
        } else if (attackProgress < 0.45F) {
            n = currentA == 0 ? 8 : 3;
        } else if (attackProgress < 0.55F) {
            n = currentA == 0 ? 7 : 2;
        } else if (attackProgress < 0.7F) {
            n = currentA == 0 ? 6 : 1;
        } else {
            n = currentA == 0 ? 5 : 0;
        }

        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(attackTexture, n, 0, 320);

        itemSprite.itemRotatePoint(options.dir == 2 ? this.attackXOffset + 25 : this.attackXOffset, options.dir == 2 ? this.attackYOffset - 5 : this.attackYOffset);
        if (itemColor != null) {
            itemSprite.itemColor(itemColor);
        }

        if (options.dir == 0 || options.dir == 2) {
            itemSprite.itemRotateOffset(-45);
        }

        return itemSprite.itemEnd();
    }

    public Function<Float, Float> getSwingDirection(InventoryItem item, AttackAnimMob mob) {
        int attackDir = mob.getDir();
        float animSwingAngle = 180.0F;
        float animSwingAngleOffset = 0.0F;
        Function<Float, Float> angleGetter;
        if (attackDir == 0) {
            if (this.getAnimInverted(item)) {
                angleGetter = (progress) -> -progress * animSwingAngle - animSwingAngleOffset;
            } else {
                angleGetter = (progress) -> 180.0F + progress * animSwingAngle + animSwingAngleOffset;
            }
        } else if (attackDir == 1) {
            if (this.getAnimInverted(item)) {
                angleGetter = (progress) -> 90.0F - progress * animSwingAngle - animSwingAngleOffset;
            } else {
                angleGetter = (progress) -> 270.0F + progress * animSwingAngle + animSwingAngleOffset;
            }
        } else if (attackDir == 2) {
            if (this.getAnimInverted(item)) {
                angleGetter = (progress) -> 180.0F - progress * animSwingAngle - animSwingAngleOffset;
            } else {
                angleGetter = (progress) -> progress * animSwingAngle + animSwingAngleOffset;
            }
        } else if (this.getAnimInverted(item)) {
            angleGetter = (progress) -> 90.0F + progress * animSwingAngle + animSwingAngleOffset;
        } else {
            angleGetter = (progress) -> 270.0F - progress * animSwingAngle - animSwingAngleOffset;
        }

        return angleGetter;
    }

    @Override
    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<>();
        int attackRange = this.getAttackRange(item);
        float lastProgress = event.lastHitboxProgress;
        float nextProgress = mob.getAttackAnimProgress();
        float circumference = (float) (Math.PI * (double) attackRange);
        float percPerWidth = Math.max(10.0F, this.width) / circumference;
        Point2D.Float base = new Point2D.Float(mob.x, mob.y);
        int attackDir = mob.getDir();
        if (attackDir == 0) {
            base.x += 8.0F;
        } else if (attackDir == 2) {
            base.x -= 8.0F;
        }

        for (float progress = lastProgress; progress <= nextProgress; progress += percPerWidth) {
            float angle = this.getSwingDirection(item, mob).apply(progress);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            Line2D.Float attackLine = new Line2D.Float(base.x, base.y, dir.x * (float) attackRange + mob.x, dir.y * (float) attackRange + mob.y);
            if (this.width > 0.0F) {
                out.add(new LineHitbox(attackLine, this.width));
            } else {
                out.add(attackLine);
            }

            if (!forDebug) {
                event.lastHitboxProgress = progress;
            }
        }

        return out;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (animAttack == 0) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();

            if (attackerMob.isServer()) {
                int strength = 50;
                Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
                level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, dir.x, dir.y, (float) strength), level);
            } else if (attackerMob.isClient()) {
                currentA = currentA == 0 ? 1 : 0;
                animInverted = currentA == 1;
            }

            int animTime = this.getAttackAnimTime(item, attackerMob);
            int aimX = x - attackerMob.getX();
            int aimY = y - attackerMob.getY() + attackHeight;
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, aimX, aimY, animTime, animTime);
            level.entityManager.addLevelEventHidden(event);

            this.consumeMana(attackerMob, item);
        }

        return item;
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, EnchantmentRegistry.meleeItemEnchantments, this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.meleeItemEnchantments.contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.meleeItemEnchantments;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "broom");
    }
}
