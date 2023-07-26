package net.jandie1505.combattest;

import net.jandie1505.combattest.endlobby.Endlobby;
import net.jandie1505.combattest.game.*;
import net.jandie1505.combattest.lobby.Lobby;
import net.jandie1505.combattest.lobby.LobbyMenu;
import net.jandie1505.combattest.lobby.LobbyPlayerData;
import net.jandie1505.combattest.lobby.MapData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EventListener implements Listener {
    private final CombatTest plugin;

    public EventListener(CombatTest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).isPlayerIngame(event.getPlayer()) && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

            if (((Game) this.plugin.getGame()).isEnableBorder() && ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId()).isAlive()) {

                if (!((Game) this.plugin.getGame()).isInBorders(event.getTo())) {

                    if (((Game) this.plugin.getGame()).isInBorders(event.getFrom())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot leave the game area");
                        return;
                    } else {
                        event.setCancelled(true);
                        ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId()).setAlive(false);
                        event.getPlayer().sendMessage("§cYou have been killed for leaving the game area");
                        return;
                    }

                }

            } else if (!((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId()).isAlive()) {

                if (event.getTo() == null) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getFrom().getWorld() != event.getTo().getWorld()) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()) {
                    event.setCancelled(true);
                    return;
                }

            }

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

            PlayerData victimData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId());

            ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId()).setAlive(false);
            event.getEntity().setGameMode(GameMode.SPECTATOR);
            event.getDrops().clear();

            if (!((Game) this.plugin.getGame()).getWorld().getGameRuleValue("doImmediateRespawn").equalsIgnoreCase("true")) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    event.getEntity().spigot().respawn();
                }, 2);
            }

            if ((victimData.getMeleeEquipment() % 100) != 0) {
                victimData.setMeleeEquipment(victimData.getMeleeEquipment() - 1);
            }

            if ((victimData.getRangedEquipment() % 100) != 0) {
                victimData.setRangedEquipment(victimData.getRangedEquipment() - 1);
            }

            if ((victimData.getArmorEquipment() % 100) != 0) {
                victimData.setArmorEquipment(victimData.getArmorEquipment() - 1);
            }

            victimData.setDeaths(victimData.getDeaths() + 1);

            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

                Player damager;

                if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager().getUniqueId())) {
                    damager = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Projectile && ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(((Player) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter()).getUniqueId())) {
                    damager = (Player) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter();
                } else {
                    damager = null;
                }

                if (damager != null && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(damager.getUniqueId())) {

                    PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayerMap().get(damager.getUniqueId());
                    damagerData.setPoints(damagerData.getPoints() + 2000);
                    damagerData.setRewardPoints(damagerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("playerKill", 0));
                    damager.sendMessage("§bPlayer Kill: + 2000 Points");

                    double comparedEquipmentLevels = PlayerData.compareEquipmentLevels(damagerData, victimData);

                    System.out.println(comparedEquipmentLevels);

                    if (comparedEquipmentLevels < 0) {
                        int receivedPoints = ((int) (1000.0 * comparedEquipmentLevels * (-1.0)));
                        damagerData.setPoints(damagerData.getPoints() + receivedPoints);
                        damager.sendMessage("§bPlayer Kill (Low Equipment Bonus): + " + receivedPoints);
                    } else if (comparedEquipmentLevels > 0) {
                        int receivedPoints = ((int) (500.0 * comparedEquipmentLevels));
                        victimData.setPoints(victimData.getPoints() + receivedPoints);
                        event.getEntity().sendMessage("§bYou were killed by a player with significantly higher equipment: + " + receivedPoints + " Points");
                    }

                    damagerData.setKills(damagerData.getKills() + 1);

                    if (damagerData.getTeam() > 0) {

                        for (UUID playerId : ((Game) this.plugin.getGame()).getTeamMembers(damagerData.getTeam())) {
                            if (!damager.getUniqueId().equals(playerId) && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(playerId)) {
                                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(playerId);
                                playerData.setPoints((playerData.getPoints() + 500));
                            }
                        }

                    }

                }

            } else if (event.getEntity().getKiller() != null) {

                Player damager = event.getEntity().getKiller();

                if (damager != null && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(damager.getUniqueId())) {

                    PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayerMap().get(damager.getUniqueId());

                    damagerData.setPoints(damagerData.getPoints() + 1000);
                    damagerData.setRewardPoints(damagerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("indirectPlayerKill", 0));
                    damager.sendMessage("§bIndirect Player Kill: + 1000 Points");

                    double comparedEquipmentLevels = PlayerData.compareEquipmentLevels(damagerData, victimData);

                    System.out.println(comparedEquipmentLevels);

                    if (comparedEquipmentLevels < 0) {
                        int receivedPoints = ((int) (500.0 * comparedEquipmentLevels * (-1.0)));
                        damagerData.setPoints(damagerData.getPoints() + receivedPoints);
                        damager.sendMessage("§bIndirect Player Kill (Low Equipment Bonus): + " + receivedPoints);
                    } else if (comparedEquipmentLevels > 0) {
                        int receivedPoints = ((int) (250.0 * comparedEquipmentLevels));
                        victimData.setPoints(victimData.getPoints() + receivedPoints);
                        event.getEntity().sendMessage("§bYou were killed by a player indirectly with significantly higher equipment: + " + receivedPoints + " Points");
                    }

                    damagerData.setKills(damagerData.getKills() + 1);

                    if (damagerData.getTeam() > 0) {

                        for (UUID playerId : ((Game) this.plugin.getGame()).getTeamMembers(damagerData.getTeam())) {
                            if (!damager.getUniqueId().equals(playerId) && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(playerId)) {
                                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(playerId);
                                playerData.setPoints((playerData.getPoints() + 250));
                            }
                        }

                    }

                }

            }

        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            Location location = event.getPlayer().getLocation();

            if (location.getY() < -65) {
                location.setY(-65);
            }

            event.setRespawnLocation(location);

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.plugin.getGame() instanceof Lobby && event.getWhoClicked() instanceof Player && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getClickedInventory() != null && event.getClickedInventory().getHolder() == event.getWhoClicked()) {

            event.setCancelled(true);

            return;
        }

        if (this.plugin.getGame() instanceof Game && event.getWhoClicked() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getClickedInventory() != null && event.getClickedInventory().getHolder() == event.getWhoClicked()) {

            // Block player menu slot
            if (event.getSlot() == 8) {
                event.setCancelled(true);

                if ((event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE) && event.getCurrentItem() != null && ItemStorage.getIdPrefix(event.getCurrentItem()).equals(ItemStorage.HOTBAR_ITEM) && ItemStorage.getId(event.getCurrentItem()) == 0) {

                    PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getWhoClicked().getUniqueId());

                    menu.setPage(0);
                    event.getWhoClicked().openInventory(menu.getInventory());

                    if (event.getWhoClicked().getItemOnCursor() != null && ItemStorage.getIdPrefix(event.getWhoClicked().getItemOnCursor()).equals(ItemStorage.HOTBAR_ITEM) && ItemStorage.getId(event.getWhoClicked().getItemOnCursor()) == 0) {

                        event.getWhoClicked().getInventory().setItem(40, new ItemStack(Material.AIR));

                    }

                }

                return;
            }

            // Block player menu slot for number keys
            if (event.getHotbarButton() == 8) {
                event.setCancelled(true);
                return;
            }

            // Block offhand
            if (event.getSlot() == 40 || event.getRawSlot() == 45 || event.getClick() == ClickType.SWAP_OFFHAND) {

                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cOffhand can only be used for specific items");

                /*
                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                if (playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) {
                    event.setCancelled(true);
                }

                */

                return;
            }

            // Block armor slots
            if (event.getSlot() == 36 || event.getSlot() == 37 || event.getSlot() == 38 || event.getSlot() == 39) {
                event.setCancelled(true);
                return;
            }

            if (event.isShiftClick() && event.getCurrentItem() != null && ItemStorage.isArmor(event.getCurrentItem())) {
                event.setCancelled(true);
                return;
            }

            return;
        }

        // Player Menu
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof PlayerMenu) {

            event.setCancelled(true);

            if (this.plugin.getGame() instanceof Game && event.getWhoClicked() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getCurrentItem() != null) {

                PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getWhoClicked().getUniqueId());

                if (menu.getPage() == 0) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getMeleeButton())) {

                        menu.setPage(1);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getRangedButton())) {

                        menu.setPage(2);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getArmorButton())) {

                        menu.setPage(3);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getItemShopButton())) {

                        menu.setPage(4);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    }

                } else if (menu.getPage() == 1) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getResetButton())) {

                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (playerData.getPoints() >= 10000) {

                            playerData.setMeleeEquipment(0);
                            playerData.setPoints(playerData.getPoints() - 10000);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§aSuccessfully reset your melee equipment");

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou don't have enough points to reset your melee equipment");

                        }

                    } else {

                        Integer itemId = ItemStorage.getMeleeReverse(event.getCurrentItem());
                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (itemId != null && itemId > playerData.getMeleeEquipment()) {

                            if (playerData.getPoints() >= ItemStorage.getMeleePrice(itemId)) {

                                playerData.setMeleeEquipment(itemId);
                                playerData.setPoints(playerData.getPoints() - ItemStorage.getMeleePrice(itemId));
                                playerData.setRewardPoints(playerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("upgradePurchased", 0));
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§aItem successfully upgraded");

                            } else {

                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§cYou don't have enough points to upgrade (Price: " + ItemStorage.getMeleePrice(itemId) + ")");

                            }

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou cannot upgrade to an item which is the same or below the item you already have");

                        }

                    }

                } else if (menu.getPage() == 2) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getResetButton())) {

                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (playerData.getPoints() >= 10000) {

                            playerData.setRangedEquipment(0);
                            playerData.setPoints(playerData.getPoints() - 10000);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§aSuccessfully reset your ranged equipment");

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou don't have enough points to reset your ranged equipment");

                        }

                    } else {

                        Integer itemId = ItemStorage.getRangedReverse(event.getCurrentItem());
                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (itemId != null && itemId > playerData.getRangedEquipment()) {

                            if (playerData.getPoints() >= ItemStorage.getRangedPrice(itemId)) {

                                playerData.setRangedEquipment(itemId);
                                playerData.setPoints(playerData.getPoints() - ItemStorage.getRangedPrice(itemId));
                                playerData.setRewardPoints(playerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("upgradePurchased", 0));
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§aItem successfully upgraded");

                            } else {

                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§cYou don't have enough points to upgrade (Price: " + ItemStorage.getRangedPrice(itemId) + ")");

                            }

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou cannot upgrade to an item which is the same or below the item you already have");

                        }

                    }

                } else if (menu.getPage() == 3) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getResetButton())) {

                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (playerData.getPoints() >= 10000) {

                            playerData.setArmorEquipment(0);
                            playerData.setPoints(playerData.getPoints() - 10000);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§aSuccessfully reset your armor");

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou don't have enough points to reset your armor");

                        }

                    } else {

                        Integer itemId = ItemStorage.getArmorReverse(event.getCurrentItem());
                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (itemId != null && itemId > playerData.getArmorEquipment()) {

                            if (playerData.getPoints() >= ItemStorage.getArmorPrice(itemId)) {

                                playerData.setArmorEquipment(itemId);
                                playerData.setPoints(playerData.getPoints() - ItemStorage.getArmorPrice(itemId));
                                playerData.setRewardPoints(playerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("upgradePurchased", 0));
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§aItem successfully upgraded");

                            } else {

                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§cYou don't have enough points to upgrade (Price: " + ItemStorage.getArmorPrice(itemId) + ")");

                            }

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou cannot upgrade to an item which is the same or below the item you already have");

                        }

                    }

                } else if (menu.getPage() == 4) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else {

                        Integer itemId = ItemStorage.getShopItemReverse(event.getCurrentItem());
                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (itemId != null) {

                            if (playerData.getPoints() >= ItemStorage.getShopItemPrice(itemId)) {

                                playerData.setPoints(playerData.getPoints() - ItemStorage.getShopItemPrice(itemId));
                                event.getWhoClicked().getInventory().addItem(ItemStorage.getShopItem(itemId));
                                event.getWhoClicked().sendMessage("§aItem successfully purchased");

                            } else {
                                event.getWhoClicked().sendMessage("§cUnfortunately you cannot afford this item :(");
                            }

                        }

                    }

                }

            }

            return;
        }

        // Lobby Menu
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof LobbyMenu) {

            event.setCancelled(true);

            if (this.plugin.getGame() instanceof Lobby && event.getWhoClicked() instanceof Player && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getCurrentItem() != null) {

                LobbyMenu menu = ((Lobby) this.plugin.getGame()).getLobbyMenu(event.getWhoClicked().getUniqueId());

                if (menu.getPage() == 0) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getLobbyVoteButton())) {

                        if (!((Lobby) this.plugin.getGame()).isMapVoting() || ((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cMap voting is already over");
                            return;
                        }

                        menu.setPage(1);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getLobbyTeamsButton())) {

                        menu.setPage(2);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    }

                } else if (menu.getPage() == 1) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (ItemStorage.getIdPrefix(event.getCurrentItem()).equals(ItemStorage.MENU_ITEM) && ItemStorage.getId(event.getCurrentItem()) == 9) {

                        if (!((Lobby) this.plugin.getGame()).isMapVoting() || ((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cMap voting is already over");
                            return;
                        }

                        List<String> lore = event.getCurrentItem().getItemMeta().getLore();

                        if (lore.size() < 2) {
                            return;
                        }

                        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        for (MapData map : ((Lobby) this.plugin.getGame()).getMaps()) {

                            if (map.getWorld().equals(lore.get(1))) {

                                event.getWhoClicked().closeInventory();

                                if (playerData.getVote() == map) {

                                    playerData.setVote(null);
                                    event.getWhoClicked().sendMessage("§aYou removed your vote");

                                } else {

                                    playerData.setVote(map);
                                    event.getWhoClicked().sendMessage("§aYou changed your vote to " + map.getName());

                                }

                                return;
                            }

                        }

                    }

                } else if (menu.getPage() == 2) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else {

                        if (!((Lobby) this.plugin.getGame()).isTeamSelection()) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cTeam selection is disabled");
                            return;
                        }

                        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (event.getCurrentItem().isSimilar(ItemStorage.getLobbyTeamSelectionLeaveTeamButton())) {

                            playerData.setTeam(0);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§aLeft team successfully");

                        } else if (event.getCurrentItem().isSimilar(ItemStorage.getLobbyTeamSelectionCreateTeamButton())) {

                            List<Integer> teams = ((Lobby) this.plugin.getGame()).getTeams();

                            int nextTeamId = 0;

                            if (teams.isEmpty()) {

                                nextTeamId = 1;

                            } else {

                                for (int i = 1; i < Integer.MAX_VALUE; i++) {

                                    if (teams.contains(i)) {
                                        continue;
                                    }

                                    nextTeamId = i;
                                    break;
                                }

                            }

                            if (nextTeamId <= 0) {
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§cError while creating team");
                                return;
                            }

                            playerData.setTeam(nextTeamId);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§aTeam created successfully");

                            return;
                        } else if (ItemStorage.getIdPrefix(event.getCurrentItem()).equals(ItemStorage.MENU_ITEM) && ItemStorage.getId(event.getCurrentItem()) == 10) {

                            List<String> lore = event.getCurrentItem().getItemMeta().getLore();

                            if (lore.size() < 2) {
                                return;
                            }

                            int teamId;

                            try {
                                teamId = Integer.parseInt(lore.get(1));
                            } catch (IllegalArgumentException e) {
                                teamId = 0;
                            }

                            if (teamId <= 0) {
                                return;
                            }

                            playerData.setTeam(teamId);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§aSuccessfully joined team " + teamId);

                            return;
                        }

                    }

                }

            }

        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (this.plugin.getGame() instanceof Lobby && event.getWhoClicked() instanceof Player && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getInventory().getHolder() == event.getWhoClicked()) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.getGame() instanceof Game && event.getWhoClicked() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getInventory() != null && event.getInventory().getHolder() == event.getWhoClicked()) {

            if (event.getInventorySlots().contains(8)) {
                event.setCancelled(true);
                return;
            }

            if (event.getInventorySlots().contains(40) || event.getRawSlots().contains(45)) {
                event.setCancelled(true);
                return;
            }

        }

        if (event.getInventory() != null && (event.getInventory().getHolder() instanceof PlayerMenu || event.getInventory().getHolder() instanceof LobbyMenu)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (this.plugin.getGame() instanceof Lobby && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            event.setCancelled(true);

            return;
        }

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                event.setCancelled(true);
            }

            if (ItemStorage.getMeleeReverse(event.getItemDrop().getItemStack()) != null || ItemStorage.getIdPrefix(event.getItemDrop().getItemStack()).equals(ItemStorage.EQUIPMENT_MELEE)) {
                event.setCancelled(true);
            }

            if (ItemStorage.getRangedReverse(event.getItemDrop().getItemStack()) != null || ItemStorage.getIdPrefix(event.getItemDrop().getItemStack()).equals(ItemStorage.EQUIPMENT_RANGED)) {
                event.setCancelled(true);
            }

            if (ItemStorage.getArmorReverse(event.getItemDrop().getItemStack()) != null || ItemStorage.getIdPrefix(event.getItemDrop().getItemStack()).equals(ItemStorage.EQUIPMENT_ARMOR)) {
                event.setCancelled(true);
            }

            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (this.plugin.getGame() instanceof  Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getItem() != null) {

                if (ItemStorage.getIdPrefix(event.getItem()).equals(ItemStorage.HOTBAR_ITEM) && ItemStorage.getId(event.getItem()) == 0) {

                    event.setCancelled(true);

                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                        PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getPlayer().getUniqueId());

                        menu.setPage(0);
                        event.getPlayer().openInventory(menu.getInventory());

                    }

                    return;
                }

                if (ItemStorage.isArmor(event.getItem())) {
                    event.setCancelled(true);
                    return;
                }

            }

            return;
        }

        if (this.plugin.getGame() instanceof Lobby && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getItem() == null) {
                return;
            }

            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            if (ItemStorage.getLobbyVoteHotbarButton().isSimilar(event.getItem())) {

                event.setCancelled(true);

                LobbyMenu lobbyMenu = ((Lobby) this.plugin.getGame()).getLobbyMenu(event.getPlayer().getUniqueId());

                if (lobbyMenu == null) {
                    return;
                }

                lobbyMenu.setPage(1);
                event.getPlayer().openInventory(lobbyMenu.getInventory());

                return;
            }

            if (ItemStorage.getLobbyTeamSelectionHotbarButton().isSimilar(event.getItem())) {

                event.setCancelled(true);

                LobbyMenu lobbyMenu = ((Lobby) this.plugin.getGame()).getLobbyMenu(event.getPlayer().getUniqueId());

                if (lobbyMenu == null) {
                    return;
                }

                lobbyMenu.setPage(2);
                event.getPlayer().openInventory(lobbyMenu.getInventory());

                return;
            }

            return;
        }

    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.plugin.getGame() instanceof Lobby && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            event.setCancelled(true);

            return;
        }

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            event.setCancelled(true);
            event.getPlayer().getInventory().setItem(40, new ItemStack(Material.AIR));
            return;

            /*
            if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                event.setCancelled(true);
                return;
            }

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId());

            if (playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) {
                event.setCancelled(true);
                return;
            }

             */

        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.plugin.getGame() instanceof Game) {

            if (event.getEntity() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

                ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId()).setRegenerationCooldown(0);
                ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId()).setNoPvpTimer(0);
                ((Player) event.getEntity()).removePotionEffect(PotionEffectType.REGENERATION);

            }

        } else if (this.plugin.getGame() instanceof Lobby || this.plugin.getGame() instanceof Endlobby) {

            if (event.getEntity() instanceof Player && this.plugin.getGame().getPlayers().contains(event.getEntity().getUniqueId())) {

                event.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (this.plugin.getGame() instanceof Game) {

            if (event.getEntity() instanceof Trident && ItemStorage.getIdPrefix(((Trident) event.getEntity()).getItem()).equals(ItemStorage.EQUIPMENT_RANGED) && ItemStorage.getId(((Trident) event.getEntity()).getItem()) == 1602) {

                Random random = new Random();
                int number = random.nextInt(10);

                if (number >= 8) {

                    if (CombatTest.getWeather(((Game) this.plugin.getGame()).getWorld()) != 2) {

                        if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(((Player) event.getEntity().getShooter()).getUniqueId())) {
                            ((Player) event.getEntity().getShooter()).sendMessage("§bThe weather has been changed through your weather manipulation ability");
                        }

                        CombatTest.setThunderingWeather(((Game) this.plugin.getGame()).getWorld());

                    }

                }

            }

        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.plugin.getGame() instanceof Lobby || this.plugin.getGame() instanceof Endlobby) {

            if (event.getEntity() instanceof Player) {

                if (this.plugin.getGame().getPlayers().contains(event.getEntity().getUniqueId())) {
                    event.setCancelled(true);
                } else if (event.getDamager() instanceof Player && this.plugin.getGame().getPlayers().contains(event.getDamager().getUniqueId())) {
                    event.setCancelled(true);
                }

            }

        } else if (this.plugin.getGame() instanceof Game) {

            if (event.getEntity() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

                Player damager;

                if (event.getDamager() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getDamager().getUniqueId())) {
                    damager = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(((Player) ((Projectile) event.getDamager()).getShooter()).getUniqueId())) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                } else {
                    damager = null;
                }

                if (damager != null) {

                    PlayerData victimData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId());
                    PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getDamager().getUniqueId());

                    if (damagerData != null) {

                        if (victimData.getTeam() > 0 && victimData.getTeam() == damagerData.getTeam()) {
                            event.setCancelled(true);
                            return;
                        }

                        damagerData.setPoints(damagerData.getPoints() + (5 * (int) event.getDamage()));
                        damagerData.setNoPvpTimer(0);

                        if (damagerData.getTeam() > 0) {

                            for (UUID playerId : ((Game) this.plugin.getGame()).getTeamMembers(damagerData.getTeam())) {
                                if (!event.getDamager().getUniqueId().equals(playerId) && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(playerId)) {
                                    PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(playerId);
                                    playerData.setPoints((playerData.getPoints() + ((int) (5.0 * event.getDamage() * 0.25))));
                                }
                            }

                        }

                    }

                }

            }

        } else if (this.plugin.getGame() == null && this.plugin.isSingleServer() && this.plugin.isAutostartNewGame()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRiptide(PlayerRiptideEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId());

            if (playerData.getRangedEquipment() >= 1500 && playerData.getRangedEquipment() <= 1599) {
                playerData.setHasUsedTrident(true);
                return;
            }

        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // teleport

        if (this.plugin.getGame() instanceof Lobby) {

            event.getPlayer().teleport(((Lobby) this.plugin.getGame()).getLobbySpawn());

        }

    }
}
