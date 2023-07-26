package net.jandie1505.combattest.config;

import org.json.JSONArray;
import org.json.JSONObject;

public final class DefaultConfigValues {
    private DefaultConfigValues() {}

    public static JSONObject getGeneralConfig() {
        JSONObject config = new JSONObject();

        config.put("permissionsPrefix", "combattest");
        config.put("singleServerMode", true);
        config.put("autostartNewGame", false);

        JSONObject lobbyConfig = new JSONObject();

        JSONObject spawnpointLobbyConfig = new JSONObject();
        spawnpointLobbyConfig.put("x", 0);
        spawnpointLobbyConfig.put("y", 0);
        spawnpointLobbyConfig.put("z", 0);
        spawnpointLobbyConfig.put("yaw", 0.0);
        spawnpointLobbyConfig.put("pitch", 0.0);
        lobbyConfig.put("spawnpoint", spawnpointLobbyConfig);

        JSONObject borderGameConfig = new JSONObject();
        borderGameConfig.put("enable", true);
        borderGameConfig.put("x1", -10);
        borderGameConfig.put("y1", -10);
        borderGameConfig.put("z1", -10);
        borderGameConfig.put("x2", 10);
        borderGameConfig.put("y2", 10);
        borderGameConfig.put("z2", 10);
        lobbyConfig.put("border", borderGameConfig);

        lobbyConfig.put("time", 90);
        lobbyConfig.put("requiredPlayers", 2);
        lobbyConfig.put("mapVoting", true);
        lobbyConfig.put("teamSelection", true);

        config.put("lobby", lobbyConfig);

        JSONObject cloudSystemConfig = new JSONObject();

        cloudSystemConfig.put("enable", false);
        cloudSystemConfig.put("switchToIngameCommand", "");

        config.put("cloudSystemMode", cloudSystemConfig);

        JSONObject integrationsConfig = new JSONObject();

        integrationsConfig.put("cloudnet", true);
        integrationsConfig.put("partyandfriends", true);
        integrationsConfig.put("supervanish-premiumvanish", true);
        integrationsConfig.put("playerpoints", true);

        config.put("integrations", integrationsConfig);

        JSONObject rewardsConfig = new JSONObject();

        rewardsConfig.put("playerKill", 20);
        rewardsConfig.put("indirectPlayerKill", 10);
        rewardsConfig.put("upgradePurchased", 5);
        rewardsConfig.put("maxRewardsAmount", 5000);

        config.put("playerPointsRewards", rewardsConfig);

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