package net.jandie1505.combattest.game;

public class PlayerData {
    private int equipment;
    private boolean alive;
    private int respawntimer;

    public PlayerData() {
        this.equipment = 0;
        this.alive = false;
    }

    public int getEquipment() {
        return equipment;
    }

    public void setEquipment(int equipment) {
        this.equipment = equipment;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getRespawntimer() {
        return respawntimer;
    }

    public void setRespawntimer(int respawntimer) {
        this.respawntimer = respawntimer;
    }
}
