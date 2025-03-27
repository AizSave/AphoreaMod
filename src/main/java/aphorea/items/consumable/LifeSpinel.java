package aphorea.items.consumable;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class LifeSpinel extends ConsumableItem {
    public LifeSpinel() {
        super(50, true);
        this.rarity = Rarity.RARE;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
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
        player.setMaxHealth(Math.min(250, player.getMaxHealthFlat() + 5));
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtEntireLevelExcept(new PacketPlayerGeneral(player.getServerClient()), level, player.getServerClient());
        } else if (level.isClient()) {
            SoundManager.playSound(GameResources.crystalHit1, SoundEffect.effect(player));
        }

        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }

        return item;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        return player.getMaxHealthFlat() >= 250 ? "incorrecthealth" : null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        tooltips.add(Localization.translate("itemtooltip", "lifespinel"));
        return tooltips;
    }
}
