package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.logic.SaberAttackHandler;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.ui.SaberAttackUIManger;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;

public class CrimsonKora extends AphSaberToolItem {

    public CrimsonKora() {
        super(800);
        rarity = Rarity.EPIC;
        attackDamage.setBaseValue(70).setUpgradedValue(1, 70);
        knockback.setBaseValue(200);

        this.attackRange.setBaseValue(80);

        chargeAnimTime.setBaseValue(500);

        attackXOffset = 10;
        attackYOffset = 10;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent) {
        return new AircutProjectile.CrimsonAircutProjectile(level, attackerMob, x, y, targetX, targetY,
                300 * powerPercent,
                (int) (400 * powerPercent),
                this.getAttackDamage(item).modDamage(item.getGndData().getFloat("modifyDamage", 1F)).modDamage(powerPercent),
                (int) (getKnockback(item, attackerMob) * powerPercent)
        );
    }

    @Override
    public void superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        float chargePercent = chargePercent(item);

        if (chargePercent >= 0.5F && item.getGndData().getBoolean("doDash")) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();

            if (attackerMob.isServer()) {
                int strength = (int) (200 * chargePercent(item));
                Point2D.Float dir = GameMath.normalize((float) x - attackerMob.x, (float) y - attackerMob.y);
                level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, dir.x, dir.y, (float) strength, AphColors.crimson_kora_dark), level);
            }
        }

        super.superOnAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getChargeAnimTime(item, attackerMob);
        item.getGndData().setBoolean("charging", false);
        item.getGndData().setFloat("modifyDamage", 1F);
        item.getGndData().setBoolean("doDash", false);
        attackerMob.startAttackHandler((new SaberAttackHandler(attackerMob, slot, item, this, animTime, false, seed)));
        return item;

    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient() && item.getGndData().getBoolean("charged") && !item.getGndData().getBoolean("charging") && item.getGndData().getFloat("modifyDamage", 1F) == 2F) {
            float chargePercent = chargePercent(item);
            level.getClient().startCameraShake(attackerMob.x, attackerMob.y, (int) (1000 * chargePercent), 40, 3.0F * chargePercent, 3.0F * chargePercent, true);
            if (chargePercent > 0.6F) {
                SoundManager.playSound(GameResources.electricExplosion, SoundEffect.effect(attackerMob).volume(chargePercent - 0.5F));
            }
        }
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = this.getChargeAnimTime(item, attackerMob) * 2;
        item.getGndData().setBoolean("charging", false);
        item.getGndData().setFloat("modifyDamage", 2F);
        item.getGndData().setBoolean("doDash", true);
        attackerMob.startAttackHandler((new CrimsonKoraGrandAttackHandler(attackerMob, slot, item, this, animTime, false, seed)).startFromInteract());
        return item;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding();
    }

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item).modDamage(item.getGndData().getFloat("modifyDamage", 1F));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate("itemtooltip", "crimsonkora"));
        return tooltips;
    }

    public static class CrimsonKoraGrandAttackHandler extends SaberAttackHandler {
        ParticleTypeSwitcher spinningTypeSwitcher;

        public CrimsonKoraGrandAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSaberToolItem toolItem, int chargeTime, boolean isAuto, int seed) {
            super(attackerMob, slot, item, toolItem, chargeTime, isAuto, seed);
            this.spinningTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.COSMETIC);
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            float chargePercent = SaberAttackUIManger.barPercent(this.getChargePercent());
            attackerMob.getLevel().lightManager.refreshParticleLightFloat(attackerMob.x, attackerMob.y, AphColors.crimson_kora_light, 1F, 50 + (int) (150 * chargePercent));

            if (attackerMob.isClient()) {
                for (int i = 0; i < (3 * chargePercent); ++i) {
                    attackerMob.getLevel().entityManager.addParticle(
                                    attackerMob.x + GameRandom.globalRandom.floatGaussian() * 4,
                                    attackerMob.y + GameRandom.globalRandom.floatGaussian() * 4,
                                    this.spinningTypeSwitcher.next()
                            ).movesFriction(
                                    GameRandom.globalRandom.floatGaussian() * 4 + attackerMob.dx * 0.1F,
                                    GameRandom.globalRandom.floatGaussian() * 4 + attackerMob.dy * 0.1F,
                             0.5F
                            )
                            .heightMoves(12 + GameRandom.globalRandom.floatGaussian() * 4, 2 + GameRandom.globalRandom.floatGaussian() * 2)
                            .color(AphColors.crimson_kora);
                }

                attackerMob.getLevel().getClient().startCameraShake(attackerMob.x, attackerMob.y, 100, 40, 0.3F * chargePercent + 0.2F, 0.5F * chargePercent, true);
            }


        }
    }
}
