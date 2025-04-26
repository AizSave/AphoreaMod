package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import aphorea.levelevents.AphNarcissistEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.util.HashMap;

public class TheNarcissist extends AphSwordToolItem {
    public TheNarcissist() {
        super(1000);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(25.0F).setUpgradedValue(1.0F, 90.0F);
        this.attackRange.setBaseValue(80);
        this.knockback.setBaseValue(100);
        this.resilienceGain.setBaseValue(1.0F);
        this.attackXOffset = 8;
        this.attackYOffset = 8;
    }

    public float getHitboxSwingAngleOffset(InventoryItem item, int dir, float swingAngle) {
        return 0.0F;
    }

    @Override
    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return 180;
    }

    @Override
    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return 180;
    }

    public boolean getAnimInverted(InventoryItem item) {
        return getCombo(item) == 1;
    }

    public int getFlatItemCooldownTime(InventoryItem item) {
        return item.getGndData().getInt("lastCombo") == 2 ? 2000 : this.getFlatAttackAnimTime(item) * 2;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int combo = getComboAndCalc(item, attackerMob);
        item.getGndData().setInt("lastCombo", combo);

        int animTime = this.getAttackAnimTime(item, attackerMob);
        if (combo == 2) {
            if (level.isServer()) {
                attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff("narcissist"), attackerMob, animTime, null), true);
                attackerMob.getLevel().entityManager.addLevelEvent(new AphNarcissistEvent(attackerMob, (float) Math.atan2(y - attackerMob.y, x - attackerMob.x), attackHeight, this.getAttackDamage(item)));

            }

        } else {
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime, combo == 1 ? new HashMap<>() : null);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }

        return item;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "thenarcissist"));
        return tooltips;
    }

    private static final int MAX_COMBO = 2;
    private static final long COMBO_TIMEOUT = 6L;

    public int getCombo(InventoryItem item) {
        return item.getGndData().getInt("combo") - 1;
    }

    public int getComboAndCalc(InventoryItem item, ItemAttackerMob attackerMob) {
        int combo = item.getGndData().getInt("combo");
        int returnValue;
        if (combo == 0) {
            item.getGndData().setInt("combo", 1);
            returnValue = 0;
        } else {
            if (item.getGndData().getBoolean("mobAttacked") && (item.getGndData().getLong("lastAttack") + getAttackAnimTime(item, attackerMob) * COMBO_TIMEOUT) > attackerMob.getTime()) {
                item.getGndData().setInt("combo", combo == MAX_COMBO ? 0 : combo + 1);
                returnValue = combo;
            } else {
                item.getGndData().setInt("combo", 1);
                returnValue = 0;
            }
        }
        item.getGndData().setLong("lastAttack", attackerMob.getTime());
        item.getGndData().setBoolean("mobAttacked", false);
        return returnValue;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        item.getGndData().setBoolean("mobAttacked", true);
        attacker.getServer().network.sendToClientsAtEntireLevel(new NarcissistHitMob(attacker), level);
    }

    public static class NarcissistHitMob extends Packet {
        public final int mobUniqueID;

        public NarcissistHitMob(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            mobUniqueID = reader.getNextInt();
        }

        public NarcissistHitMob(Mob mob) {
            this.mobUniqueID = mob.getUniqueID();
            PacketWriter writer = new PacketWriter(this);
            writer.putNextInt(mobUniqueID);
        }

        @Override
        public void processClient(NetworkPacket packet, Client client) {
            Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (mob instanceof ItemAttackerMob) {
                InventoryItem item = ((ItemAttackerMob) mob).getCurrentSelectedAttackSlot().getItem();
                if (item.item instanceof TheNarcissist) {
                    item.getGndData().setBoolean("mobAttacked", true);
                }
            }
        }
    }
}
