package aphorea.registry;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class AphDamageType {
    public static DamageType INSPIRATION;

    public static void registerCore() {
        DamageTypeRegistry.registerDamageType("inspiration", INSPIRATION = new InspirationDamageType());
    }

    public static class InspirationDamageType extends DamageType {

        public InspirationDamageType() {
        }

        public Modifier<Float> getBuffDamageModifier() {
            return AphModifiers.INSPIRATION_DAMAGE;
        }

        public Modifier<Float> getBuffAttackSpeedModifier(Attacker attacker) {
            return null;
        }

        public Modifier<Float> getBuffCritChanceModifier() {
            return AphModifiers.INSPIRATION_CRIT_CHANCE;
        }

        public Modifier<Float> getBuffCritDamageModifier() {
            return AphModifiers.INSPIRATION_CRIT_DAMAGE;
        }

        public GameMessage getStatsText() {
            return new LocalMessage("stats", "inspiration_damage");
        }

        public DoubleItemStatTip getDamageTip(int damage) {
            return new LocalMessageDoubleItemStatTip("itemtooltip", "inspirationdamagetip", "value", damage, 0);
        }

        public String getSteamStatKey() {
            return "inspiration_damage_dealt";
        }
    }
}
