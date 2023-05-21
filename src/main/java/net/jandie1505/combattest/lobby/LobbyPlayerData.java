package net.jandie1505.combattest.lobby;

public class LobbyPlayerData {
    private int team;
    private MapData vote;

    public LobbyPlayerData() {
        this.team = 0;
        this.vote = null;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public MapData getVote() {
        return vote;
    }

    public void setVote(MapData vote) {
        this.vote = vote;
    }
}
