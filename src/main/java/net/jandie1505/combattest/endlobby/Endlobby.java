package net.jandie1505.combattest.endlobby;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.GameStatus;
import net.jandie1505.combattest.game.PlayerData;
import net.jandie1505.combattest.game.TeamData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Endlobby implements GamePart {
    private final CombatTest plugin;
    private boolean timeStep;
    private int time;
    private World world;
    private boolean lobbyBorderEnabled;
    private int[] lobbyBorder;
    private Location lobbySpawn;
    private final Map<UUID, PlayerData> playerMap;
    private final List<PlayerData> playerKillsRanking;
    private final List<PlayerData> playerDeathsRanking;
    private final List<PlayerData> playerKDRanking;
    private final List<TeamData> teams;
    private final List<TeamData> teamKillsRanking;
    private final List<TeamData> teamDeathsRanking;
    private final List<TeamData> teamKDRanking;

    public Endlobby(CombatTest plugin, Map<UUID, PlayerData> playerMap) {
        this.plugin = plugin;
        this.timeStep = false;
        this.time = 60;

        this.world = this.plugin.getServer().getWorlds().get(0);
        this.lobbyBorderEnabled = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optBoolean("enable", false);
        this.lobbyBorder = new int[]{
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x1", -10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y1", -10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z1", -10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x2", 10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y2", 10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z2", 10)
        };
        this.lobbySpawn = new Location(
                this.world,
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("x", 0),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("y", 0),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("z", 0),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("yaw", 0.0F),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("pitch", 0.0F)
        );

        this.playerMap = playerMap;

        this.playerKillsRanking = new ArrayList<>(playerMap.values());
        this.playerKillsRanking.sort(PlayerData.getKillsComparator());

        this.playerDeathsRanking = new ArrayList<>(playerMap.values());
        this.playerDeathsRanking.sort(PlayerData.getDeathsComparator());

        this.playerKDRanking = new ArrayList<>(playerMap.values());
        this.playerKDRanking.sort(PlayerData.getKDComparator());

        for (UUID playerId : this.getPlayerMap().keySet()) {

            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.playerMap.remove(playerId);
                continue;
            }

            player.teleport(this.lobbySpawn);

            PlayerData playerData = this.getPlayerMap().get(playerId);

            player.sendMessage("§b§lYour stats§r§7\n Kills: §a" + playerData.getKills() + "§7\n Deaths: §c" + playerData.getDeaths() + "§7\n K/D: §6" + PlayerData.getKD(playerData.getKills(), playerData.getDeaths()) + "§7\n Points: §2" + playerData.getPoints());

            if (playerData.getTeam() > 0) {
                player.sendMessage("§7 Team: §9Team " + playerData.getTeam());
            }

            this.plugin.givePointsToPlayer(player, playerData.getRewardPoints(), "§6Rewards for this game: + {points} points");

        }

        this.teams = new ArrayList<>();

        for (Integer teamId : this.getTeams()) {
            TeamData teamData = new TeamData(teamId);

            teamData.setKills(this.getTeamKills(teamId));
            teamData.setDeaths(this.getTeamDeaths(teamId));

            this.teams.add(teamData);
        }

        this.teamKillsRanking = new ArrayList<>(this.teams);
        this.teamKillsRanking.sort(TeamData.getKillsComparator());

        this.teamDeathsRanking = new ArrayList<>(this.teams);
        this.teamDeathsRanking.sort(TeamData.getDeathsComparator());

        this.teamKDRanking = new ArrayList<>(this.teams);
        this.teamKDRanking.sort(TeamData.getKDComparator());
    }


    @Override
    public int tick() {

        // TIME
        if (timeStep) {
            this.timeStep = false;
            this.time--;
        } else {
            this.timeStep = true;
        }

        // BEST PLAYERS

        PlayerData mostKillsPlayerData = null;
        Player mostKillsPlayer = null;
        if (this.playerKillsRanking.size() > 0) {
            mostKillsPlayerData = this.playerKillsRanking.get(this.playerKillsRanking.size() - 1);
            mostKillsPlayer = this.plugin.getServer().getPlayer(mostKillsPlayerData.getPlayerId());
        }

        PlayerData mostDeathsPlayerData = null;
        Player mostDeathsPlayer = null;
        if (this.playerDeathsRanking.size() > 0) {
            mostDeathsPlayerData = this.playerDeathsRanking.get(this.playerDeathsRanking.size() - 1);
            mostDeathsPlayer = this.plugin.getServer().getPlayer(mostDeathsPlayerData.getPlayerId());
        }

        PlayerData bestKDPlayerData = null;
        Player bestKDPlayer = null;
        if (this.playerKDRanking.size() > 0) {
            bestKDPlayerData = this.playerKDRanking.get(this.playerKDRanking.size() - 1);
            bestKDPlayer = this.plugin.getServer().getPlayer(bestKDPlayerData.getPlayerId());
        }

        // PLAYER SPECIFIC CODE

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.playerMap.remove(playerId);
                continue;
            }

            PlayerData playerData = this.playerMap.get(playerId);

            // Gamemode

            if (player.getGameMode() != GameMode.ADVENTURE && !this.plugin.isPlayerBypassing(player.getUniqueId())) {
                player.setGameMode(GameMode.ADVENTURE);
            }

            // Health

            if (player.getHealthScale() < 20) {
                player.setHealth(20);
            }

            // Saturation

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            // Messages

            if (this.time >= 55 && this.time <= 59) {
                if (mostKillsPlayerData != null && mostKillsPlayer != null) {
                    player.sendTitle("§a" + mostKillsPlayer.getName(), "§aMost kills (" + mostKillsPlayerData.getKills() + ")", 0, 20, 0);
                } else {
                    player.sendTitle("§a--- offline ---", "§aMost kills", 0, 20, 0);
                }
            }

            if (this.time >= 50 && this.time <= 54) {
                if (mostDeathsPlayerData != null && mostDeathsPlayer != null) {
                    player.sendTitle("§c" + mostDeathsPlayer.getName(), "§cDarwin Award (" + mostDeathsPlayerData.getDeaths() + " deaths)", 0, 20, 0);
                } else {
                    player.sendTitle("§c--- offline ---", "§cDarwin Award", 0, 20, 0);
                }
            }

            if (this.time >= 45 && this.time <= 49) {
                if (bestKDPlayerData != null && bestKDPlayer != null) {
                    player.sendTitle("§6" + bestKDPlayer.getName(), "§6Best K/D (" + PlayerData.getKD(bestKDPlayerData.getKills(), bestKDPlayerData.getDeaths()) + ")", 0, 20, 0);
                } else {
                    player.sendTitle("§6--- offline ---", "§6Best K/D", 0, 20, 0);
                }
            }

            if (!this.teams.isEmpty()) {

                if (this.time >= 40 && this.time <= 44) {
                    TeamData mostKillsTeam = this.teamKillsRanking.get(this.teamKillsRanking.size() - 1);
                    player.sendTitle("§aTeam " + mostKillsTeam.getId(), "§aMost kills (" + mostKillsTeam.getKills() + ")", 0, 20, 0);
                }

                if (this.time >= 35 && this.time <= 39) {
                    TeamData mostDeathsTeam = this.teamDeathsRanking.get( this.teamDeathsRanking.size() - 1);
                    player.sendTitle("§cTeam " + mostDeathsTeam.getId(), "§cDarwin Award (" + mostDeathsTeam.getDeaths() + " deaths)", 0, 20, 0);
                }

                if (this.time >= 30 && this.time <= 34) {
                    TeamData bestKDTeam = this.teamKDRanking.get(this.teamKDRanking.size() - 1);
                    player.sendTitle("§6Team " + bestKDTeam.getId(), "§6Best K/D (" + PlayerData.getKD(bestKDTeam.getKills(), bestKDTeam.getDeaths()) + ")", 0, 20, 0);
                }

            }

            if (this.time >= 15 && this.time <= 29) {
                player.sendTitle(
                        "§a" + playerData.getKills() + " §7|§c " + playerData.getDeaths() + " §7|§6 " + PlayerData.getKD(playerData.getKills(), playerData.getDeaths()),
                        "§7Your stats: §aK §7|§c D §7|§6 K/D",
                        0,
                        20,
                        0);
            }

            if (this.time >= 0 && this.time <= 14) {
                player.sendTitle(
                        "§a#" + (this.playerKillsRanking.size() - this.playerKillsRanking.indexOf(playerData)) + " §7|§c #" + (this.playerDeathsRanking.size() - this.playerDeathsRanking.indexOf(playerData)) + " §7|§6 #" + (this.playerKDRanking.size() - this.playerKDRanking.indexOf(playerData)),
                        "§7Your ranking: §aK §7|§c D §7|§6 K/D",
                        0,
                        20,
                        0);
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6--- Game end in " + this.time + " seconds ---"));

            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

            if (!this.plugin.isPlayerBypassing(player.getUniqueId())) {

                // Clear Inventory

                player.getInventory().clear();

                // Lobby location

                if (this.lobbyBorderEnabled) {

                    Location location = player.getLocation();

                    if (!(location.getBlockX() >= this.lobbyBorder[0] && location.getBlockY() >= this.lobbyBorder[1] && location.getBlockZ() >= this.lobbyBorder[2] && location.getBlockX() <= this.lobbyBorder[3] && location.getBlockY() <= this.lobbyBorder[4] && location.getBlockZ() <= this.lobbyBorder[5])) {
                        player.teleport(this.lobbySpawn);
                    }

                }

            }
        }

        // TIME

        if (this.time >= 0) {
            return GameStatus.NORMAL;
        } else {
            return GameStatus.NEXT;
        }
    }

    @Override
    public GamePart getNextStatus() {

        if (this.plugin.isCloudSystemMode()) {
            this.plugin.getServer().shutdown();
        }

        return null;
    }

    @Override
    public boolean addPlayer(Player player) {
        return false;
    }

    @Override
    public boolean removePlayer(UUID playerId) {
        return this.playerMap.remove(playerId) != null;
    }

    @Override
    public List<UUID> getPlayers() {
        return List.copyOf(this.playerMap.keySet());
    }

    @Override
    public List<UUID> getPlayers(UUID[] playerIds) {
        return null;
    }

    public List<Integer> getTeams() {
        List<Integer> teamList = new ArrayList<>();

        for (UUID p : this.getPlayerMap().keySet()) {
            PlayerData playerData = this.getPlayerMap().get(p);

            if (playerData.getTeam() > 0 && !teamList.contains(playerData.getTeam())) {
                teamList.add(playerData.getTeam());
            }
        }

        return List.copyOf(teamList);
    }

    public int getTeamKills(int teamId) {
        int teamKills = 0;

        for (UUID p : this.getPlayerMap().keySet()) {
            PlayerData playerData = this.getPlayerMap().get(p);
            if (playerData.getTeam() == teamId) {
                teamKills = teamKills + this.getPlayerMap().get(p).getKills();
            }
        }

        return teamKills;
    }

    public int getTeamDeaths(int teamId) {
        int teamDeaths = 0;

        for (UUID p : this.getPlayerMap().keySet()) {
            PlayerData playerData = this.getPlayerMap().get(p);
            if (playerData.getTeam() == teamId) {
                teamDeaths = teamDeaths + this.getPlayerMap().get(p).getDeaths();
            }
        }

        return teamDeaths;
    }

    public List<UUID> getTeamMembers(int teamId) {
        List<UUID> teamMembers = new ArrayList<>();

        for (UUID p : this.getPlayerMap().keySet()) {

            if (this.getPlayerMap().get(p).getTeam() == teamId) {
                teamMembers.add(p);
            }

        }

        return List.copyOf(teamMembers);
    }

    public Map<UUID, PlayerData> getPlayerMap() {
        return Map.copyOf(this.playerMap);
    }
}
