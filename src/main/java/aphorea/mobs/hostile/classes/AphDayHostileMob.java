package aphorea.mobs.hostile.classes;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.level.maps.levelBuffManager.LevelModifiers;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

abstract public class AphDayHostileMob extends HostileMob {

    public boolean onlyDay = true;

    public AphDayHostileMob(int health) {
        super(health);
        spawnLightThreshold = new ModifierValue<>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 150);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        if (attacker != null && attacker.getAttackOwner() != null) {
            if (Arrays.stream(attackers.toArray()).noneMatch((a -> ((Attacker) a).getAttackOwner() != null && ((Attacker) a).getAttackOwner().isPlayer))) {
                this.dropsLoot = false;
            }
        }
        super.onDeath(attacker, attackers);
    }

    public static class CollisionOnlyPlayerChaserWandererAI<T extends Mob> extends SelectorAINode<T> {
        public final EscapeAINode<T> escapeAINode;
        public final CollisionOnlyPlayerChaserAI<T> collisionPlayerChaserAI;
        public final WandererAINode<T> wandererAINode;

        public CollisionOnlyPlayerChaserWandererAI(final Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
            this.addChild(this.escapeAINode = new EscapeAINode<T>() {
                public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                    if (mob.isHostile && !mob.isSummoned && mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
                        return true;
                    } else {
                        return shouldEscape != null && shouldEscape.get();
                    }
                }
            });
            this.addChild(this.collisionPlayerChaserAI = new CollisionOnlyPlayerChaserAI<>(searchDistance, damage, knockback));
            this.addChild(this.wandererAINode = new WandererAINode<>(wanderFrequency));
        }
    }

    public static class CollisionOnlyPlayerChaserAI<T extends Mob> extends CollisionChaserAI<T> {
        public CollisionOnlyPlayerChaserAI(int searchDistance, GameDamage damage, int knockback) {
            super(searchDistance, damage, knockback);
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
            return TargetFinderAINode.streamPlayers(mob, base, distance).map(playerMob -> playerMob);
        }
    }


}
