package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.*;

public class Game implements GamePart {
    private final CombatTest plugin;
    private boolean killswitch;
    private int timeStep;
    private int time;
    World world;
    private Map<UUID, PlayerData> players;
    private List<Spawnpoint> spawnpoints;
    private boolean enableBorder;
    private int[] border;
    private Map<UUID, PlayerMenu> playerMenus;

    public Game(CombatTest plugin, int time, World world, List<UUID> players, List<Spawnpoint> spawnpoints, boolean enableBorder, int[] border) {
        this.plugin = plugin;
        this.killswitch = false;
        this.timeStep = 0;
        this.time = time;
        this.world = world;

        if (world == null) {
            this.killswitch = true;
        }

        this.players = Collections.synchronizedMap(new HashMap<>());

        for (UUID playerId : players) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            this.players.put(playerId, new PlayerData(playerId));
        }

        this.spawnpoints = Collections.synchronizedList(new ArrayList<>());
        this.spawnpoints.addAll(spawnpoints);

        this.enableBorder = enableBorder;
        this.border = border;
        if (enableBorder && border.length < 6) {
            this.killswitch = true;
        }

        this.playerMenus = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public int tick() {

        // KILL SWITCH

        if (this.killswitch) {
            return GameStatus.ABORT;
        }

        // TIME MANAGEMENT

        if (this.timeStep >= 1) {

            if (this.time >= 0) {
                this.time--;
            } else {
                return GameStatus.NEXT;
            }

            this.timeStep = 0;

        } else {
            this.timeStep++;
        }

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.removePlayer(playerId);
                break;
            }

            PlayerData playerData = this.players.get(playerId);

            // Respawn

            if (!playerData.isAlive()) {

                if ((player.getGameMode() != GameMode.SPECTATOR) && !(this.plugin.isPlayerBypassing(playerId))) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (playerData.getRespawntimer() > 0) {
                    player.sendTitle("§cDEAD", "§7Respawn in " + playerData.getRespawntimer() + " seconds", 0, 20, 0);
                    player.sendMessage("§7You will respawn in " + playerData.getRespawntimer() + " seconds");
                    playerData.setRespawntimer(playerData.getRespawntimer() - 1);
                } else {
                    this.respawnPlayer(player);
                }

            } else {

                if ((player.getGameMode() != GameMode.ADVENTURE) && !(this.plugin.isPlayerBypassing(playerId))) {
                    player.setGameMode(GameMode.ADVENTURE);
                }

                if (playerData.getRespawntimer() < 5) {
                    playerData.setRespawntimer(5);
                }

            }

            // Player Menu Object

            if (!this.playerMenus.containsKey(playerId)) {

                this.playerMenus.put(playerId, new PlayerMenu(this, playerId));

            }

            // Player Menu Button

            if (!ItemStorage.getPlayerMenuButton().isSimilar(player.getInventory().getItem(8))) {

                player.getInventory().remove(ItemStorage.getPlayerMenuButton());
                player.getInventory().setItem(8, ItemStorage.getPlayerMenuButton());

            }

            // Check scores

            if (!((playerData.getMeleeEquipment() == 0) || (playerData.getMeleeEquipment() >= 100 && playerData.getMeleeEquipment() <= 102) || (playerData.getMeleeEquipment() >= 200 && playerData.getMeleeEquipment() <= 202) || (playerData.getMeleeEquipment() >= 300 && playerData.getMeleeEquipment() <= 302) || (playerData.getMeleeEquipment() >= 1100 && playerData.getMeleeEquipment() <= 1103) || (playerData.getMeleeEquipment() >= 1200 && playerData.getMeleeEquipment() <= 1203) || (playerData.getMeleeEquipment() >= 1300 && playerData.getMeleeEquipment() <= 1303) || (playerData.getMeleeEquipment() >= 1400 && playerData.getMeleeEquipment() <= 1403) || (playerData.getMeleeEquipment() >= 1500 && playerData.getMeleeEquipment() <= 1503) || (playerData.getMeleeEquipment() >= 1600 && playerData.getMeleeEquipment() <= 1603))) {
                playerData.setMeleeEquipment(0);
            }

