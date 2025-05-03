package aphorea.items.tools.weapons.melee.rapier.logic;

import aphorea.items.tools.weapons.melee.rapier.AphRapierToolItem;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.HudDrawElement;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class RapierDashAttackHandler extends MousePositionAttackHandler {
    public int chargeTime;
    public AphRapierToolItem rapierItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public Color particleColors;
    public boolean endedByInteract;
    protected HudDrawElement hudDrawElement;

    public RapierDashAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphRapierToolItem rapierItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.rapierItem = rapierItem;
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
                    if (RapierDashAttackHandler.this.attackerMob.getAttackHandler() != RapierDashAttackHandler.this) {
                        this.remove();
                    } else {
                        float distance = RapierDashAttackHandler.this.getChargeDistance(RapierDashAttackHandler.this.getChargePercent());
                        if (distance > 0.0F) {
                            Point2D.Float dir = GameMath.normalize((float) RapierDashAttackHandler.this.lastX - RapierDashAttackHandler.this.attackerMob.x, (float) RapierDashAttackHandler.this.lastY - RapierDashAttackHandler.this.attackerMob.y);
                            final DrawOptions drawOptions = HUD.getArrowHitboxIndicator(RapierDashAttackHandler.this.attackerMob.x, RapierDashAttackHandler.this.attackerMob.y, dir.x, dir.y, (int) distance, 50, new Color(0, 0, 0, 0), new Color(220, 255, 255, 100), new Color(0, 0, 0, 100), camera);
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
        if (chargePercent >= 1.0F) {
            this.attackerMob.endAttackHandler(true);
            return;
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
            LevelEvent event = new RapierDashLevelEvent(this.attackerMob, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int) (200.0F * chargePercent), this.rapierItem.getAttackDamage(this.item).modDamage(rapierItem.getDashDamageMultiplier(item)));
            this.attackerMob.addAndSendAttackerLevelEvent(event);
        }

        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }

    }

    public float getChargeDistance(float chargePercent) {
        chargePercent = Math.min(chargePercent, 1.0F);
        return chargePercent > 0.5F ? (chargePercent - 0.5F) * 2.0F * (float) this.rapierItem.dashRange.getValue(this.rapierItem.getUpgradeTier(this.item)) : 0.0F;
    }
}
