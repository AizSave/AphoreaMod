package aphorea.mobs.summon;

import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMob;
import necesse.gfx.gameTexture.GameTexture;

public class UndeadSkeleton extends BabySkeletonMob {

    public int count;

    public static GameTexture texture;

    public UndeadSkeleton() {
        super();
    }

    @Override
    public void init() {
        super.init();
        count = 0;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        count++;

        if (count >= 200) {
            if (this.isFollowing()) {
                ((ItemAttackerMob) this.getFollowingMob()).serverFollowersManager.removeFollower(this, false, false);
            }
            this.remove();
        }
    }

}
