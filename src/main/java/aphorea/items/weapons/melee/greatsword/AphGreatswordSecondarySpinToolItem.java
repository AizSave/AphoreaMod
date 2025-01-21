package aphorea.items.weapons.melee.greatsword;

import aphorea.utils.customchargeattacks.AphGreatswordCustomChargeToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

abstract public class AphGreatswordSecondarySpinToolItem extends AphGreatswordCustomChargeToolItem implements ItemInteractAction {
    boolean secondary;
    Color spinAttackColor;

    public AphGreatswordSecondarySpinToolItem(int enchantCost, int attackAnimTime, GreatswordChargeLevel[] chargeLevels, Color spinAttackColor) {
        super(enchantCost, chargeLevels);

        this.attackAnimTime.setBaseValue(attackAnimTime);
        this.spinAttackColor = spinAttackColor;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "spinsecondaryattack"));
        return tooltips;
    }

    @Override
    public float getSwingRotationOffset(InventoryItem item, int dir, float swingAngle) {
        float offset = super.getSwingRotationOffset(item, dir, swingAngle);
        if (secondary) {
            if(dir == 1 || dir == 3) {
                offset -= 180;
            } else {
                offset -= 90;
            }
        }
        return offset;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.isAttacking = true;
        secondary = false;
        return super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
    }

    @Override
    public int getLevelInteractAttackAnimTime(InventoryItem item, PlayerMob player) {
        return 500;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.isRiding() && !player.isAttacking && !player.buffManager.hasBuff(AphBuffs.SPIN_ATTACK_COOLDOWN);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        player.isAttacking = true;
        secondary = true;
        player.startAttackHandler((new GreatswordSecondarySpinAttackHandler<>(player, slot, item, this, 2000, spinAttackColor, seed)).startFromInteract());
        return item;
    }

    @Override
    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return secondary ? 360.0F : 150.0F;
    }

    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return secondary ? 360.0F : 150.0F;
    }

    @Override
    public void endChargeAttack(PlayerMob player, Point2D.Float dir, int charge) {
        int strength = 200;
        AphCustomPushPacket.applyToPlayer(player.getLevel(), player, dir.x, dir.y, (float) strength);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, player, 0.15F, null), player.getLevel().isServer());
        player.buffManager.addBuff(new ActiveBuff(AphBuffs.SPIN_ATTACK_COOLDOWN, player, 3.0F, null), player.getLevel().isServer());
        player.buffManager.forceUpdateBuffs();

        super.endChargeAttack(player, dir, charge);
    }

    public static class GreatswordSecondarySpinAttackHandler<T extends AphGreatswordSecondarySpinToolItem> extends MousePositionAttackHandler {
        public int chargeTime;
        public boolean fullyCharged;
        public T toolItem;
        public long startTime;
        public InventoryItem item;
        public int seed;
        public Color particleColors;
        public boolean endedByInteract;
        protected HudDrawElement hudDrawElement;

        public GreatswordSecondarySpinAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, T toolItem, int chargeTime, Color particleColors, int seed) {
            super(player, slot, 20);
            this.item = item;
            this.toolItem = toolItem;
            this.chargeTime = chargeTime;
            this.particleColors = particleColors;
            this.seed = seed;
            this.startTime = player.getWorldEntity().getLocalTime();
        }

        public long getTimeSinceStart() {
            return this.player.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return (float) this.getTimeSinceStart() / (float) this.chargeTime;
        }

        public void onUpdate() {
            super.onUpdate();
            if (this.player.isClient() && this.hudDrawElement == null) {
                this.hudDrawElement = this.player.getLevel().hudManager.addElement(new HudDrawElement() {
                    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                        if (GreatswordSecondarySpinAttackHandler.this.player.getAttackHandler() != GreatswordSecondarySpinAttackHandler.this) {
                            this.remove();
                        } else {
                            float distance = GreatswordSecondarySpinAttackHandler.this.getChargeDistance(GreatswordSecondarySpinAttackHandler.this.getChargePercent());
                            if (distance > 0.0F) {
                                Point2D.Float dir = GameMath.normalize((float) GreatswordSecondarySpinAttackHandler.this.lastX - GreatswordSecondarySpinAttackHandler.this.player.x, (float) GreatswordSecondarySpinAttackHandler.this.lastY - GreatswordSecondarySpinAttackHandler.this.player.y);
                                final DrawOptions drawOptions = HUD.getArrowHitboxIndicator(GreatswordSecondarySpinAttackHandler.this.player.x, GreatswordSecondarySpinAttackHandler.this.player.y, dir.x, dir.y, (int) distance, 50, new Color(0, 0, 0, 0), new Color(220, 255, 255, 100), new Color(0, 0, 0, 100), camera);
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
            InventoryItem showItem = this.item.copy();
            showItem.getGndData().setFloat("chargePercent", chargePercent);
            showItem.getGndData().setBoolean("chargeUp", true);
            showItem.getGndData().setBoolean("charging", true);
            Packet attackContent = new Packet();
            this.player.showAttack(showItem, this.lastX, this.lastY, this.seed, attackContent);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, showItem, this.lastX, this.lastY, this.seed, attackContent), this.player, client);
            }

            if (chargePercent >= 1.0F && !this.fullyCharged) {
                this.fullyCharged = true;
                if (this.player.isClient()) {
                    int particles = 35;
                    float anglePerParticle = 360.0F / (float) particles;
                    ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

                    for (int i = 0; i < particles; ++i) {
                        int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float) Math.sin(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float) Math.cos(Math.toRadians(angle)) * (float) GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                        this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(this.particleColors).heightMoves(0.0F, 30.0F).lifeTime(500);
                    }

                    SoundManager.playSound(GameResources.magicbolt4, SoundEffect.effect(this.player).volume(0.1F).pitch(2.5F));
                }
            }
        }

        public void onMouseInteracted(int levelX, int levelY) {
            this.endedByInteract = true;
            this.player.endAttackHandler(false);
        }

        public void onControllerInteracted(float aimX, float aimY) {
            this.endedByInteract = true;
            this.player.endAttackHandler(false);
        }

        public void onEndAttack(boolean bySelf) {
            float chargePercent = this.getChargePercent();
            if (!this.endedByInteract && chargePercent >= 1.0F) {
                this.player.constantAttack = true;
                Packet attackContent = new Packet();
                InventoryItem attackItem = this.item.copy();
                attackItem.getGndData().setBoolean("shouldFire", true);
                attackItem.getGndData().setInt("cooldown", this.toolItem.getAttackAnimTime(attackItem, this.player) + 100);
                this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), this.lastX, this.lastY, this.player, attackItem);
                this.player.showAttack(attackItem, this.lastX, this.lastY, this.seed, attackContent);
                if (this.player.isServer()) {
                    ServerClient client = this.player.getServerClient();
                    this.player.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketShowAttack(this.player, attackItem, this.lastX, this.lastY, this.seed, attackContent), this.player, client);
                }

                this.toolItem.superOnAttack(this.player.getLevel(), this.lastX, this.lastY, this.player, this.player.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, new PacketReader(attackContent));

                for (ActiveBuff b : this.player.buffManager.getArrayBuffs()) {
                    b.onItemAttacked(this.lastX, this.lastY, this.player, this.player.getCurrentAttackHeight(), attackItem, this.slot, 0);
                }

                Point2D.Float dir = GameMath.normalize((float) this.lastX - this.player.x, (float) this.lastY - this.player.y);
                toolItem.endChargeAttack(player, dir, 0);
            }

            if (this.hudDrawElement != null) {
                this.hudDrawElement.remove();
            }

        }

        public float getChargeDistance(float chargePercent) {
            chargePercent = Math.min(chargePercent, 1.0F);
            return chargePercent * 200;
        }
    }
}
