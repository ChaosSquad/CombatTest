package net.jandie1505.combattest.lobby;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.GameStatus;
import net.jandie1505.combattest.game.Spawnpoint;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
    private List<UUID> players;
    private boolean forcestart;
    private List<MapData> maps;
    private MapData selectedMap;
    private World world;
    private boolean lobbyBorderEnabled;
    private int[] lobbyBorder;
    private Location lobbySpawn;

    public Lobby(CombatTest plugin) {
        this.plugin = plugin;
        this.killswitch = false;
        this.timeStep = 0;
        this.time = 60;
        this.players = Collections.synchronizedList(new ArrayList<>());
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

        if (this.selectedMap == null && this.time <= 10 && !this.maps.isEmpty()) {
            this.selectedMap = this.maps.get(new Random().nextInt(this.maps.size()));
        }

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            // Cleanup

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

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

            if (this.time == 10 && this.selectedMap != null && players.size() >= 2 && this.timeStep >= 1) {
                player.sendMessage("§bSelected Map: " + this.selectedMap.getName());
            }

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

                objective.getScore("§§§").setScore(5);

                if (this.players.size() >= 2) {

                    objective.getScore("§bStarting in " + this.time).setScore(4);
                    objective.getScore("§§").setScore(3);
                    objective.getScore("§7Players: §a" + this.players.size() + " / 2").setScore(2);
                    objective.getScore("§7Map: §a" + mapName).setScore(1);

                } else {

                    objective.getScore("§cNot enough players").setScore(4);
                    objective.getScore("§§").setScore(3);
                    objective.getScore("§7Players: §c" + this.players.size() + " / 2").setScore(2);
                    objective.getScore("§7Map: §a" + mapName).setScore(1);

                }

                objective.getScore("§").setScore(0);

                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                player.setScoreboard(scoreboard);

                if (!this.plugin.isPlayerBypassing(player.getUniqueId())) {
                    player.getInventory().clear();
                }
            }

        }

        // SINGLE SERVER MODE

        if (this.plugin.isSingleServer()) {

            for (Player player : this.plugin.getServer().getOnlinePlayers()) {

                if (!this.players.contains(player.getUniqueId()) && !(this.plugin.isPlayerBypassing(player.getUniqueId()))) {
                    this.players.add(player.getUniqueId());
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

        if (this.players.contains(player.getUniqueId())) {
            return false;
        }

        this.players.add(player.getUniqueId());
        return true;
    }

    @Override
    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId);
    }

    @Override
    public List<UUID> getPlayers() {
        return List.copyOf(this.players);
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

    @Override
    public GamePart getNextStatus() {

        if (selectedMap == null) {
            this.plugin.getLogger().warning("Game stopped because no world was selected");
            return null;
        }

        World world = this.plugin.getServer().createWorld(new WorldCreator(selectedMap.getWorld()));

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
                world, players,
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
    }

    public List<MapData> getMaps() {
        return List.copyOf(this.maps);
    }
}
