package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphConsumableItem;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;

public class UnstableCore extends AphConsumableItem {
    public UnstableCore() {
        super(5, true);
        this.itemCooldownTime.setBaseValue(2000);
        this.setItemCategory("consumable", "bossitems");
        this.dropsAsMatDeathPenalty = true;
        this.keyWords.add("boss");
        this.rarity = Rarity.LEGENDARY;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    public String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (level.isClient()) {
            return null;
        } else if (level instanceof IncursionLevel) {
            return "inincursion";
        } else if (!level.isIslandPosition()) {
            return "notisland";
        } else if (level.getIslandDimension() != 0) {
            return "notsurface";
        } else if (level.getServer().world.worldEntity.isNight()) {
            return "night";
        } else {
            ArrayList<Point> spawnPoints = new ArrayList<>();
            Mob mob = MobRegistry.getMob("unstablegelslime", level);
            int pTileX = player.getX() / 32;
            int pTileY = player.getY() / 32;

            for (int i = -10; i <= 10; ++i) {
                for (int j = -10; j <= 10; ++j) {
                    int tileX = pTileX + i;
                    int tileY = pTileY + j;
                    if (!level.isLiquidTile(tileX, tileY) && !level.isShore(tileX, tileY) && !mob.collidesWith(level, tileX * 32 + 16, tileY * 32 + 16)) {
                        spawnPoints.add(new Point(tileX, tileY));
                    }
                }
            }

            if (spawnPoints.isEmpty()) {
                return "nospace";
            } else {
                return null;
            }
        }
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (level.isServer()) {
            if (level instanceof IncursionLevel) {
                GameMessage summonError = ((IncursionLevel) level).canSummonBoss("unstablegelslime");
                if (summonError != null) {
                    if (player != null && player.isServerClient()) {
                        player.getServerClient().sendChatMessage(summonError);
                    }

                    return item;
                }
            }

            ArrayList<Point> spawnPoints = new ArrayList<>();
            Mob mob = MobRegistry.getMob("unstablegelslime", level);
            int pTileX = player.getX() / 32;
            int pTileY = player.getY() / 32;

            for (int i = -10; i <= 10; ++i) {
                for (int j = -10; j <= 10; ++j) {
                    int tileX = pTileX + i;
                    int tileY = pTileY + j;
                    if (!level.isLiquidTile(tileX, tileY) && !level.isShore(tileX, tileY) && !mob.collidesWith(level, tileX * 32 + 16, tileY * 32 + 16)) {
                        spawnPoints.add(new Point(tileX, tileY));
                    }
                }
            }

            System.out.println("Unstable Gel Slime has been summoned at " + level.getIdentifier() + ".");
            Point spawnPoint;
            if (!spawnPoints.isEmpty()) {
                spawnPoint = GameRandom.globalRandom.getOneOf(spawnPoints);
            } else {
                spawnPoint = new Point(player.getTileX() + GameRandom.globalRandom.getIntBetween(-8, 8), player.getTileY() + GameRandom.globalRandom.getIntBetween(-8, 8));
            }

            level.entityManager.addMob(mob, (float) (spawnPoint.x * 32 + 16), (float) (spawnPoint.y * 32 + 16));
            level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", mob.getLocalization())), mob);
            if (level instanceof IncursionLevel) {
                ((IncursionLevel) level).onBossSummoned(mob);
            }
        }

        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }

        return item;
    }

    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader, String error) {
        if (level.isServer() && player != null && player.isServerClient() && error.equals("inincursion")) {
            player.getServerClient().sendChatMessage(new LocalMessage("misc", "cannotsummoninincursion"));
        } else {
            if (level.isServer() && player != null) {

                if (error.equals("night")) {
                    level.getServer().network.sendPacket(new PacketMobChat(player.getUniqueID(), "message", "cantuseatnight"), player.getServerClient());
                } else {
                    String translationKey;

                    switch (error) {
                        case "alreadyspawned":
                            translationKey = null;
                            break;
                        case "notsurface":
                            translationKey = "portalnotonsurface";
                            break;
                        case "nospace":
                            translationKey = "portalnospace";
                            break;
                        default:
                            translationKey = "portalerror";
                            break;
                    }

                    if (translationKey != null) {
                        level.getServer().network.sendPacket(new PacketMobChat(player.getUniqueID(), "itemtooltip", translationKey), player.getServerClient());
                    }
                }
            }
        }
        return item;
    }

    public String getTranslatedTypeName() {
        return Localization.translate("item", "relic");
    }

    @Override
    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "unstablecore"));
        return tooltips;
    }
}