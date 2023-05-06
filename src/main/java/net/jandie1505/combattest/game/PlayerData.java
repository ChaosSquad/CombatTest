package net.jandie1505.combattest.game;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private int meleeEquipment;
    private int rangedEquipment;
    private int armorEquipment;
    private boolean alive;
    private int respawntimer;
    private int points;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;

        this.meleeEquipment = 0;
        this.rangedEquipment = 0;
        this.armorEquipment = 0;

        this.alive = false;
        this.respawntimer = 0;

        this.points = 0;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public int getMeleeEquipment() {
        return meleeEquipment;
    }

    public void setMeleeEquipment(int meleeEquipment) {
        this.meleeEquipment = meleeEquipment;
    }

    public int getRangedEquipment() {
        return rangedEquipment;
    }

    public void setRangedEquipment(int rangedEquipment) {
        this.rangedEquipment = rangedEquipment;
    }

    public int getArmorEquipment() {
        return armorEquipment;
    }

    public void setArmorEquipment(int armorEquipment) {
        this.armorEquipment = armorEquipment;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
