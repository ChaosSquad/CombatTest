package net.jandie1505.combattest.game;

import org.bukkit.Location;
import org.bukkit.World;

public class Spawnpoint {
    private final int x;
    private final int y;
    private final int z;
    private final int direction;
    private final int angle;
    private final int teamMode;

    public Spawnpoint(int x, int y, int z, int direction, int angle, int teamMode) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.angle = angle;
        this.teamMode = teamMode;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDirection() {
        return direction;
    }

    public int getAngle() {
        return angle;
    }

    public int getTeamMode() {
        return teamMode;
    }

    public Location buildLocation(World world) {
        return new Location(world, x, y, z, direction, angle);
    }
}
