package net.jandie1505.combattest.game;

import java.util.Comparator;

public class TeamData {
    private final int id;
    private int kills;
    private int deaths;

    public TeamData(int id) {
        this.id = id;
        this.kills = 0;
        this.deaths = 0;
    }

    public int getId() {
        return id;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public static Comparator<TeamData> getKillsComparator() {
        return Comparator.comparingInt(TeamData::getKills);
    }

    public static Comparator<TeamData> getDeathsComparator() {
        return Comparator.comparingInt(TeamData::getDeaths);
    }

    public static Comparator<TeamData> getKDComparator() {
        return Comparator.comparingDouble(o -> PlayerData.getKD(o.getKills(), o.getDeaths()));
    }
}
