package net.jandie1505.combattest.lobby;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.ItemStorage;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.GameStatus;
import net.jandie1505.combattest.game.PlayerData;
import net.jandie1505.combattest.game.Spawnpoint;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Lobby implements GamePart {
    private final CombatTest plugin;
    private boolean killswitch;
    private int timeStep;
    private int time;
    private Map<UUID, LobbyPlayerData> players;
    private boolean forcestart;
    private List<MapData> maps;
    private MapData selectedMap;
    private World world;
    private boolean lobbyBorderEnabled;
    private int[] lobbyBorder;
    private Location lobbySpawn;
    private Map<UUID, LobbyMenu> lobbyMenus;
    private boolean mapVoting;
    private boolean teamSelection;

    public Lobby(CombatTest plugin) {
        this.plugin = plugin;
        this.killswitch = false;
        this.timeStep = 0;
        this.time = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("time", 90);
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.forcestart = false;
        this.maps = new ArrayList<>();
        this.selectedMap = null;
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
        this.lobbyMenus = Collections.synchronizedMap(new HashMap<>());
        this.mapVoting = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optBoolean("mapVoting", false);
        this.teamSelection = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optBoolean("teamSelection", false);;

        for (String world : List.copyOf(this.plugin.getMapConfig().getConfig().keySet())) {
            try {
                JSONObject mapConfig = this.plugin.getMapConfig().getConfig().getJSONObject(world);

                List<Spawnpoint> spawnpoints = new ArrayList<>();

                for (Object object : mapConfig.getJSONArray("spawnpoints")) {

                    if (!(object instanceof JSONObject)) {
                        continue;
                    }

                    try {
                        JSONObject spawnpoint = (JSONObject) object;
                        spawnpoints.add(new Spawnpoint(spawnpoint.getInt("x"), spawnpoint.getInt("y"), spawnpoint.getInt("z"), spawnpoint.getInt("yaw"), spawnpoint.getInt("pitch"), spawnpoint.getInt("team")));
                    } catch (JSONException e) {
                        this.plugin.getLogger().warning("Error while loading a spawnpoint in map config " + world + ". Please check your configuration.");
                    }
                }

                this.maps.add(new MapData(
                        world,
                        mapConfig.getString("name"),
                        spawnpoints,
                        mapConfig.getJSONObject("border").getBoolean("enable"),
                        new int[]{mapConfig.getJSONObject("border").getInt("x1"),mapConfig.getJSONObject("border").getInt("y1"),mapConfig.getJSONObject("border").getInt("z1"),mapConfig.getJSONObject("border").getInt("x2"),mapConfig.getJSONObject("border").getInt("y2"),mapConfig.getJSONObject("border").getInt("z2")},
                        mapConfig.getInt("spawnpointBlockedRadius"),
                        mapConfig.getInt("time"),
                        mapConfig.getBoolean("enforcepvp")
                ));
            } catch (JSONException e) {
                this.plugin.getLogger().warning("Error while loading map config " + world + ". Please check your configuration.");
            }
        }
    }

    @Override
    public int tick() {

        // KILLSWITCH

        if (killswitch) {
            return GameStatus.ABORT;
        }

        // TIME MANAGEMENT

        if (this.timeStep >= 1) {

            if (time > 0) {

                if (players.size() >= 2) {
                    time--;
                } else if(time < 60) {
                    time++;
                }
            } else {
                return GameStatus.NEXT;
            }

            this.timeStep = 0;

        } else {
            this.timeStep++;
        }

        if (this.selectedMap == null && this.time <= 10) {
            this.autoSelectMap();
            this.displayMap();
        }

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            // Cleanup

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            // Get Player Data

            LobbyPlayerData playerData = this.players.get(playerId);

            // Force adventure mode

            if ((player.getGameMode() != GameMode.ADVENTURE) && !this.plugin.isPlayerBypassing(playerId)) {
                player.setGameMode(GameMode.ADVENTURE);
            }

            // Actionbar

            if (this.players.size() >= 2) {

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aStarting in " + this.time + "s §8§l|§r§a Players: " + this.players.size() + " / 2"));

            } else {

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cNot enough players (" + this.players.size() + " / 2)"));

            }

            // Messages

            if ((this.time <= 5 || (this.time % 10 == 0)) && players.size() >= 2 && this.timeStep >= 1) {
                player.sendMessage("§7The game starts in " + this.time + " seconds");
            }

            // Player Menu

            if (!this.lobbyMenus.containsKey(playerId)) {
                this.lobbyMenus.put(playerId, new LobbyMenu(this, playerId));
            }

            // Single Server stuff

            if (this.plugin.isSingleServer()) {

                // Lobby location

                if (!this.plugin.isPlayerBypassing(playerId) && this.lobbyBorderEnabled) {

                    Location location = player.getLocation();

                    if (!(location.getBlockX() >= this.lobbyBorder[0] && location.getBlockY() >= this.lobbyBorder[1] && location.getBlockZ() >= this.lobbyBorder[2] && location.getBlockX() <= this.lobbyBorder[3] && location.getBlockY() <= this.lobbyBorder[4] && location.getBlockZ() <= this.lobbyBorder[5])) {
                        player.teleport(this.lobbySpawn);
                    }

                }

                // Scoreboard

                String mapName = "---";

                if (this.selectedMap != null) {
                    mapName = this.selectedMap.getName();
                }

                Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("lobby", Criteria.DUMMY, "");

                objective.setDisplayName("§6§lCOMBAT TEST");

                objective.getScore("§§§§").setScore(7);

                if (this.players.size() >= 2) {

                    objective.getScore("§bStarting in " + this.time).setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §a" + this.players.size() + " / 2").setScore(4);

                } else {

                    objective.getScore("§cNot enough players").setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §c" + this.players.size() + " / 2").setScore(4);

                }

                objective.getScore("§§").setScore(3);
                objective.getScore("§7Map: §a" + mapName).setScore(2);

                objective.getScore("§").setScore(0);

                if (playerData.getTeam() > 0) {
                    objective.getScore("§7Team: §a" + playerData.getTeam()).setScore(1);
                } else {
                    objective.getScore("§7Team: §a" + "---").setScore(1);
                }

                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                player.setScoreboard(scoreboard);

                if (!this.plugin.isPlayerBypassing(player.getUniqueId())) {

                    for (ItemStack item : Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length)) {

                        if (item == null || item.getType() == Material.AIR) {
                            continue;
                        }

                        if (!item.isSimilar(ItemStorage.getLobbyVoteHotbarButton()) && !item.isSimilar(ItemStorage.getLobbyTeamSelectionHotbarButton())) {
                            player.getInventory().clear();
                        }

                    }

                    if (!player.getInventory().contains(ItemStorage.getLobbyVoteHotbarButton())) {
                        player.getInventory().setItem(3, ItemStorage.getLobbyVoteHotbarButton());
                    }

                    if (!player.getInventory().contains(ItemStorage.getLobbyTeamSelectionHotbarButton())) {
                        player.getInventory().setItem(5, ItemStorage.getLobbyTeamSelectionHotbarButton());
                    }

                }
            }

        }

        // PLAYER MENUS

        for (UUID playerId : Map.copyOf(this.lobbyMenus).keySet()) {

            if (!this.players.containsKey(playerId)) {
                this.lobbyMenus.remove(playerId);
            }

        }

        // SINGLE SERVER MODE

        if (this.plugin.isSingleServer()) {

            for (Player player : this.plugin.getServer().getOnlinePlayers()) {

                if (!this.players.containsKey(player.getUniqueId()) && !(this.plugin.isPlayerBypassing(player.getUniqueId()))) {
                    this.addPlayer(player);
                }

            }

        }

        // FORCE START

        if (this.forcestart) {
            return GameStatus.NEXT;
        }

        // DEFAULT RETURN

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

        this.players.put(player.getUniqueId(), new LobbyPlayerData());
        return true;
    }

    @Override
    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    public Map<UUID, LobbyPlayerData> getPlayerMap() {
        return Map.copyOf(this.players);
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

    private List<MapData> getHighestVotedMaps() {

        // Get map votes

        Map<MapData, Integer> mapVotes = new HashMap<>();

        for (UUID playerId : this.getPlayerMap().keySet()) {
            LobbyPlayerData playerData = this.players.get(playerId);

            if (playerData.getVote() == null) {
                continue;
            }

            if (mapVotes.containsKey(playerData.getVote())) {
                mapVotes.put(playerData.getVote(), mapVotes.get(playerData.getVote()) + 1);
            } else {
                mapVotes.put(playerData.getVote(), 1);
            }

        }

        // Get list of maps with the highest vote count

        List<MapData> highestVotedMaps = new ArrayList<>();
        int maxVotes = Integer.MIN_VALUE;

        for (Map.Entry<MapData, Integer> entry : mapVotes.entrySet()) {
            int votes = entry.getValue();
            if (votes > maxVotes) {
                maxVotes = votes;
                highestVotedMaps.clear();
                highestVotedMaps.add(entry.getKey());
            } else if (votes == maxVotes) {
                highestVotedMaps.add(entry.getKey());
            }
        }

        return highestVotedMaps;

    }

    private void autoSelectMap() {

        MapData selectedMap = null;

        if (this.mapVoting) {

            List<MapData> highestVotedMaps = this.getHighestVotedMaps();

            if (!highestVotedMaps.isEmpty()) {

                selectedMap = highestVotedMaps.get(new Random().nextInt(highestVotedMaps.size()));

            }

        }

        if (selectedMap == null) {

            if (!this.maps.isEmpty()) {

                selectedMap = this.maps.get(new Random().nextInt(this.maps.size()));

            }

        }

        this.selectedMap = selectedMap;

    }

    private void displayMap() {

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            if (this.selectedMap == null) {
                return;
            }

            player.sendMessage("§bThe map has been set to " + this.selectedMap.getName());

        }

    }

    public void randomTeams() {

        for (UUID playerId : this.getPlayerMap().keySet()) {

            LobbyPlayerData playerData = this.players.get(playerId);

            if (playerData == null || playerData.getTeam() == 1 || playerData.getTeam() == 2) {
                continue;
            }

            if (this.getTeamMembers(1).size() == this.getTeamMembers(2).size()) {

                playerData.setTeam(new Random().nextInt(3));

            } else if (this.getTeamMembers(1).size() < this.getTeamMembers(2).size()) {

                playerData.setTeam(1);

            } else if (this.getTeamMembers(1).size() > this.getTeamMembers(2).size()) {

                playerData.setTeam(2);

            }

        }

    }

    @Override
    public GamePart getNextStatus() {

        if (this.selectedMap == null) {
            this.autoSelectMap();
            this.displayMap();
        }

        if (selectedMap == null) {
            this.plugin.getLogger().warning("Game stopped because no world was selected");
            return null;
        }

        World world = this.plugin.loadWorld(selectedMap.getWorld());

        if (world == null || !this.plugin.getServer().getWorlds().contains(world)) {
            this.plugin.getLogger().warning("Game stopped because world does not exist");
            return null;
        }

        if (world == this.plugin.getServer().getWorlds().get(0)) {
            this.plugin.getLogger().warning("Game stopped because selected world is default world on server");
            return null;
        }

        world.setAutoSave(false);

        return new Game(
                this.plugin,
                selectedMap.getTime(),
                world,
                players,
                selectedMap.getSpawnpoints(),
                selectedMap.isEnableBorder(),
                selectedMap.getBorder(),
                selectedMap.isEnforcePvp()
        );
    }

    public void forcestart() {
        this.forcestart = true;
    }

    public MapData getSelectedMap() {
        return this.selectedMap;
    }

    public void selectMap(MapData mapData) {
        this.selectedMap = mapData;
        this.displayMap();
    }

    public List<MapData> getMaps() {
        return List.copyOf(this.maps);
    }

    public LobbyMenu getLobbyMenu(UUID playerId) {
        return this.lobbyMenus.get(playerId);
    }

    public boolean isMapVoting() {
        return this.mapVoting;
    }

    public void setMapVoting(boolean mapVoting) {
        this.mapVoting = mapVoting;
    }

    public int getTime() {
        return this.time;
    }

    public boolean isTeamSelection() {
        return this.teamSelection;
    }

    public void setTeamSelection(boolean teamSelection) {
        this.teamSelection = teamSelection;
    }

    public List<Integer> getTeams() {
        List<Integer> teamList = new ArrayList<>();

        for (UUID p : this.getPlayerMap().keySet()) {
            LobbyPlayerData playerData = this.getPlayerMap().get(p);

            if (playerData.getTeam() > 0 && !teamList.contains(playerData.getTeam())) {
                teamList.add(playerData.getTeam());
            }
        }

        return List.copyOf(teamList);
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
}
