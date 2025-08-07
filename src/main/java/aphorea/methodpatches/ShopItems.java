package aphorea.methodpatches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import net.bytebuddy.asm.Advice;

import java.util.ArrayList;
import java.util.Objects;

@ModMethodPatch(target = HumanShop.class, name = "getShopItems", arguments = {VillageShopsData.class, ServerClient.class})
public class ShopItems {

    @Advice.OnMethodExit
    static void onExit(@Advice.This HumanShop humanShop, @Advice.Argument(0) VillageShopsData data, @Advice.Argument(1) ServerClient client, @Advice.Return(readOnly = false) ArrayList<ShopItem> shopItems) {
        if (Objects.equals(humanShop.getStringID(), "pawnbrokerhuman")) {
            if (shopItems == null) {
                shopItems = new ArrayList<>();
            }
            GameRandom random = new GameRandom(humanShop.getShopSeed() + 5L);
            shopItems.add(ShopItem.item("pawningrune", random.getIntBetween(1800, 2000)));
        }
    }
}
