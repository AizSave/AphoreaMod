package aphorea.items.banners;

import aphorea.registry.AphBuffs;
import aphorea.registry.AphDamageType;
import aphorea.registry.AphModifiers;
import aphorea.other.area.AphArea;
import aphorea.other.area.AphAreaList;
import aphorea.other.itemtype.AphBanner;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;

public class StrikeBannerItem extends AphBanner {
    public static AphAreaList areaList = new AphAreaList(
            new AphArea(200, new Color(191, 0, 0, 191)).setDamageArea(30).setArmorPen(5)
    ).setDamageType(AphDamageType.BANNER);

    public StrikeBannerItem() {
        super(Rarity.LEGENDARY, 480, (m) -> AphBuffs.BANNER.STRIKE, 2000 / 50);
    }

    @Override
    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
        areaList.executeAreas(player);
        level.getServer().network.sendToAllClients(new StrikeBannerAreaParticlesPacket(player.getServerClient().slot));
    }

    public static class StrikeBannerAreaParticlesPacket extends Packet {
        public final int slot;

        public StrikeBannerAreaParticlesPacket(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader(this);
            this.slot = reader.getNextByteUnsigned();
        }

        public StrikeBannerAreaParticlesPacket(int slot) {
            this.slot = slot;
            PacketWriter writer = new PacketWriter(this);
            writer.putNextByteUnsigned(slot);
        }

        public void processClient(NetworkPacket packet, Client client) {
            if (client.getLevel() != null) {
                ClientClient target = client.getClient(this.slot);
                if (target != null && target.isSamePlace(client.getLevel())) {
                    applyToPlayer(target.playerMob.getLevel(), target.playerMob);
                } else {
                    client.network.sendPacket(new PacketRequestPlayerData(this.slot));
                }

            }
        }

        public static void applyToPlayer(Level level, PlayerMob player) {

            if (level != null && level.isClient()) {
                areaList.showAllAreaParticles(player, player.x, player.y, 1, 0.5F);
            }

        }
    }

    public DrawOptions getStandDrawOptions(Level level, int tileX, int tileY, int drawX, int drawY, GameLight light) {
        int anim = GameUtils.getAnim(level.getWorldEntity().getTime() + (tileX * 97L) + (tileY * 151L), 5, 800);
        int xOffset = -30;
        int yOffset = -32;

        return this.holdTexture.initDraw().sprite(anim, 2, 128).light(light).pos(drawX - 16 + xOffset, drawY - 40 + yOffset + (anim % 2 != 0 ? 0 : 2));
    }

    @Override
    public void addToolTips(ListGameTooltips tooltips, PlayerMob perspective) {
        if (perspective != null) {
            float bannerEffect = perspective.buffManager.getModifier(AphModifiers.BANNER_EFFECT);
            tooltips.add(Localization.translate("itemtooltip", "strikebannereffect", "effect", Math.round(10F * bannerEffect * 100.0f) / 100.0f));
            float bannerAbilitySpeed = perspective.buffManager.getModifier(AphModifiers.BANNER_ABILITY_SPEED);
            tooltips.add(Localization.translate("itemtooltip", "strikebannerability", "time", Math.round(2F / bannerAbilitySpeed * 100.0f) / 100.0f));
            areaList.addAreasToolTip(tooltips, perspective, true, null, null);
        }
    }
}

