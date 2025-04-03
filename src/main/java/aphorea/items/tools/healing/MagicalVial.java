package aphorea.items.tools.healing;

import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.level.maps.Level;

public class MagicalVial extends AphMagicHealingToolItem {

    static AphAreaList area = new AphAreaList(
            new AphArea(400, AphColors.blood)
    );

    int particlesAreaCount = 0;
    int particleCount = 0;

    public MagicalVial() {
        super(200);
        this.rarity = Rarity.COMMON;
        magicHealing.setBaseValue(30)
                .setUpgradedValue(1.0F, 35);

        this.setItemCategory("equipment", "tools", "healing");
        this.setItemCategory(ItemCategory.equipmentManager, "tools", "healingtools");

        attackDamage.setBaseValue(1)
                .setUpgradedValue(1, 2);
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 500;
    }

    @Override
    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
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
                area.executeClient(perspective.getLevel(), perspective.x, perspective.y, 1, 0.5F, 0, (int) (Math.random() * 200) + 400);
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
            perspective.getLevel().entityManager.addParticle(target.x + dx, target.y + dy, new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC).next()).movesFriction(0, 0, 0).color(AphColors.blood).heightMoves(10, 10).lifeTime(160);
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob mob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if(mob.isPlayer) {
            PlayerMob player = (PlayerMob) mob;

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
        }

        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob mob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.drink, SoundEffect.effect(mob));
        }
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        AphMagicHealing.addMagicHealingTip(this, list, currentItem, lastItem, perspective);
    }

    @Override
    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "magicalvial"));
        tooltips.add(Localization.translate("itemtooltip", "magicalvial2"));
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