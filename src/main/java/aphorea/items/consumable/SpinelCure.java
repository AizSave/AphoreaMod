package aphorea.items.consumable;

import aphorea.data.AphWorldData;
import aphorea.items.vanillaitemtypes.AphConsumableItem;
import aphorea.packets.AphParticlePacket;
import aphorea.particles.SpinelCureParticle;
import aphorea.registry.AphData;
import aphorea.registry.AphPackets;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.geom.Line2D;

public class SpinelCure extends AphConsumableItem {
    public SpinelCure() {
        super(1, true);
        this.rarity = Rarity.UNIQUE;
    }

    @Override
    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item, String error, GNDItemMap mapContent) {
        return error == null;
    }

    @Override
    public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        SoundManager.playSound(GameResources.crystalHit1, SoundEffect.effect(player));
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            AphWorldData currentData = AphData.getWorldData(level.getWorldEntity());
            currentData.spinelCured = true;

            PacketChatMessage message = new PacketChatMessage(Localization.translate("message", "spinelcure"));
            GameUtils.streamServerClients(level).forEach((j) -> j.sendPacket(message));
            player.getServer().network.sendToClientsAtEntireLevel(new AphPackets.PARTICLES.SpinelCureParticlePacket(player.x, player.y, 5000, Particle.GType.CRITICAL), player.getLevel());
        }

        return item;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        if (level.isServer()) {
            PacketChatMessage message = new PacketChatMessage(Localization.translate("message", error));
            player.getServerClient().sendPacket(message);
        }

        return super.onAttemptPlace(level, x, y, player, item, mapContent, error);
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return AphData.spinelCured(level.getWorldEntity()) ? "alreadycured" : null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "spinelcure"));
        return tooltips;
    }
}
