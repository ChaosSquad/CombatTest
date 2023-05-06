package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lobby implements GamePart {
    private final CombatTest plugin;
    private boolean killswitch;
    private int timeStep;
    private int time;
    private List<UUID> players;
    private boolean forcestart;

    public Lobby(CombatTest plugin) {
        this.plugin = plugin;
        this.killswitch = false;
        this.timeStep = 0;
        this.time = 60;
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.forcestart = false;
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

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayers()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            if ((player.getGameMode() != GameMode.ADVENTURE) && !this.plugin.isPlayerBypassing(playerId)) {
                player.setGameMode(GameMode.ADVENTURE);
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
        World world = this.plugin.getServer().getWorld(this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optString("world", ""));

        if (world == null) {
            return null;
        }

        List<Spawnpoint> spawnpoints = new ArrayList<>();

        JSONArray spawnpointArray = this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONArray("spawnpoints");

        if (spawnpointArray == null) {
            return null;
        }

        for (Object spawnpointObject : spawnpointArray) {
            try {
                JSONObject spawnpointData = (JSONObject) spawnpointObject;
                spawnpoints.add(new Spawnpoint(spawnpointData.getInt("x"), spawnpointData.getInt("y"), spawnpointData.getInt("z"), spawnpointData.getInt("direction"), spawnpointData.getInt("angle"), spawnpointData.getInt("team")));
            } catch (Exception ignored) {
                ignored.printStackTrace();
                this.plugin.getLogger().warning("Spawnpoint configuration contains errors");
            }
        }

        return new Game(this.plugin, this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optInt("time", 900),
                world,
                this.players,
                spawnpoints,
                this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optBoolean("enable", false),
                new int[]{
                        this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x1", -5),
                        this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y1", -5),
                        this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z1", -5),
                        this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x2", 5),
                        this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y2", 5),
                        this.plugin.getConfigManager().getConfig().optJSONObject("gameConfig", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z2", 5)
                }
        );
    }

    public void forcestart() {
        this.forcestart = true;
    }
}
