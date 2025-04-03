package aphorea.items.tools.weapons.range.sabergun.logic;

import aphorea.items.tools.weapons.melee.saber.logic.SaberDashLevelEvent;
import aphorea.items.tools.weapons.melee.saber.logic.SaberJumpLevelEvent;
import aphorea.items.tools.weapons.range.sabergun.AphSaberGunToolItem;
import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.HudDrawElement;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class SaberGunDashAttackHandler extends MousePositionAttackHandler {
    public int chargeTime;
    public boolean fullyCharged;
    public AphSaberGunToolItem saberItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public Color particleColors;
    public boolean endedByInteract;
    protected int endAttackBuffer;
    protected HudDrawElement hudDrawElement;

    public SaberGunDashAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSaberGunToolItem saberItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.saberItem = saberItem;
        this.chargeTime = chargeTime;
        this.particleColors = particleColors;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float) this.getTimeSinceStart() / (float) this.chargeTime;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.attackerMob.isClient() && this.hudDrawElement == null) {
            this.hudDrawElement = this.attackerMob.getLevel().hudManager.addElement(new HudDrawElement() {
                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    if (SaberGunDashAttackHandler.this.attackerMob.getAttackHandler() != SaberGunDashAttackHandler.this) {
                        this.remove();
                    } else {
                        float distance = SaberGunDashAttackHandler.this.getChargeDistance(SaberGunDashAttackHandler.this.getChargePercent());
                        if (distance > 0.0F) {
                            Point2D.Float dir = GameMath.normalize((float) SaberGunDashAttackHandler.this.lastX - SaberGunDashAttackHandler.this.attackerMob.x, (float) SaberGunDashAttackHandler.this.lastY - SaberGunDashAttackHandler.this.attackerMob.y);
                            final DrawOptions drawOptions = HUD.getArrowHitboxIndicator(SaberGunDashAttackHandler.this.attackerMob.x, SaberGunDashAttackHandler.this.attackerMob.y, dir.x, dir.y, (int) distance, 50, new Color(0, 0, 0, 0), new Color(220, 255, 255, 100), new Color(0, 0, 0, 100), camera);
                            list.add(new SortedDrawable() {
                                public int getPriority() {
                                    return 1000;
                                }

                                public void draw(TickManager tickManager) {
                                    drawOptions.draw();
                                }
                            });
                        }

                    }
                }
            });
        }

        float chargePercent = this.getChargePercent();
        if (!this.attackerMob.isPlayer && chargePercent >= 1.0F) {
            this.endAttackBuffer += this.updateInterval;
            if (this.endAttackBuffer >= 350) {
                this.endAttackBuffer = 0;
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }

        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        showItem.getGndData().setBoolean("charging", true);
        GNDItemMap attackMap = new GNDItemMap();
        this.attackerMob.showItemAttack(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
        if (this.attackerMob.isServer()) {
            if (this.attackerMob.isPlayer) {
                PlayerMob player = (PlayerMob) this.attackerMob;
                ServerClient client = player.getServerClient();
                this.attackerMob.getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(player, showItem, this.lastX, this.lastY, 0, this.seed, attackMap), this.attackerMob, client);
            } else {
                this.attackerMob.showItemAttackMobAbility.runAndSend(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
            }
        }

        if (chargePercent >= 1.0F && !this.fullyCharged) {
            this.fullyCharged = true;
            if (this.attackerMob.isClient()) {
                int particles = 35;
                float anglePerParticle = 360.0F / (float) particles;
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

                for (int i = 0; i < particles; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(this.particleColors).heightMoves(0.0F, 30.0F).lifeTime(500);
                }

                SoundManager.playSound(GameResources.magicbolt4, SoundEffect.effect(this.attackerMob).volume(0.1F).pitch(2.5F));
            }
        }

    }

    @Override
    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    @Override
    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (!this.endedByInteract && chargePercent >= 0.5F) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob) this.attackerMob).constantAttack = true;
            }

            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            Point2D.Float dir = GameMath.normalize((float) this.lastX - this.attackerMob.x, (float) this.lastY - this.attackerMob.y);
            chargePercent = Math.min(chargePercent, 1.0F);
            LevelEvent event;
            GameDamage baseDamage = this.saberItem.getAttackDamage(this.item);
            GameDamage damage = new GameDamage(DamageTypeRegistry.MELEE, baseDamage.damage * saberItem.getDashDamageMultiplier(item), baseDamage.armorPen, baseDamage.baseCritChance, baseDamage.playerDamageMultiplier, baseDamage.finalDamageMultiplier);
            if(attackerMob.buffManager.hasBuff("ninjascarf") && !attackerMob.getLevel().isTrialRoom) {
                event = new SaberJumpLevelEvent(this.attackerMob, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int) (200.0F * chargePercent), damage);
            } else {
                event = new SaberDashLevelEvent(this.attackerMob, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int) (200.0F * chargePercent), damage);
            }
            this.attackerMob.addAndSendAttackerLevelEvent(event);
            this.attackerMob.buffManager.addBuff(new ActiveBuff(AphBuffs.SABER_DASH_COOLDOWN, this.attackerMob, 3.0F, null), this.attackerMob.isServer());
        }

        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }

    }

    public float getChargeDistance(float chargePercent) {
        chargePercent = Math.min(chargePercent, 1.0F);
        return chargePercent > 0.5F ? (chargePercent - 0.5F) * 2.0F * (float) this.saberItem.dashRange.getValue(this.saberItem.getUpgradeTier(this.item)) : 0.0F;
    }
}