            if (!((playerData.getRangedEquipment() == 0) || (playerData.getRangedEquipment() >= 100 && playerData.getRangedEquipment() <= 101) || (playerData.getRangedEquipment() >= 200 && playerData.getRangedEquipment() <= 201) || (playerData.getMeleeEquipment() >= 300 && playerData.getMeleeEquipment() <= 301) || (playerData.getRangedEquipment() >= 1100 && playerData.getRangedEquipment() <= 1102) || (playerData.getRangedEquipment() >= 1200 && playerData.getRangedEquipment() <= 1202) || (playerData.getRangedEquipment() >= 1300 && playerData.getRangedEquipment() <= 1302) || (playerData.getRangedEquipment() >= 1400 && playerData.getRangedEquipment() <= 1402) || (playerData.getRangedEquipment() >= 1500 && playerData.getRangedEquipment() <= 1502) || (playerData.getRangedEquipment() >= 1600 && playerData.getRangedEquipment() <= 1602))) {
                playerData.setMeleeEquipment(0);
            }

            if (!((playerData.getArmorEquipment() == 0) || (playerData.getArmorEquipment() >= 100 && playerData.getArmorEquipment() <= 102) || (playerData.getArmorEquipment() >= 1100 && playerData.getArmorEquipment() <= 1103) || (playerData.getArmorEquipment() >= 1200 && playerData.getArmorEquipment() <= 1203))) {
                playerData.setArmorEquipment(0);
            }

            // Remove unowned Equipment

