package aphorea.items.tools.weapons.melee.rapier;

import aphorea.items.tools.weapons.melee.rapier.logic.RapierDashAttackHandler;
import aphorea.items.vanillaitemtypes.weapons.AphSpearToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.utils.AphColors;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
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
import necesse.inventory.InventoryItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class AphRapierToolItem extends AphSpearToolItem {
    public IntUpgradeValue dashRange;
    public IntUpgradeValue dashAnimTime;

    public AphRapierToolItem(int enchantCost) {
        super(enchantCost);
        this.keyWords.add("rapier");

        attackAnimTime.setBaseValue(100);
        knockback.setBaseValue(20);

        this.dashRange = new IntUpgradeValue(200, 0.0F);
        this.dashRange.setBaseValue(200);

        this.dashAnimTime = new IntUpgradeValue(1000, 0.0F);
        this.dashAnimTime.setBaseValue(600);

    }

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        GameSprite attackSprite = this.getAttackSprite(item, perspective);
        return attackSprite != null && Math.max(attackSprite.width, attackSprite.height) >= 32 ? attackSprite : this.getItemSprite(item, perspective);
    }

    @Override
    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<>();
        float attackRange = (float) this.getAttackRange(item);
        Point2D.Float dir = GameMath.normalize((float) aimX, (float) aimY);
        float yOffset = (float) Math.min(mob.getCurrentAttackDrawYOffset() + mob.getStartAttackHeight(), 0);
        Line2D.Float attackLine = new Line2D.Float(mob.x, mob.y, mob.x + dir.x * attackRange, mob.y + dir.y * attackRange + yOffset);
        if (this.width > 0.0F) {
            out.add(new LineHitbox(attackLine, this.width));
        } else {
            out.add(attackLine);
        }

        return out;
    }

    public int getDashAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("chargeAnimTime") ? gndData.getInt("chargeAnimTime") : this.dashAnimTime.getValue(this.getUpgradeTier(item));
    }

    public float getDashDamageMultiplier(InventoryItem item) {
        return 5;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int combo = getComboAndCalc(item, attackerMob);
        if (combo == 19) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, attackerMob, 0.15F, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();

            if (attackerMob.isServer()) {
                int strength = 100;
                Point2D.Float dir = GameMath.normalize(attackerMob.x - x, attackerMob.y - y);
                level.getServer().network.sendToClientsAtEntireLevel(new AphCustomPushPacket(attackerMob, dir.x, dir.y, (float) strength), level);
            }

            int animTime = (int) ((float) this.getDashAnimTime(item, attackerMob));
            mapContent.setBoolean("charging", true);
            attackerMob.startAttackHandler((new RapierDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed)));

            return item;
        } else {
            item.getGndData().setBoolean("charging", false);

            float dx = x - attackerMob.x;
            float dy = y - attackerMob.y;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            float angle = (float) Math.atan2(dy, dx);

            float angleOffset = (float) Math.toRadians(GameRandom.globalRandom.getFloatOffset(0, 15));
            angle += angleOffset;

            int aimX = (int) (attackerMob.x + Math.cos(angle) * distance);
            int aimY = (int) (attackerMob.y + Math.sin(angle) * distance);

            float dirX = aimX - attackerMob.x;
            float dirY = aimY - attackerMob.y;
            float magnitude = (float) Math.sqrt(dirX * dirX + dirY * dirY);

            if (magnitude != 0) {
                dirX /= magnitude;
                dirY /= magnitude;
            }

            item.getGndData().setFloat("attackDirX", dirX);
            item.getGndData().setFloat("attackDirY", dirY);


            return super.onAttack(level, aimX, aimY, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        }
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("charged") && !item.getGndData().getBoolean("charging")) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (item.getGndData().getBoolean("charging")) {
            drawOptions.pointRotation(attackDirX, attackDirY);
            drawOptions.thrustOffsets(0, 0, attackProgress);
        } else {
            float newAttackDirX = item.getGndData().getFloat("attackDirX");
            float newAttackDirY = item.getGndData().getFloat("attackDirY");
            drawOptions.pointRotation(newAttackDirX, newAttackDirY);
            drawOptions.thrustOffsets(newAttackDirX, newAttackDirY, attackProgress);
        }
    }

    private static final int MAX_COMBO = 20;
    private static final long COMBO_TIMEOUT = 4L;

    public int getComboAndCalc(InventoryItem item, ItemAttackerMob attackerMob) {
        int combo = item.getGndData().getInt("combo");
        int returnValue;
        if (combo == 0) {
            item.getGndData().setInt("combo", 1);
            returnValue = 0;
        } else {
            if (item.getGndData().getLong("lastAttack") + getAttackAnimTime(item, attackerMob) * COMBO_TIMEOUT > attackerMob.getTime()) {
                item.getGndData().setInt("combo", combo == MAX_COMBO ? 0 : combo + 1);
                returnValue = combo;
            } else {
                item.getGndData().setInt("combo", 1);
                returnValue = 0;
            }
        }
        item.getGndData().setLong("lastAttack", attackerMob.getTime());
        return returnValue;
    }


}
