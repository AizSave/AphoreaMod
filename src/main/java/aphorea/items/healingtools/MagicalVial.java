package aphorea.items.healingtools;

import aphorea.other.area.AphoreaArea;
import aphorea.other.area.AphoreaAreaList;
import aphorea.other.magichealing.AphoreaMagicHealing;
import aphorea.other.itemtype.healing.AphoreaMagicHealingToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

import java.awt.*;

public class MagicalVial extends AphoreaMagicHealingToolItem {

    static AphoreaAreaList area = new AphoreaAreaList(
            new AphoreaArea(400, new Color(255, 0, 0))
    );


    Mob actualMob = null;
    int count;
    int particlesAreaCount = 0;
    int particleCount = 0;

    public MagicalVial() {
        super(200);
        this.rarity = Rarity.UNCOMMON;
        magicHealing.setBaseValue(18)
                .setUpgradedValue(1.0F, 20);

        this.setItemCategory("equipment", "tools", "healing");
        this.setItemCategory(ItemCategory.equipmentManager, "tools", "healingtools");

        attackDamage.setBaseValue(1)
                .setUpgradedValue(1, 2);
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, Mob mob) {
        return 500;
    }

    @Override
    public int getItemCooldownTime(InventoryItem item, Mob mob) {
        return 20000;
    }

    @Override
    public boolean onMouseHoverMob(InventoryItem item, GameCamera camera, PlayerMob perspective, Mob mob, boolean isDebug) {
        boolean canHealMob = AphoreaMagicHealing.canHealMob(perspective, mob);
        boolean inInDistance = perspective.getPositionPoint().distance(mob.x, mob.y) <= 400;
        if (canHealMob && inInDistance) {
            actualMob = mob;
            count = 0;
        } else if (actualMob != null) {
            if (count > 100) {
                actualMob = null;
            } else {
                count++;
            }
        }
        if(canHealMob && !perspective.isItemOnCooldown(this) && !inInDistance) {
            if (particlesAreaCount >= 3) {
                particlesAreaCount = 0;
                area.showAllAreaParticles(perspective, perspective.x, perspective.y, 1, 0.5F, 0, (int) (Math.random() * 200) + 400);
            } else {
                particlesAreaCount++;
            }
        }
        return false;
    }

    @Override
    public void onMouseHoverTile(InventoryItem item, GameCamera camera, PlayerMob perspective, int mouseX, int mouseY, TilePosition pos, boolean isDebug) {
        if (actualMob != null) {
            if (count > 100) {
                actualMob = null;
            } else {
                count++;
            }
        }
        super.onMouseHoverTile(item, camera, perspective, mouseX, mouseY, pos, isDebug);
    }

    @Override
    public boolean onMouseHoverPickup(InventoryItem item, GameCamera camera, PlayerMob perspective, PickupEntity pickupEntity, boolean isDebug) {
        if (actualMob != null) {
            if (count > 100) {
                actualMob = null;
            } else {
                count++;
            }
        }
        return super.onMouseHoverPickup(item, camera, perspective, pickupEntity, isDebug);
    }

    @Override
    public void tickHolding(InventoryItem item, PlayerMob player) {
        super.tickHolding(item, player);
        if (player.isClient() && !player.isItemOnCooldown(this)) {
            if (actualMob != null && (player.getPositionPoint().distance(actualMob.x, actualMob.y) > 400 || actualMob.getHealthPercent() == 1)) {
                actualMob = null;
            }
            Mob target = actualMob == null ? player : actualMob;
            if (AphoreaMagicHealing.canHealMob(player, target)) {
                particleCount++;
                if (particleCount >= 80) {
                    particleCount = 0;
                }
                circleParticle(player, target);
            }
        }
    }

    public void circleParticle(PlayerMob perspective, Mob target) {
        float d = (target.getSelectBox().height + target.getSelectBox().width) * 0.55F;

        int particles = (int) (Math.PI * d / 2);
        for (int i = 0; i < particles; i++) {
            float angle = (float) i / particles * 240 + 9 * particleCount;
            float dx = (float) Math.sin(Math.toRadians(angle)) * d;
            float dy = (float) Math.cos(Math.toRadians(angle)) * d;
            perspective.getLevel().entityManager.addParticle(target.x + dx, target.y + dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(0, 0, 0).color(new Color(255, 0, 0)).heightMoves(10, 10).lifeTime(160);
        }
    }

    @Override
    public String canAttack(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        Mob target = actualMob == null ? player : actualMob;

        return AphoreaMagicHealing.canHealMob(player, target) ? null : Localization.translate("message", "cantheal");
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        this.animInverted = actualMob == null;
        onItemUsed(player, item);
        if (level.isServer()) {
            healMob(player, actualMob == null ? player : actualMob, item);
            actualMob = null;
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, AttackAnimMob mob, int attackHeight, InventoryItem item, int seed, PacketReader contentReader) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.drink, SoundEffect.effect(mob));
        }
    }

    @Override
    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "magicalvial", "healing", AphoreaMagicHealing.getMagicHealingToolTip(perspective, magicHealing.getValue(item.item.getUpgradeTier(item)), this, item)));
        return tooltips;
    }


    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        int healing = AphoreaMagicHealing.getMagicHealing(perspective, null, magicHealing.getValue(currentItem.item.getUpgradeTier(currentItem)), this, currentItem);
        DoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", healing, 0);
        if (lastItem != null) {
            int lastHealing = AphoreaMagicHealing.getMagicHealing(perspective, null, magicHealing.getValue(lastItem.item.getUpgradeTier(lastItem)), this, lastItem);
            tip.setCompareValue(lastHealing);
        }
        list.add(100, tip);
    }
}
