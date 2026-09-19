package net.jandie1505.combattest.config;

import net.jandie1505.datastorage.DataStorage;
import org.json.JSONArray;
import org.json.JSONObject;

public final class DefaultConfigValues {
    private DefaultConfigValues() {}

    public static DataStorage getConfig() {
        var config = new DataStorage();

        config.set(ConfigKeys.SINGLE_SERVER_MODE, true);
        config.set(ConfigKeys.AUTOSTART_NEW_GAME, false);

        config.set(ConfigKeys.LOBBY_SPAWNPOINT_X, 0);
        config.set(ConfigKeys.LOBBY_SPAWNPOINT_Y, 0);
        config.set(ConfigKeys.LOBBY_SPAWNPOINT_Z, 0);
        config.set(ConfigKeys.LOBBY_SPAWNPOINT_YAW, 0.0);
        config.set(ConfigKeys.LOBBY_SPAWNPOINT_PITCH, 0.0);

        config.set(ConfigKeys.LOBBY_BORDER_ENABLE, true);
        config.set(ConfigKeys.LOBBY_BORDER_X1, -10);
        config.set(ConfigKeys.LOBBY_BORDER_Y1, -10);
        config.set(ConfigKeys.LOBBY_BORDER_Z1, -10);
        config.set(ConfigKeys.LOBBY_BORDER_X2, 10);
        config.set(ConfigKeys.LOBBY_BORDER_Y2, 10);
        config.set(ConfigKeys.LOBBY_BORDER_Z2, 10);

        config.set(ConfigKeys.LOBBY_TIME, 60);
        config.set(ConfigKeys.LOBBY_REQUIRED_PLAYERS, 2);
        config.set(ConfigKeys.LOBBY_MAP_VOTING, true);
        config.set(ConfigKeys.LOBBY_TEAM_SELECTION, true);

        config.set(ConfigKeys.CLOUD_SYSTEM_MODE_ENABLE, false);
        config.set(ConfigKeys.CLOUD_SYSTEM_MODE_SWITCH_TO_INGAME_COMMAND, "");

        config.set(ConfigKeys.INTEGRATIONS_CLOUDNET, true);
        config.set(ConfigKeys.INTEGRATIONS_PARTY_AND_FRIENDS, true);
        config.set(ConfigKeys.INTEGRATIONS_SUPERVANISH_PREMIUMVANISH, true);

        config.set(ConfigKeys.INTEGRATIONS_PLAYERPOINTS_ENABLE, false);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERPOINTS_KILL, 20);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERPOINTS_INDIRECT_KILL, 5);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERPOINTS_UPGRADE_PURCHASED, 5);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERPOINTS_MAX_REWARDS_AMOUNT, 5000);

        config.set(ConfigKeys.INTEGRATIONS_PLAYERLEVELS_ENABLE, false);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERLEVELS_KILL, 10.0);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERLEVELS_INDIRECT_KILL, 5.0);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERLEVELS_UPGRADE_PURCHASED, 1.0);
        config.set(ConfigKeys.INTEGRATIONS_PLAYERLEVELS_MAX_REWARDS_AMOUNT, 1000.0);

        config.set(ConfigKeys.GAME_SPAWNPOINT_BLOCKED_RADIUS, 10);

        return config;
    }

    public static JSONObject getWorldConfig() {

        JSONObject config = new JSONObject();

        JSONObject mapConfig = new JSONObject();

        mapConfig.put("name", "Example World");

        JSONArray spawnpointsGameConfig = new JSONArray();
        JSONObject spawnpointSpawnpointsGameConfig = new JSONObject();
        spawnpointSpawnpointsGameConfig.put("x", 10);
        spawnpointSpawnpointsGameConfig.put("y", 10);
        spawnpointSpawnpointsGameConfig.put("z", 10);
        spawnpointSpawnpointsGameConfig.put("direction", 0);
        spawnpointSpawnpointsGameConfig.put("yaw", 0);
        spawnpointSpawnpointsGameConfig.put("pitch", 0);
        spawnpointsGameConfig.put(spawnpointSpawnpointsGameConfig);
        mapConfig.put("spawnpoints", spawnpointsGameConfig);

        JSONObject borderGameConfig = new JSONObject();
        borderGameConfig.put("enable", true);
        borderGameConfig.put("x1", 10);
        borderGameConfig.put("y1", 10);
        borderGameConfig.put("z1", 10);
        borderGameConfig.put("x2", 20);
        borderGameConfig.put("y2", 20);
        borderGameConfig.put("z2", 20);
        mapConfig.put("border", borderGameConfig);

        mapConfig.put("spawnpointBlockedRadius", 10);
        mapConfig.put("time", 900);
        mapConfig.put("enforcepvp", false);

        config.put("exampleworld", mapConfig);

        return config;
    }
}