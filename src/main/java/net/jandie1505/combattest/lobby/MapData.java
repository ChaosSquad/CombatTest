package net.jandie1505.combattest.lobby;

import net.jandie1505.combattest.game.Spawnpoint;

import java.util.List;

public class MapData {
    private final String world;
    private final String name;
    private final List<Spawnpoint> spawnpoints;
    private final boolean enableBorder;
    private final int[] border;
    private final int spawnpointBlockedRadius;
    private final int time;
    private final boolean enforcePvp;

    public MapData(String world, String name, List<Spawnpoint> spawnpoints, boolean enableBorder, int[] border, int spawnpointBlockedRadius, int time, boolean enforcePvp) {
        this.world = world;
        this.name = name;
        this.spawnpoints = spawnpoints;
        this.enableBorder = enableBorder;
        this.border = border;
        this.spawnpointBlockedRadius = spawnpointBlockedRadius;
        this.time = time;
        this.enforcePvp = enforcePvp;
    }

    public String getWorld() {
        return world;
    }

    public String getName() {
        return name;
    }

    public List<Spawnpoint> getSpawnpoints() {
        return spawnpoints;
    }

    public boolean isEnableBorder() {
        return enableBorder;
    }

    public int[] getBorder() {
        return border;
    }

    public int getSpawnpointBlockedRadius() {
        return spawnpointBlockedRadius;
    }

    public int getTime() {
        return time;
    }

    public boolean isEnforcePvp() {
        return enforcePvp;
    }
}
