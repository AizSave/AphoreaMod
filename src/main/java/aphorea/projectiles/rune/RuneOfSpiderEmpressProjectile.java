package aphorea.projectiles.rune;

import aphorea.registry.AphBuffs;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundPlayer;
import necesse.entity.chains.Chain;
import necesse.entity.chains.ChainLocation;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.util.List;

public class RuneOfSpiderEmpressProjectile extends BoomerangProjectile {
    private Chain chain;

    public RuneOfSpiderEmpressProjectile() {
    }

    public RuneOfSpiderEmpressProjectile(float x, float y, float angle, GameDamage damage, float projectileSpeed, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(500);
        this.speed = projectileSpeed;
    }

    public void init() {
        super.init();
        this.setWidth(10.0F, true);
        this.height = 18.0F;
        this.piercing = 0;
        this.isSolid = true;
        final Mob owner = this.getOwner();
        if (owner != null) {
            this.chain = new Chain(new ChainLocation() {
                public int getX() {
                    return (int) owner.x;
                }

                public int getY() {
                    return (int) owner.y;
                }

                public boolean removed() {
                    return false;
                }
            }, this);
            this.chain.sprite = new GameSprite(GameResources.chains, 5, 0, 32);
            this.chain.height = this.getHeight();
            this.getLevel().entityManager.addChain(this.chain);
        }
    }

    protected void returnToOwner() {
        if (!this.returningToOwner) {
            this.speed *= 2.0F;
        }

        super.returnToOwner();
    }

    @Override
    protected SoundPlayer playMoveSound() {
        return null;
    }

    public Trail getTrail() {
        return null;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.shadowTexture.getHeight() / 2);
        }
    }

    public void remove() {
        if (this.chain != null) {
            this.chain.remove();
        }

        super.remove();
    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        if (mob != null && getOwner().isPlayer) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, mob, 10000, this), true);
            mob.addBuff(new ActiveBuff(AphBuffs.STUN, mob, 3000, this), true);
        }
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
    }

}
