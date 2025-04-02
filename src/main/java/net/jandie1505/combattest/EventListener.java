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
        if (this.plugin.getGame() instanceof Game game) {

            if (event.getEntity() instanceof Trident && ItemStorage.getIdPrefix(((Trident) event.getEntity()).getItem()).equals(ItemStorage.EQUIPMENT_RANGED) && ItemStorage.getId(((Trident) event.getEntity()).getItem()) == 1602) {

                Random random = new Random();
                int number = random.nextInt(10);

                if (number >= 8) {

                    if (WorldUtils.getWeather(game.getWorld()) != WorldUtils.WeatherType.THUNDER) {

                        if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player && (game).getPlayerMap().containsKey(((Player) event.getEntity().getShooter()).getUniqueId())) {
                            ((Player) event.getEntity().getShooter()).sendMessage("§bThe weather has been changed through your weather manipulation ability");
                        }

                        WorldUtils.setWeather(game.getWorld(), WorldUtils.WeatherType.THUNDER);

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
