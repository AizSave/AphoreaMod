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
import necesse.entity.mobs.GameDamage;
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
        this.attackDamage.setBaseValue(30.0F).setUpgradedValue(1.0F, 60.0F);
        this.attackRange.setBaseValue(85);
        this.knockback.setBaseValue(100);
        this.resilienceGain.setBaseValue(1.0F);
        this.attackXOffset = 8;
        this.attackYOffset = 8;
    }

    @Override
    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return 180;
    }

    @Override
    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return 180;
    }

    @Override
    public boolean getAnimInverted(InventoryItem item) {
        return getCombo(item) == 1 || getCombo(item) == 3;
    }

    @Override
    public int getFlatItemCooldownTime(InventoryItem item) {
        return item.getGndData().getInt("lastCombo") == 4 ? 5000 : this.getFlatAttackAnimTime(item) * 2;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int combo = getComboAndCalc(item, attackerMob);
        item.getGndData().setInt("lastCombo", combo);

        int animTime = this.getAttackAnimTime(item, attackerMob);
        if (combo == 4) {
            if (level.isServer()) {
                attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff("narcissistbuff"), attackerMob, animTime, null), true);
                attackerMob.getLevel().entityManager.events.add(new AphNarcissistEvent(attackerMob, (float) Math.atan2(y - attackerMob.y, x - attackerMob.x), attackHeight, this.getDefaultAttackDamage(item)));
            }
            item.getGndData().setInt("lastCombo", 4);
        } else {
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime, combo == 1 ? new HashMap<>() : null);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }

        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient()) {
            int lastCombo = item.getGndData().getInt("lastCombo");
            if (0 < lastCombo && lastCombo < 4) {
                level.getClient().startCameraShake(attackerMob.x, attackerMob.y, 500, 40, (float) lastCombo / 2, (float) lastCombo / 4, true);
            }
        }
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "thenarcissist"));
        return tooltips;
    }

    private static final int MAX_COMBO = 4;
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

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        float modDamage = 1 + 0.2F * item.getGndData().getInt("lastCombo", 0);
        return super.getAttackDamage(item).modDamage(modDamage);
    }

    public GameDamage getDefaultAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item);
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
