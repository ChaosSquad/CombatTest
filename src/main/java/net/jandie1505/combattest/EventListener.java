package net.jandie1505.combattest;

import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.combattest.game.endlobby.Endlobby;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyMenu;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.jandie1505.combattest.game.lobby.MapData;
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

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EventListener implements Listener {
    private final CombatTest plugin;

    public EventListener(CombatTest plugin) {
        this.plugin = plugin;
    }

    /*
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

     */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.plugin.getGame() instanceof Lobby && event.getWhoClicked() instanceof Player && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getClickedInventory() != null && event.getClickedInventory().getHolder() == event.getWhoClicked()) {

            event.setCancelled(true);

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
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (this.plugin.getGame() instanceof Lobby && ((Lobby) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            event.setCancelled(true);

            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

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
                event.getPlayer().openInventory(((Lobby) this.plugin.getGame()).getVoteMenu().getInventory(event.getPlayer()));

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
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.plugin.getGame() instanceof Lobby || this.plugin.getGame() instanceof Endlobby) {

            if (event.getEntity() instanceof Player && this.plugin.getGame().getPlayers().contains(event.getEntity().getUniqueId())) {

                event.setCancelled(true);

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