            for (ItemStack item : Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length)) {

                if (item != null) {

                    Integer meleeId = ItemStorage.getMeleeReverse(item);
                    Integer rangedId = ItemStorage.getRangedReverse(item);

                    if (meleeId != null && meleeId != playerData.getMeleeEquipment()) {
                        player.getInventory().remove(item);
                    }

                    if (rangedId != null && rangedId != playerData.getRangedEquipment()) {
                        player.getInventory().remove(item);
                    }

                    if (!(playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) && item.isSimilar(ItemStorage.getRocketLauncherAmmo())) {
                        player.getInventory().remove(item);
                    }

                }

            }

            // Give owned Equipment

            ItemStack meleeItem = ItemStorage.getMelee(playerData.getMeleeEquipment());

            if (meleeItem != null && !player.getInventory().contains(meleeItem) && !(player.getItemOnCursor() != null && player.getItemOnCursor().isSimilar(meleeItem))) {

                if ((playerData.getMeleeEquipment() >= 300 && playerData.getMeleeEquipment() <= 301) || (playerData.getMeleeEquipment() >= 1500 && playerData.getMeleeEquipment() <= 1699)) {

                    switch (playerData.getMeleeEquipment()) {
                        case 300:
                        case 1600:
                            if (playerData.getPotionTimer() >= 6) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        case 301:
                        case 1601:
                            if (playerData.getPotionTimer() >= 5) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        case 302:
                        case 1602:
                            if (playerData.getPotionTimer() >= 4) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        case 1500:
                        case 1603:
                            if (playerData.getPotionTimer() >= 3) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        case 1501:
                            if (playerData.getPotionTimer() >= 2) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        case 1502:
                            if (playerData.getPotionTimer() >= 1) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        case 1503:
                            if (playerData.getPotionTimer() >= 0.5) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            } else {
                                playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);
                            }
                            break;
                        default:
                            break;
                    }

                } else {
                    player.getInventory().addItem(meleeItem);
                }

            }

            ItemStack rangedItem = ItemStorage.getRanged(playerData.getRangedEquipment());

            if (rangedItem != null && !player.getInventory().contains(rangedItem) && !(player.getItemOnCursor() != null && player.getItemOnCursor().isSimilar(rangedItem))) {

                player.getInventory().addItem(rangedItem);

            }

            if ((playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) && (player.getInventory().getItem(40) == null || !player.getInventory().getItem(40).isSimilar(ItemStorage.getRocketLauncherAmmo()))) {
                player.getInventory().setItem(40, ItemStorage.getRocketLauncherAmmo());
            }

            // Saturation

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            if (player.getSaturation() < 20) {
                player.setSaturation(20);
            }

            // Regeneration

            if (!player.hasPotionEffect(PotionEffectType.REGENERATION) && playerData.getRegenerationCooldown() >= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999, 0, false, false));
            } else if (!player.hasPotionEffect(PotionEffectType.REGENERATION) && playerData.getRegenerationCooldown() < 10) {
                playerData.setRegenerationCooldown(playerData.getRegenerationCooldown() + 1);
            }

            // Actionbar

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a" + playerData.getKills() + " kills §8§l|§r§c " + playerData.getDeaths() + " deaths §8§l|§r§6 Points: " + playerData.getPoints() + " §8§l|§r§6 " + this.time + "s"));

            // Scoreboard

            if (this.plugin.isSingleServer()) {

                Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("player", Criteria.DUMMY, "");
                objective.setDisplayName("§6§lCOMBAT TEST");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                objective.getScore("§§§").setScore(6);
                objective.getScore("Kills: §a" + playerData.getKills()).setScore(5);
                objective.getScore("Deaths: §a" + playerData.getDeaths()).setScore(4);
                objective.getScore("Points: §a" + playerData.getPoints()).setScore(3);
                objective.getScore("§§").setScore(2);
                objective.getScore("Time: §a" + this.time + "s").setScore(1);
                objective.getScore("§").setScore(0);

                player.setScoreboard(scoreboard);

            }

        }

        // HANDLE MENUS

        for (UUID playerId : this.getPlayerMenus().keySet()) {

            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null || !this.players.containsKey(playerId)) {

                this.playerMenus.remove(playerId);

            }

        }

        // SINGLE SERVER MODE

        if (this.plugin.isSingleServer()) {

            for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

                if (player == null) {
                    continue;
                }

                if (this.players.containsKey(player.getUniqueId())) {
                    continue;
                }

                if (player.getGameMode() != GameMode.SPECTATOR && !this.plugin.isPlayerBypassing(player.getUniqueId())) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }

            }

        }

        return GameStatus.NORMAL;
    }

    @Override
    public boolean addPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (this.players.containsKey(player.getUniqueId())) {
            return false;
        }

        this.players.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        return true;
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        return this.players.remove(uuid) != null;
    }

    @Override
    public List<UUID> getPlayers() {
        return List.copyOf(this.getPlayerMap().keySet());
    }

    @Override
    public List<UUID> getPlayers(UUID[] playerIds) {
        List<UUID> returnList = new ArrayList<>();

        for (UUID playerId : playerIds) {

            if (this.getPlayers().contains(playerId)) {
                returnList.add(playerId);
            }

        }

        return List.copyOf(returnList);
    }

    public Map<UUID, PlayerData> getPlayerMap() {
        return Map.copyOf(this.players);
    }

    public boolean isPlayerIngame(Player player) {
        if (player == null) {
            return false;
        }

        return this.getPlayerMap().containsKey(player.getUniqueId());
    }

    public boolean respawnPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (!this.getPlayerMap().containsKey(player.getUniqueId())) {
            return false;
        }

        Spawnpoint spawnpoint = this.getRandomSpawnpoint(0);

        if (spawnpoint == null) {
            return false;
        }

        player.teleport(spawnpoint.buildLocation(world));
        this.getPlayerMap().get(player.getUniqueId()).setAlive(true);

        return true;
    }

    private Spawnpoint getRandomSpawnpoint(int teamMode) {
        int attempt = 0;

        while (attempt < 5) {

            try {

                Random random = new Random();
                Spawnpoint spawnpoint = this.spawnpoints.get(random.nextInt(this.spawnpoints.size()));

                if (attempt < 4) {
                    if (teamMode == spawnpoint.getTeamMode() || teamMode == -1) {
                        Collection<Entity> nearbyEntities = world.getNearbyEntities(spawnpoint.buildLocation(world), this.plugin.getConfigManager().getConfig().getInt("spawnpointBlockedRadius"), 2, this.plugin.getConfigManager().getConfig().getInt("spawnpointBlockedRadius"));

                        if (nearbyEntities.isEmpty()) {
                            return spawnpoint;
                        }
                    }
                } else {
                    return spawnpoint;
                }

            } catch (Exception ignored) {
                // ignored
            }

            attempt++;
        }

        return null;
    }

    public boolean isInBorders(Location location) {
        if (location.getWorld() != this.world) {
            return false;
        }

        return location.getBlockX() >= this.border[0] && location.getBlockY() >= this.border[1] && location.getBlockZ() >= this.border[2] && location.getBlockX() <= this.border[3] && location.getBlockY() <= this.border[4] && location.getBlockZ() <= this.border[5];
    }

    public int getTime() {
        return this.time;
    }

    public boolean isEnableBorder() {
        return this.enableBorder;
    }

    public World getWorld() {
        return this.world;
    }

    private Map<UUID, PlayerMenu> getPlayerMenus() {
        return Map.copyOf(this.playerMenus);
    }

    public PlayerMenu getPlayerMenu(UUID playerId) {
        return this.getPlayerMenus().get(playerId);
    }

    @Override
    public GamePart getNextStatus() {
        return new Endlobby(this.plugin);
    }
}
