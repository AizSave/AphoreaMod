package aphorea.items.healingtools;

import aphorea.other.area.AphArea;
import aphorea.other.area.AphAreaList;
import aphorea.other.itemtype.healing.AphMagicHealingToolItem;
import aphorea.other.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
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

import java.awt.*;

public class MagicalVial extends AphMagicHealingToolItem {

    static AphAreaList area = new AphAreaList(
            new AphArea(400, new Color(255, 0, 0))
    );

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
        boolean canHealMob = AphMagicHealing.canHealMob(perspective, mob);
        boolean inInDistance = perspective.getPositionPoint().distance(mob.x, mob.y) <= 400;
        if (canHealMob && inInDistance) {
            if (perspective.isClient() && !perspective.isItemOnCooldown(this)) {
                if (AphMagicHealing.canHealMob(perspective, mob)) {
                    particleCount++;
                    if (particleCount >= 80) {
                        particleCount = 0;
                    }
                    circleParticle(perspective, mob);
                }
            }
        }
        if (canHealMob && !perspective.isItemOnCooldown(this) && !inInDistance) {
            if (particlesAreaCount >= 3) {
                particlesAreaCount = 0;
                area.showAllAreaParticles(perspective, perspective.x, perspective.y, 1, 0.5F, 0, (int) (Math.random() * 200) + 400);
            } else {
                particlesAreaCount++;
            }
        }
        return false;
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
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        Mob target = GameUtils.streamNetworkClients(level).filter(c -> c.playerMob != null).map(c -> c.playerMob)
                .filter(m -> AphMagicHealing.canHealMob(player, m) && m.getDistance(x, y) / 32 <= 2)
                .findFirst().orElse(null);

        if (target == null) {
            target = level.entityManager.mobs.getInRegionByTileRange(x / 32, y / 32, 2).stream()
                    .filter(m -> AphMagicHealing.canHealMob(player, m))
                    .findFirst().orElse(null);
        }

        if (level.isServer()) {
            healMob(player, target == null ? player : target, item);
        }

        this.animInverted = target == null;

        onHealingToolItemUsed(player, item);

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
        tooltips.add(Localization.translate("itemtooltip", "magicalvial", "healing", AphMagicHealing.getMagicHealingToolTip(perspective, magicHealing.getValue(item.item.getUpgradeTier(item)), item, this)));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        int healing = AphMagicHealing.getMagicHealing(perspective, null, magicHealing.getValue(currentItem.item.getUpgradeTier(currentItem)), this, currentItem);
        DoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", healing, 0);
        if (lastItem != null) {
            int lastHealing = AphMagicHealing.getMagicHealing(perspective, null, magicHealing.getValue(lastItem.item.getUpgradeTier(lastItem)), this, lastItem);
            tip.setCompareValue(lastHealing);
        }
        list.add(100, tip);
    }

}