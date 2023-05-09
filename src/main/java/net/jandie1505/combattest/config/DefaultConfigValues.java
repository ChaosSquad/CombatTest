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

        JSONObject gameConfig = new JSONObject();
        gameConfig.put("world", "world");
        JSONArray spawnpointsGameConfig = new JSONArray();
        JSONObject spawnpointSpawnpointsGameConfig = new JSONObject();
        spawnpointSpawnpointsGameConfig.put("x", 10);
        spawnpointSpawnpointsGameConfig.put("y", 10);
        spawnpointSpawnpointsGameConfig.put("z", 10);
        spawnpointSpawnpointsGameConfig.put("direction", 0);
        spawnpointSpawnpointsGameConfig.put("angle", 0);
        spawnpointSpawnpointsGameConfig.put("team", 0);
        spawnpointsGameConfig.put(spawnpointSpawnpointsGameConfig);
        gameConfig.put("spawnpoints", spawnpointsGameConfig);
        JSONObject borderGameConfig = new JSONObject();
        borderGameConfig.put("enable", true);
        borderGameConfig.put("x1", 10);
        borderGameConfig.put("y1", 10);
        borderGameConfig.put("z1", 10);
        borderGameConfig.put("x2", 20);
        borderGameConfig.put("y2", 20);
        borderGameConfig.put("z2", 20);
        gameConfig.put("border", borderGameConfig);
        gameConfig.put("spawnpointBlockedRadius", 10);
        gameConfig.put("time", 900);
        gameConfig.put("enforcepvp", false);
        config.put("gameConfig", gameConfig);

        return config;
    }
}